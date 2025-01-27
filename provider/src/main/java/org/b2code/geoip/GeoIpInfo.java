package org.b2code.geoip;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoIpInfo {
    private String ip;
    private String city;
    private String postalCode;
    private String country;
    private String countryIsoCode;
    private String continent;
    private Integer confidence;
    private Double latitude;
    private Double longitude;
    private Integer accuracyRadius;

    /**
     * Calculate the distance between two GeoIpInfo objects.
     * @param other the other GeoIpInfo object
     * @return the distance in kilometers
     */
    public double getDistanceTo(GeoIpInfo other) {
        // Calculated using haversine formula
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Check if the accuracy radius of this GeoIpInfo overlaps with the radius of another GeoIpInfo.
     * Can be used to determine if two GeoIpInfo objects are close enough to be considered the same location.
     * @param other the other GeoIpInfo object
     * @return true if the radius overlaps, false otherwise
     */
    public boolean radiusOverlapsWith(GeoIpInfo other) {
        if (this.accuracyRadius == null || other.accuracyRadius == null || this.latitude == null || this.longitude == null || other.latitude == null || other.longitude == null) {
            return false;
        }
        return this.getDistanceTo(other) <= this.accuracyRadius + other.accuracyRadius;
    }
}
