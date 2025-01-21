package org.b2code.geoip.maxmind;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaxmindDatabase {

    GEO_LITE("GeoLite2"),
    GEO_IP("GeoIP2"),
    SANDBOX("Sandbox");

    private final String label;

    public static MaxmindDatabase fromLabel(String label) {
        for (MaxmindDatabase database : values()) {
            if (database.label.equalsIgnoreCase(label)) {
                return database;
            }
        }
        throw new IllegalArgumentException("Unknown Maxmind database: " + label);
    }
}
