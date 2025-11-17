package org.b2code.geoip.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class GeoIpInfo {

    @Column(name = "IP_ADDRESS", length = 45, nullable = false)
    private String ip;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "POSTAL_CODE", length = 20)
    private String postalCode;

    @Column(name = "COUNTRY", length = 100)
    private String country;

    @Column(name = "COUNTRY_ISO_CODE", length = 2)
    private String countryIsoCode;

    @Column(name = "CONTINENT", length = 20)
    private String continent;

    @Column(name = "LATITUDE", precision = 10)
    private Double latitude;

    @Column(name = "LONGITUDE", precision = 10)
    private Double longitude;

    @Column(name = "ACCURACY_RADIUS")
    private Integer accuracyRadius;

}
