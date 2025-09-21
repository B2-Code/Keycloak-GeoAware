package org.b2code.config;

import java.util.HashMap;
import java.util.Map;

public class InfinispanCacheServerConfig extends MaxmindGeoLiteFileServerConfig {

    @Override
    Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<>(super.getOptions());
        options.put("spi-geoaware-geoip-cache--provider", "infinispan");
        return options;
    }

    @Override
    String cacheConfigFile() {
        return "/geoip-cache-ispn.xml";
    }
}
