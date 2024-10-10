package org.b2code.geoip.database.maxmind;

import com.google.auto.service.AutoService;
import com.google.common.base.Stopwatch;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.database.GeoipDatabaseAccessProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@JBossLog
@AutoService(GeoipDatabaseAccessProviderFactory.class)
public class MaxmindDatabaseAccessProviderFactory implements GeoipDatabaseAccessProviderFactory, ServerInfoAwareProviderFactory {

    private static final String DATABASE_PATH = "databasePath";

    private Config.Scope config;

    private DatabaseReader reader;

    @Override
    public MaxmindDatabaseAccessProvider create(KeycloakSession keycloakSession) {
        if (config.get(DATABASE_PATH) == null) {
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
        String databasePath = config.get(DATABASE_PATH);

        File database;
        try {
            database = new File(databasePath);
        } catch (NullPointerException e) {
            log.errorf("No Maxmind database file found at '%s'", databasePath, e);
            return null;
        }

        DatabaseReader newReader;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            newReader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
        } catch (IOException e) {
            log.error("Failed to create Maxmind database reader", e);
            return null;
        }

        log.debugf("Maxmind database reader created in %s", stopwatch.stop());
        if (!newReader.getMetadata().getDatabaseType().contains("City")) {
            log.error("Maxmind database is not a City database");
            return null;
        }
        log.debugf("Loaded Database '%s' (built at %s)", newReader.getMetadata().getDatabaseType(), newReader.getMetadata().getBuildDate());
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
