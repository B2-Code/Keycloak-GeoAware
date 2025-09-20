package org.b2code.geoip.maxmind;

import com.google.auto.service.AutoService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.GeoIp2Provider;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.io.File;
import java.io.IOException;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MaxmindFileProviderFactory extends MaxmindProviderFactory {

    public static final String PROVIDER_ID = "maxmind-file";

    @Override
    public MaxmindProvider create(KeycloakSession keycloakSession) {
        log.tracef("Creating new %s", MaxmindProvider.class.getSimpleName());
        return new MaxmindProvider(keycloakSession, reader);
    }

    @Override
    public GeoIp2Provider createReader() {
        log.trace("Creating new Maxmind file reader");
        String databasePath = getDbPath();

        File database;
        try {
            database = new File(databasePath);
        } catch (NullPointerException e) {
            log.errorf("No Maxmind database file found at '%s'", databasePath, e);
            return null;
        }
        try {
            DatabaseReader newReader = new DatabaseReader.Builder(database).build();
            log.infof("Loaded Database '%s' (built at %s)", newReader.getMetadata().getDatabaseType(), newReader.getMetadata().getBuildDate());
            return newReader;
        } catch (IOException e) {
            log.error("Failed to create Maxmind database reader", e);
            return null;
        }
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
