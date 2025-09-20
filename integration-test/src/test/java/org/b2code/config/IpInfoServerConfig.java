package org.b2code.config;

import java.util.HashMap;
import java.util.Map;

public class IpInfoServerConfig extends ServerConfig {

    private static final String PROVIDER_ID = "ipinfo-webservice";

    private static final String TOKEN = System.getenv("IPINFO_TOKEN");

    @Override
    Map<String, String> getOptions() {
        if (TOKEN == null) {
            throw new IllegalStateException("IPINFO_TOKEN must be set as environment variable");
        }
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-geoip--provider", PROVIDER_ID);
        options.put("spi-geoaware-geoip--ipinfo-webservice--token", TOKEN);
        return options;
    }
}
