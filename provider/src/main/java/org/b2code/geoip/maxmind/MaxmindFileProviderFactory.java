package org.b2code.geoip.maxmind;

import com.google.auto.service.AutoService;
import com.google.common.base.Stopwatch;
import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.io.File;
import java.io.IOException;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MaxmindFileProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "maxmind-file";

    private DatabaseReader reader;

    @Override
    public MaxmindFileProvider create(KeycloakSession keycloakSession) {
        log.tracef("Creating new %s", MaxmindFileProvider.class.getSimpleName());
        if (reader == null) {
            reader = createReader(keycloakSession);
        }
        return new MaxmindFileProvider(keycloakSession, reader);
    }

    private DatabaseReader createReader(KeycloakSession keycloakSession) {
        log.trace("Creating new Maxmind file reader");
        PluginConfigWrapper pluginConfig = new PluginConfigWrapper(keycloakSession.getContext().getRealm());
        String databasePath = pluginConfig.getMaxmindDatabaseFilePath();

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
            newReader = new DatabaseReader.Builder(database).build();
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
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

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
        return PROVIDER_ID;
    }
}
