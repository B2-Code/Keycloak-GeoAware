package org.b2code.geoip.ipdata;

import com.google.auto.service.AutoService;
import io.ipdata.client.Ipdata;
import io.ipdata.client.service.IpdataService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpProvider;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.net.URI;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class IpDataProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "ipdata-webservice";

    private IpdataService client;

    @Override
    public GeoIpProvider create(KeycloakSession session) {
        log.tracef("Creating new %s", IpDataProvider.class.getSimpleName());
        if (client == null) {
            client = createClient(session);
        }
        return new IpDataProvider(session, client);
    }

    private IpdataService createClient(KeycloakSession keycloakSession) {
        log.trace("Creating new IpData client");
        PluginConfigWrapper pluginConfig = PluginConfigWrapper.of(keycloakSession);

        try {
            return Ipdata.builder()
                    .url(new URI(pluginConfig.getIpDataApiUrl() ? IpDataDatabase.EU_SERVERS.getLabel() : IpDataDatabase.ALL_SERVERS.getLabel()).toURL())
                    .key(pluginConfig.getIpDataApiKey())
                    .withDefaultCache()
                    .get();
        } catch (Exception e) {
            log.error("Failed to create IpData client", e);
            throw new RuntimeException("Failed to create IpData client", e);
        }
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
