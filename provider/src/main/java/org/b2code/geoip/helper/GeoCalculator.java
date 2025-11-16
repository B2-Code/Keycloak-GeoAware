package org.b2code.geoip.helper;

import lombok.experimental.UtilityClass;
import org.b2code.geoip.persistence.entity.GeoIpInfo;

@UtilityClass
public class GeoCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MIN_RADIUS_KM = 25.0;

    public static BoundingBox calculateBoundingBox(double latitude, double longitude, double radiusKm) {
        double latRad = Math.toRadians(latitude);

        double latDiff = Math.toDegrees(radiusKm / EARTH_RADIUS_KM);
        double lonDiff = Math.toDegrees(Math.asin(Math.sin(radiusKm / EARTH_RADIUS_KM) / Math.cos(latRad)));

        return new BoundingBox(
                latitude - latDiff,
                latitude + latDiff,
                longitude - lonDiff,
                longitude + lonDiff
        );
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return EARTH_RADIUS_KM * 2 * Math.asin(Math.sqrt(a));
    }

    public static boolean isLocationWithinRadius(GeoIpInfo a, GeoIpInfo b) {
        return isLocationWithinRadius(
                a.getLatitude(), a.getLongitude(), a.getAccuracyRadius(),
                b.getLatitude(), b.getLongitude(), b.getAccuracyRadius()
        );
    }

    public static boolean isLocationWithinRadius(
            double lat1, double lon1, double radius1,
            double lat2, double lon2, double radius2) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        double maxRadius = Math.max(Math.max(radius1, radius2), MIN_RADIUS_KM);
        return distance <= maxRadius;
    }
}
