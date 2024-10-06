package org.b2code.geoip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GeoIpInfo {
    private String ip;
    private String city;
    private String postalCode;
    private String country;
    private String countryIsoCode;
    private String continent;
    private int confidence;
}
