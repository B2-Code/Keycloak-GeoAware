package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.WebServiceClient;
import org.keycloak.models.KeycloakSession;

public class MaxmindFileAutodownloadProvider extends MaxmindProvider {

    public MaxmindFileAutodownloadProvider(KeycloakSession session, DatabaseReader geoIpProvider) {
        super(session, geoIpProvider);
    }

}
