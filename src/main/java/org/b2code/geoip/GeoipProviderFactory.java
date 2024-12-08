package org.b2code.geoip;

import org.b2code.admin.PluginConfigWrapper;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderFactory;


public interface GeoipProviderFactory extends ProviderFactory<GeoipProvider> {

    static GeoipProvider getProvider(KeycloakSession session) {
        RealmModel realm = session.getContext().getRealm();
        PluginConfigWrapper pluginConfigWrapper = new PluginConfigWrapper(realm);
        String configuredProvider = pluginConfigWrapper.getGeoipDatabaseProvider();
        return session.getProvider(GeoipProvider.class, configuredProvider);
    }
}
