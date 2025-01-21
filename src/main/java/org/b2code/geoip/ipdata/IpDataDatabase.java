package org.b2code.geoip.ipdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IpDataDatabase {

    ALL_SERVERS("https://api.ipdata.co"),
    EU_SERVERS("https://eu-api.ipdata.co");

    private final String label;

    public static IpDataDatabase fromLabel(String label) {
        for (IpDataDatabase database : values()) {
            if (database.label.equalsIgnoreCase(label)) {
                return database;
            }
        }
        throw new IllegalArgumentException("Unknown IpData database: " + label);
    }
}
