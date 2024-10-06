package org.b2code.geoip.database;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class GeoipDatabaseSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "geoip-database";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return GeoipDatabaseAccessProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<GeoipDatabaseAccessProvider>> getProviderFactoryClass() {
        return GeoipDatabaseAccessProviderFactory.class;
    }
}
