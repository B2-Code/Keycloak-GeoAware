package org.b2code.config;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MaxmindGeoLiteFileServerConfig extends ServerConfig {

    private static final String PROVIDER_ID = "maxmind-file";

    @Override
    Map<String, String> getOptions() {
        // data source: https://github.com/maxmind/MaxMind-DB/blob/main/source-data/GeoLite2-City-Test.json
        String path = Paths.get("src", "test", "resources", "GeoLite2-City-Test.mmdb").toFile().getAbsolutePath();
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-geoip--provider", PROVIDER_ID);
        options.put("spi-geoaware-geoip--maxmind-file--db-path", path);
        return options;
    }

}
