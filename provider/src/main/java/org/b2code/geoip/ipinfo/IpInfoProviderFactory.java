package org.b2code.geoip.ipinfo;

import com.google.auto.service.AutoService;
import io.ipinfo.api.IPinfo;
import io.ipinfo.api.cache.NoCache;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpProvider;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class IpInfoProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "ipinfo-webservice";

    private IPinfo client;

    @Override
    public GeoIpProvider create(KeycloakSession session) {
        log.tracef("Creating new %s", IpInfoProvider.class.getSimpleName());
        if (client == null) {
            client = createClient(session);
        }
        return new IpInfoProvider(session, client);
    }

    private IPinfo createClient(KeycloakSession keycloakSession) {
        log.trace("Creating new IpInfo file reader");
        PluginConfigWrapper pluginConfig = PluginConfigWrapper.of(keycloakSession);

        return new IPinfo
                .Builder()
                .setToken(pluginConfig.getIpInfoToken())
                .setCache(new NoCache())
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
