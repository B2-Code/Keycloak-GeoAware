package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.WebServiceClient;
import org.keycloak.models.KeycloakSession;

public class MaxmindWebServiceProvider extends MaxmindProvider {

    public MaxmindWebServiceProvider(KeycloakSession session, WebServiceClient webClient) {
        super(session, webClient);
    }

}
