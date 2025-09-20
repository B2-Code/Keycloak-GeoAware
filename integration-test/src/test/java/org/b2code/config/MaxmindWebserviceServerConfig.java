package org.b2code.config;

import java.util.HashMap;
import java.util.Map;

public class MaxmindWebserviceServerConfig extends ServerConfig {

    private static final String PROVIDER_ID = "maxmind-webservice";

    private static final String ACCOUNT_ID = System.getenv("MAXMIND_ACCOUNT_ID");

    private static final String LICENSE_KEY = System.getenv("MAXMIND_LICENSE_KEY");

    @Override
    Map<String, String> getOptions() {
        if (ACCOUNT_ID == null || LICENSE_KEY == null) {
            throw new IllegalStateException("MAXMIND_ACCOUNT_ID and MAXMIND_LICENSE_KEY must be set as environment variables");
        }
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-geoip--provider", PROVIDER_ID);
        options.put("spi-geoaware-geoip--maxmind-webservice--account-id", ACCOUNT_ID);
        options.put("spi-geoaware-geoip--maxmind-webservice--license-key", LICENSE_KEY);
        return options;
    }
}
