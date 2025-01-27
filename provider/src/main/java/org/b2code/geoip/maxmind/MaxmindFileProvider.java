package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import org.keycloak.models.KeycloakSession;

public class MaxmindFileProvider extends MaxmindProvider {

    public MaxmindFileProvider(KeycloakSession session, DatabaseReader geoIpProvider) {
        super(session, geoIpProvider);
    }

}
