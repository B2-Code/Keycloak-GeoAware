package org.b2code.config;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MaxmindGeoIpFileServerConfig extends ServerConfig {

    private static final String PROVIDER_ID = "maxmind-file";

    @Override
    Map<String, String> getOptions() {
        // data source: https://github.com/maxmind/MaxMind-DB/blob/main/source-data/GeoIP2-City-Test.json
        String path = Paths.get("src", "test", "resources", "GeoIP2-City-Test.mmdb").toFile().getAbsolutePath();
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-global-geoip-provider", PROVIDER_ID);
        options.put("spi-geoaware-global-maxmind-database-path", path);
        return options;
    }

}
