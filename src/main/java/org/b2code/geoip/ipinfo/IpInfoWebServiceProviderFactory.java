package org.b2code.geoip.ipinfo;

import com.google.auto.service.AutoService;
import io.ipinfo.api.IPinfo;
import io.ipinfo.api.cache.SimpleCache;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoipProvider;
import org.b2code.geoip.GeoipProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.time.Duration;

@JBossLog
@AutoService(GeoipProviderFactory.class)
public class IpInfoWebServiceProviderFactory extends ServerInfoAwareFactory implements GeoipProviderFactory {

    public static final String PROVIDER_ID = "ipinfo-webservice";

    private IPinfo client;

    @Override
    public GeoipProvider create(KeycloakSession session) {
        log.tracef("Creating new %s", IpInfoWebServiceProvider.class.getSimpleName());
        if (client == null) {
            client = createClient(session);
        }
        return new IpInfoWebServiceProvider(client);
    }

    private IPinfo createClient(KeycloakSession keycloakSession) {
        log.trace("Creating new IpInfo file reader");
        PluginConfigWrapper pluginConfig = new PluginConfigWrapper(keycloakSession.getContext().getRealm());

        return new IPinfo
                .Builder()
                .setToken(pluginConfig.getIpInfoToken())
                .setCache(new SimpleCache(Duration.ofDays(pluginConfig.getIpInfoCacheInDays())))
                .build();
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
