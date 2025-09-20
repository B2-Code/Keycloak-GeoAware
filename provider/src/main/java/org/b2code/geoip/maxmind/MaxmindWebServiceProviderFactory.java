package org.b2code.geoip.maxmind;

import com.google.auto.service.AutoService;
import com.maxmind.geoip2.GeoIp2Provider;
import com.maxmind.geoip2.WebServiceClient;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.models.KeycloakSession;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MaxmindWebServiceProviderFactory extends MaxmindProviderFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "maxmind-webservice";

    private static final String MAXMIND_WEB_HOSTNAME_CONFIG_PARM = "webHost";

    @Override
    public MaxmindProvider create(KeycloakSession keycloakSession) {
        log.tracef("Creating new %s", MaxmindProvider.class.getSimpleName());
        return new MaxmindProvider(keycloakSession, reader);
    }

    @Override
    public GeoIp2Provider createReader() {
        return new WebServiceClient
                .Builder(getMaxmindAccountId(), getMaxmindLicenseKey())
                .host(config.get(MAXMIND_WEB_HOSTNAME_CONFIG_PARM))
                .build();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
