package org.b2code.geoip.database.maxmind;

import com.google.auto.service.AutoService;
import com.google.common.base.Stopwatch;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.geoip.database.GeoipDatabaseAccessProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@JBossLog
@AutoService(GeoipDatabaseAccessProviderFactory.class)
public class MaxmindDatabaseAccessProviderFactory extends ServerInfoAwareFactory implements GeoipDatabaseAccessProviderFactory {

    private static final String DATABASE_PATH_PARAM = "databasePath";

    private static final String CACHE_SIZE_PARAM = "cacheSize";
    private static final int CACHE_SIZE_DEFAULT = 1000;

    private Config.Scope config;

    private DatabaseReader reader;

    @Override
    public MaxmindDatabaseAccessProvider create(KeycloakSession keycloakSession) {
        if (config.get(DATABASE_PATH_PARAM) == null) {
            log.error("Maxmind Database requires database path to be set.");
            return null;
        }
        log.tracef("Creating new %s", MaxmindDatabaseAccessProvider.class.getSimpleName());
        if (reader == null) {
            reader = createReader();
        }
        return new MaxmindDatabaseAccessProvider(reader);
    }

    private DatabaseReader createReader() {
        log.trace("Creating new Maxmind database reader");
        String databasePath = config.get(DATABASE_PATH_PARAM);

        File database;
        try {
            database = new File(databasePath);
        } catch (NullPointerException e) {
            log.errorf("No Maxmind database file found at '%s'", databasePath, e);
            return null;
        }

        DatabaseReader newReader;
        int cacheSize = config.getInt(CACHE_SIZE_PARAM, CACHE_SIZE_DEFAULT);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            newReader = new DatabaseReader.Builder(database).withCache(new CHMCache(cacheSize)).build();
        } catch (IOException e) {
            log.error("Failed to create Maxmind database reader", e);
            return null;
        }

        log.debugf("Maxmind database reader created in %s", stopwatch.stop());
        if (!newReader.getMetadata().getDatabaseType().contains("City")) {
            log.error("Maxmind database is not a City database");
            return null;
        }
        log.infof("Loaded Database '%s' (built at %s)", newReader.getMetadata().getDatabaseType(), newReader.getMetadata().getBuildDate());
        return newReader;
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        if (reader == null) {
            reader = createReader();
        }
    }

    @Override
    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            log.warn("Failed to close Maxmind database reader", e);
        }
    }

    @Override
    public String getId() {
        return "maxmind";
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "dev";
        }
        return Map.of("Version", version);
    }

}
