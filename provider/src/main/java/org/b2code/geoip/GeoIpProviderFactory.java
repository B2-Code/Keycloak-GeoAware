package org.b2code.geoip;

import org.b2code.admin.PluginConfigWrapper;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderFactory;


public interface GeoIpProviderFactory extends ProviderFactory<GeoIpProvider> {

    static GeoIpProvider getProvider(KeycloakSession session) {
        PluginConfigWrapper pluginConfigWrapper = PluginConfigWrapper.of(session);
        String configuredProvider = pluginConfigWrapper.getGeoIpDatabaseProvider();
        return session.getProvider(GeoIpProvider.class, configuredProvider);
    }
}
