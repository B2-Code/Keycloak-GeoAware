package org.b2code.geoip.provider.ipinfo;

import com.google.auto.service.AutoService;
import io.ipinfo.api.IPinfo;
import io.ipinfo.api.cache.NoCache;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.geoip.provider.GeoIpProvider;
import org.b2code.geoip.provider.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class IpInfoProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "ipinfo-webservice";

    private static final String IPINFO_TOKEN_CONFIG_PARM = "token";

    private Config.Scope config;

    private IPinfo client;

    @Override
    public GeoIpProvider create(KeycloakSession session) {
        log.tracef("Creating new %s", IpInfoProvider.class.getSimpleName());
        return new IpInfoProvider(session, client);
    }

    private IPinfo createClient() {
        log.trace("Creating new IpInfo file reader");
        return new IPinfo
                .Builder()
                .setToken(config.get(IPINFO_TOKEN_CONFIG_PARM))
                .setCache(new NoCache())
                .build();
    }

    @Override
    public void init(Config.Scope config) {
        this.config = config;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        client = createClient();
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
