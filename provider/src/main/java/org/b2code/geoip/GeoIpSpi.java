package org.b2code.geoip;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.geoip.provider.GeoIpProvider;
import org.b2code.geoip.provider.GeoIpProviderFactory;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class GeoIpSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return PluginConstants.PLUGIN_NAME_LOWER_CASE + "-geoip";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return GeoIpProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<GeoIpProvider>> getProviderFactoryClass() {
        return GeoIpProviderFactory.class;
    }
}
