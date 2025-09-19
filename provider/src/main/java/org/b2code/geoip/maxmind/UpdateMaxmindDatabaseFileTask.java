package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.b2code.PluginConstants;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpProvider;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.quarkus.runtime.Environment;
import org.keycloak.timer.ScheduledTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@JBossLog
public class UpdateMaxmindDatabaseFileTask implements ScheduledTask {

    public static final String TASK_NAME = UpdateMaxmindDatabaseFileTask.class.getSimpleName();
    private static final String MAXMIND_DB_URL = "https://download.maxmind.com/geoip/databases/GeoLite2-City/download?suffix=tar.gz";
    private static final String MMDB_FILE_SUFFIX = ".mmdb";
    private static final Path DB_PATH = Path.of(Environment.getDataDir(), PluginConstants.PLUGIN_NAME_LOWER_CASE, "maxmind_db" + MMDB_FILE_SUFFIX);

    @Override
    public void run(KeycloakSession session) {
        ProviderFactory<?> factory = session.getKeycloakSessionFactory()
                .getProviderFactory(GeoIpProvider.class, MaxmindFileAutodownloadProviderFactory.PROVIDER_ID);

        if (factory instanceof MaxmindFileAutodownloadProviderFactory maxmindFactory) {
            maxmindFactory.setReader(getUpdatedDatabaseReader(session, maxmindFactory.getReader()));
        } else {
            log.warnf("Could not detect Factory. Expected MaxmindFileAutodownloadProviderFactory, but got %s", factory.getClass().getSimpleName());
        }
    }

    public DatabaseReader getUpdatedDatabaseReader(KeycloakSession session, DatabaseReader currentReader) {
        log.info("Checking for Maxmind database updates");
        PluginConfigWrapper config = PluginConfigWrapper.of(session);
        String accountId = String.valueOf(config.getMaxmindAccountId());
        String licenseKey = config.getMaxmindLicenseKey();

        if (currentReader == null || isUpdateAvailable(session, currentReader, accountId, licenseKey)) {
            log.info("Performing Maxmind database update");
            return updateDatabase(session, accountId, licenseKey).orElse(currentReader);
        }

        log.info("Maxmind database is up to date");
        return currentReader;
    }

    private boolean isUpdateAvailable(KeycloakSession session, DatabaseReader reader, String accountId, String licenseKey) {
        try {
            SimpleHttp.Response response = SimpleHttp.doHead(MAXMIND_DB_URL, session)
                    .authBasic(accountId, licenseKey)
                    .asResponse();

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
            Date currentDate = reader.getMetadata().getBuildDate();
            return remoteDate != null && remoteDate.after(currentDate);
        } catch (IOException e) {
            log.error("Failed to check for database update", e);
            return false;
        }
    }

    private Optional<DatabaseReader> updateDatabase(KeycloakSession session, String accountId, String licenseKey) {
        try {
            Path tempFile = Files.createTempFile("maxmind_db_", ".tar.gz");
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
            HttpGet request = new HttpGet(MAXMIND_DB_URL);
            String auth = Base64.getEncoder().encodeToString((accountId + ":" + licenseKey).getBytes());
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);

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
                if (tarInput.getCurrentEntry().getName().endsWith(MMDB_FILE_SUFFIX)) {
                    Files.createDirectories(DB_PATH.getParent());
                    Files.copy(tarInput, DB_PATH, StandardCopyOption.REPLACE_EXISTING);
                    return new DatabaseReader.Builder(DB_PATH.toFile()).build();
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