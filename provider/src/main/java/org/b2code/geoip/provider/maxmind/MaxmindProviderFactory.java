package org.b2code.geoip.provider.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.GeoIp2Provider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.io.FileUtils;
import org.b2code.PluginConstants;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.geoip.provider.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.quarkus.runtime.Environment;

import java.io.File;
import java.io.IOException;

@JBossLog
public abstract class MaxmindProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String MMDB_FILE_EXTENSION = ".mmdb";

    private static final String MAXMIND_ACCOUNT_ID_CONFIG_PARM = "accountId";
    private static final String MAXMIND_LICENSE_KEY_CONFIG_PARM = "licenseKey";

    private static final String DB_PATH_CONFIG_PARM = "dbPath";
    private static final String DB_PATH_DEFAULT = Environment.getDataDir() + File.separator + PluginConstants.PLUGIN_NAME_LOWER_CASE;

    private static final String DB_NAME_CONFIG_PARM = "dbName";

    protected Config.Scope config;

    @Getter
    @Setter
    protected GeoIp2Provider reader;

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        this.reader = createReader();
    }

    public abstract GeoIp2Provider createReader();

    protected Integer getMaxmindAccountId() {
        return config.getInt(MAXMIND_ACCOUNT_ID_CONFIG_PARM);
    }

    protected String getMaxmindLicenseKey() {
        return config.get(MAXMIND_LICENSE_KEY_CONFIG_PARM);
    }

    protected String getDbName() {
        return config.get(DB_NAME_CONFIG_PARM);
    }

    protected String getDbPath() {
        return config.get(DB_PATH_CONFIG_PARM, DB_PATH_DEFAULT);
    }

    protected File findMmdbFile() {
        String dbName = getDbName();
        String dbPath = getDbPath();

        if (dbName != null && !dbName.isBlank()) {
            File dbFile = new File(dbPath, dbName);
            if (dbFile.exists() && dbFile.canRead() && dbFile.getName().endsWith(MMDB_FILE_EXTENSION)) {
                return dbFile;
            }
            log.warnf("Maxmind database file not found or not readable at path: %s", dbFile.getAbsolutePath());
            return null;
        }

        File dir = new File(dbPath);
        if (!dir.isDirectory()) {
            return null;
        }

        File foundFile = FileUtils.listFiles(dir, new String[]{MMDB_FILE_EXTENSION.substring(1)}, false).stream().findFirst().orElse(null);
        if (foundFile == null) {
            log.warnf("No Maxmind database file found in directory: %s", dir.getAbsolutePath());
        }
        return foundFile;
    }

    @Override
    public void close() {
        try {
            if (this.reader != null) {
                if (this.reader instanceof DatabaseReader databaseReader) {
                    databaseReader.close();
                }
            }
        } catch (IOException e) {
            log.warn("Failed to close Maxmind database reader", e);
        }
    }

}
