package org.b2code.geoip.mock;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.geoip.GeoIpProvider;
import org.b2code.geoip.GeoIpProviderFactory;
import org.b2code.geoip.ipinfo.IpInfoProviderFactory;
import org.b2code.geoip.maxmind.MaxmindFileAutodownloadProviderFactory;
import org.b2code.geoip.maxmind.MaxmindFileProviderFactory;
import org.b2code.geoip.maxmind.MaxmindWebServiceProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.quarkus.runtime.Environment;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MockGeoipProviderFactory implements GeoIpProviderFactory {

    private static final String PROVIDER_ID = "mock";
    private static final String PROVIDER_IMPL_NAME_CONFIG_PARM = "provider";

    private Config.Scope config;
    private GeoIpProvider provider;

    @Override
    public GeoIpProvider create(KeycloakSession keycloakSession) {
        if (!Environment.isDevMode()) {
            throw new IllegalStateException("The 'mock' GeoIP provider can only be used in dev mode");
        }
        if (provider == null) {
            log.warnf("Using '%s' GeoIP provider. This should only be used for development and testing purposes.", PROVIDER_ID);
            String providerName = config.get(PROVIDER_IMPL_NAME_CONFIG_PARM);
            log.infof("Using provider: %s", providerName);
            GeoIpProviderFactory factory = getProviderFactory(providerName);
            if (factory != null) {
                factory.init(Config.scope("geoaware-geoip--" + providerName));
                factory.postInit(keycloakSession.getKeycloakSessionFactory());
                provider = factory.create(keycloakSession);
            }
        }
        String mockIp = keycloakSession.getContext().getRealm().getAttribute(PluginConstants.PLUGIN_NAME_LOWER_CASE + "-mock-ip");
        if (mockIp != null) {
            log.infof("Using mock IP address: %s", mockIp);
        }
        return new DelegatingGeoIpProvider(provider, mockIp);
    }

    private GeoIpProviderFactory getProviderFactory(String name) {
        switch (name) {
            case MaxmindFileProviderFactory.PROVIDER_ID -> {
                return new MaxmindFileProviderFactory();
            }
            case MaxmindWebServiceProviderFactory.PROVIDER_ID -> {
                return new MaxmindWebServiceProviderFactory();
            }
            case MaxmindFileAutodownloadProviderFactory.PROVIDER_ID -> {
                return new MaxmindFileAutodownloadProviderFactory();
            }
            case IpInfoProviderFactory.PROVIDER_ID -> {
                return new IpInfoProviderFactory();
            }
            default -> {
                log.warn("Unknown GeoIP provider factory: " + name);
                return null;
            }
        }
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
