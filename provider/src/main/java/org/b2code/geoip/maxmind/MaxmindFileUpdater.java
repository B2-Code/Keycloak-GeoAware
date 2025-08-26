package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import lombok.experimental.UtilityClass;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.b2code.admin.PluginConfigWrapper;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@JBossLog
@UtilityClass
public class MaxmindFileUpdater {

    private static final String MAXMIND_DB_URL = "https://download.maxmind.com/geoip/databases/GeoLite2-City/download?suffix=tar.gz";

    public static DatabaseReader getUpdatedDatabaseReader(KeycloakSession session, DatabaseReader currentReader) {
        log.info("Checking for Maxmind database updates");
        PluginConfigWrapper config = PluginConfigWrapper.of(session);
        String accountId = String.valueOf(config.getMaxmindAccountId());
        String licenseKey = config.getMaxmindLicenseKey();

        if (isUpdateAvailable(session, currentReader, accountId, licenseKey)) {
            log.info("Maxmind database update available, performing update");
            return updateDatabase(session, accountId, licenseKey).orElse(currentReader);
        }

        log.info("Maxmind database is up to date, no update needed");
        return currentReader;
    }

    private static boolean isUpdateAvailable(KeycloakSession session, DatabaseReader reader, String accountId, String licenseKey) {
        Date currentDbDate = reader.getMetadata().getBuildDate();

        try (SimpleHttp.Response response = SimpleHttp.doHead(MAXMIND_DB_URL, session)
                .authBasic(accountId, licenseKey)
                .asResponse()) {

            String lastModified = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
            if (lastModified == null) {
                log.error("No Last-Modified header found in Maxmind DB response");
                return false;
            }

            Date remoteDbDate = DateUtils.parseDate(lastModified);
            return remoteDbDate != null && remoteDbDate.after(currentDbDate);
        } catch (IOException e) {
            log.error("Failed to check for Maxmind database update", e);
            return false;
        }
    }

    private static Optional<DatabaseReader> updateDatabase(KeycloakSession session, String accountId, String licenseKey) {
        try {
            Path tempFile = Files.createTempFile("maxmind_db_", ".tar.gz");

            if (downloadDatabase(session, accountId, licenseKey, tempFile)) {
                DatabaseReader reader = extractDatabase(tempFile);
                Files.deleteIfExists(tempFile);
                return Optional.of(reader);
            }
        } catch (IOException e) {
            log.error("Error updating MaxMind database", e);
        }
        return Optional.empty();
    }

    private static boolean downloadDatabase(KeycloakSession session, String accountId, String licenseKey, Path targetFile) {
        log.info("Downloading MaxMind database");
        CloseableHttpClient client = session.getProvider(HttpClientProvider.class).getHttpClient();

        HttpGet request = new HttpGet(MAXMIND_DB_URL);
        String authString = accountId + ":" + licenseKey;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
        request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);

        try (CloseableHttpResponse response = client.execute(request);
             InputStream content = response.getEntity().getContent()) {

            Files.copy(content, targetFile);
            log.infof("Downloaded database to: %s", targetFile);
            return true;
        } catch (IOException e) {
            log.error("Failed to download MaxMind database", e);
            return false;
        }
    }

    private static DatabaseReader extractDatabase(Path archivePath) throws IOException {
        log.info("Extracting MaxMind database from archive");
        try (InputStream fileInput = Files.newInputStream(archivePath);
             GZIPInputStream gzipInput = new GZIPInputStream(fileInput);
             TarArchiveInputStream tarInput = new TarArchiveInputStream(gzipInput)) {

            tarInput.getNextEntry();
            DatabaseReader newReader = new DatabaseReader.Builder(tarInput).build();

            log.infof("Loaded new database '%s' (built at %s)",
                    newReader.getMetadata().getDatabaseType(),
                    newReader.getMetadata().getBuildDate());

            return newReader;
        } catch (IOException e) {
            log.error("No valid file found in MaxMind database archive", e);
            throw e;
        }
    }
}