package org.b2code.geoip.maxmind;

import com.google.auto.service.AutoService;
import com.maxmind.geoip2.WebServiceClient;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MaxmindWebServiceProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "maxmind-webservice";

    private WebServiceClient client;

    @Override
    public MaxmindWebServiceProvider create(KeycloakSession keycloakSession) {
        log.tracef("Creating new %s", MaxmindFileProvider.class.getSimpleName());
        if (client == null) {
            client = createClient(keycloakSession);
        }
        return new MaxmindWebServiceProvider(keycloakSession, client);
    }

    private WebServiceClient createClient(KeycloakSession keycloakSession) {
        log.trace("Creating new Maxmind file reader");
        PluginConfigWrapper pluginConfig = PluginConfigWrapper.of(keycloakSession);

        return new WebServiceClient
                .Builder(pluginConfig.getMaxmindAccountId(), pluginConfig.getMaxmindLicenseKey())
                .host(getHost(pluginConfig.getMaxmindWebDatabase()))
                .build();
    }

    private String getHost(MaxmindDatabase database) {
        return switch (database) {
            case GEO_IP -> "geoip.maxmind.com";
            case GEO_LITE -> "geolite.info";
            case SANDBOX -> "sandbox.maxmind.com";
        };
    }

    @Override
    public void init(Config.Scope scope) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
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
