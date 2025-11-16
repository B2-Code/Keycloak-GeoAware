package org.b2code.geoip.provider.maxmind;

import com.google.auto.service.AutoService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.GeoIp2Provider;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.provider.GeoIpProviderFactory;
import org.keycloak.models.KeycloakSession;

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
        File database = findMmdbFile();
        if (database == null || !database.exists() || !database.canRead()) {
            log.errorf("Maxmind database file not found or not readable at path: %s", getDbPath());
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
