package org.b2code.config;

import java.util.HashMap;
import java.util.Map;

public class UnknownDeviceServerConfig extends ServerConfig {

    private static final String PROVIDER_ID = "geoaware-unknown-device";

    @Override
    Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("spi-authenticator", PROVIDER_ID);
        return options;
    }
}
