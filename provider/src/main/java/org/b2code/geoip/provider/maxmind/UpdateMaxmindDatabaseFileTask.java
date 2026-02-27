package org.b2code.geoip.provider.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.platform.Platform;
import org.keycloak.timer.ScheduledTask;
import org.keycloak.utils.KeycloakSessionUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@RequiredArgsConstructor
@JBossLog
public class UpdateMaxmindDatabaseFileTask implements ScheduledTask {

    public static final String TASK_NAME = UpdateMaxmindDatabaseFileTask.class.getSimpleName();

    private static final String MMDB_FILE_NAME = "maxmind-db";

    private final MaxmindFileAutodownloadProviderFactory factory;

    @Override
    public void run(KeycloakSession session) {
        factory.setReader(getUpdatedDatabaseReader(session, factory.createReader()));
    }

    public DatabaseReader getReader() {
        return getUpdatedDatabaseReader(KeycloakSessionUtil.getKeycloakSession(), null);
    }

    public DatabaseReader getUpdatedDatabaseReader(KeycloakSession session, DatabaseReader currentReader) {
        log.info("Checking for Maxmind database updates");
        String accountId = factory.getMaxmindAccountId().toString();
        String licenseKey = factory.getMaxmindLicenseKey();

        if (currentReader == null || isUpdateAvailable(session, currentReader, accountId, licenseKey)) {
            log.info("Performing Maxmind database update");
            return updateDatabase(session, accountId, licenseKey).orElse(currentReader);
        }

        log.info("Maxmind database is up to date");
        return currentReader;
    }

    private boolean isUpdateAvailable(KeycloakSession session, DatabaseReader reader, String accountId, String licenseKey) {
        SimpleHttp httpHeadReq = SimpleHttp.doHead(factory.getMaxmindDbDownloadUrl(), session);
        httpHeadReq.authBasic(accountId, licenseKey);
        try (SimpleHttp.Response response = httpHeadReq.asResponse()) {
            if (response.getStatus() != 200) {
                log.errorf("Update check failed, status: %d", response.getStatus());
                return false;
            }

            String lastModified = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
            if (lastModified == null) {
                log.error("No Last-Modified header in response");
                return false;
            }

            Date remoteDate = DateUtils.parseDate(lastModified);
            Date currentDate = new Date(reader.metadata().buildEpoch().longValue() * 1000);
            return remoteDate != null && remoteDate.after(currentDate);
        } catch (IOException e) {
            log.error("Failed to check for database update", e);
            return false;
        }
    }

    private Optional<DatabaseReader> updateDatabase(KeycloakSession session, String accountId, String licenseKey) {
        try {
            File tempDir = Platform.getPlatform().getTmpDirectory();
            if (!tempDir.exists()) {
                if (!tempDir.mkdirs()) {
                    log.errorf("Temporary directory %s does not exist and could not be created.", tempDir.getAbsolutePath());
                    return Optional.empty();
                }
            }
            if (!tempDir.canWrite()) {
                log.errorf("Temporary directory %s is not writable.", tempDir.getAbsolutePath());
                return Optional.empty();
            }
            Path tempFile = Files.createTempFile(tempDir.toPath(), MMDB_FILE_NAME, MaxmindProviderFactory.MMDB_FILE_EXTENSION);
            if (downloadDatabase(session, accountId, licenseKey, tempFile)) {
                DatabaseReader reader = extractDatabase(tempFile);
                Files.delete(tempFile);
                return Optional.of(reader);
            }
        } catch (IOException e) {
            log.error("Error updating database", e);
        }
        return Optional.empty();
    }

    private boolean downloadDatabase(KeycloakSession session, String accountId, String licenseKey, Path targetFile) {
        log.info("Downloading MaxMind database");
        CloseableHttpClient client = session.getProvider(HttpClientProvider.class).getHttpClient();

        try {
            // Initial request to get redirect URL
            HttpGet request = new HttpGet(factory.getMaxmindDbDownloadUrl());
            String auth = Base64.getEncoder().encodeToString((accountId + ":" + licenseKey).getBytes());
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);
            request.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());

            try (CloseableHttpResponse response = client.execute(request)) {
                int status = response.getStatusLine().getStatusCode();

                if (status != 301 && status != 302) {
                    log.errorf("Expected redirect, got: %d", status);
                    if (status == 429) {
                        log.error("Rate limit exceeded");
                    }
                    return false;
                }

                String redirectUrl = response.getFirstHeader(HttpHeaders.LOCATION).getValue();
                return downloadFile(client, redirectUrl, targetFile);
            }
        } catch (IOException e) {
            log.error("Download failed", e);
            return false;
        }
    }

    private boolean downloadFile(CloseableHttpClient client, String url, Path targetFile) throws IOException {
        try (CloseableHttpResponse response = client.execute(new HttpGet(url));
             InputStream content = response.getEntity().getContent()) {

            if (response.getStatusLine().getStatusCode() != 200) {
                log.errorf("Download failed, status: %d", response.getStatusLine().getStatusCode());
                return false;
            }

            Files.copy(content, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
    }

    private DatabaseReader extractDatabase(Path archivePath) throws IOException {
        log.info("Extracting database from archive");

        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(
                new GZIPInputStream(Files.newInputStream(archivePath)))) {

            // Find the .mmdb file in the archive
            while (tarInput.getNextEntry() != null) {
                if (tarInput.getCurrentEntry().getName().endsWith(MaxmindProviderFactory.MMDB_FILE_EXTENSION)) {
                    Path dbPath = Path.of(factory.getDbPath() + File.separator + MMDB_FILE_NAME + MaxmindProviderFactory.MMDB_FILE_EXTENSION);
                    Files.createDirectories(dbPath.getParent());
                    Files.copy(tarInput, dbPath, StandardCopyOption.REPLACE_EXISTING);
                    return new DatabaseReader.Builder(dbPath.toFile()).build();
                }
            }

            throw new IOException("No .mmdb file found in archive");
        }
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }
}