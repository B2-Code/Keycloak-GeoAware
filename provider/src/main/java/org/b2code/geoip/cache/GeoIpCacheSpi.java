package org.b2code.geoip.cache;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class GeoIpCacheSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return PluginConstants.PLUGIN_NAME_LOWER_CASE + "-geoip-cache";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return GeoIpCacheProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<GeoIpCacheProvider>> getProviderFactoryClass() {
        return GeoIpCacheProviderFactory.class;
    }

}
