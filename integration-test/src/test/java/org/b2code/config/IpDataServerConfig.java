package org.b2code.config;

import java.util.HashMap;
import java.util.Map;

public class IpDataServerConfig extends ServerConfig {

    private static final String PROVIDER_ID = "ipdata-webservice";

    private static final String APIKEY = System.getenv("IPDATA_API_KEY");

    @Override
    Map<String, String> getOptions() {
        if (APIKEY == null) {
            throw new IllegalStateException("IPDATA_API_KEY must be set as environment variable");
        }
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-global-geoip-provider", PROVIDER_ID);
        options.put("spi-geoaware-global-ipdata-api-key", APIKEY);
        return options;
    }
}
