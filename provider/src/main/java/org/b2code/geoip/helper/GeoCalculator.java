package org.b2code.geoip.helper;

import lombok.experimental.UtilityClass;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Envelope;

@UtilityClass
public class GeoCalculator {

    private static final double FALLBACK_RADIUS_KM = 50;

    public static BoundingBox calculateBoundingBox(double lat, double lon, Integer radiusKm) {
        Envelope bbox = boundingBox(lat, lon, radiusKm);
        return new BoundingBox(bbox.getMinY(), bbox.getMaxY(), bbox.getMinX(), bbox.getMaxX());
    }

    public static boolean circlesOverlap(GeoIpInfo geoIp1, GeoIpInfo geoIp2) {
        return circlesOverlap(geoIp1.getLatitude(), geoIp1.getLongitude(), geoIp1.getAccuracyRadius(), geoIp2.getLatitude(), geoIp2.getLongitude(), geoIp2.getAccuracyRadius());
    }

    private static boolean circlesOverlap(Double lat1, Double lon1, Integer radiusKm1, Double lat2, Double lon2, Integer radiusKm2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return false;
        }
        double rad1 = (radiusKm1 != null) ? radiusKm1 : FALLBACK_RADIUS_KM;
        double rad2 = (radiusKm2 != null) ? radiusKm2 : FALLBACK_RADIUS_KM;
        GeodeticCalculator calc = new GeodeticCalculator(DefaultGeographicCRS.WGS84);
        calc.setStartingGeographicPoint(lon1, lat1);
        calc.setDestinationGeographicPoint(lon2, lat2);
        double distanceMeters = calc.getOrthodromicDistance();
        return distanceMeters <= (rad1 + rad2) * 1000.0;
    }

    private static Envelope boundingBox(double lat, double lon, Integer radiusKm) {
        double radius = (radiusKm != null) ? radiusKm : FALLBACK_RADIUS_KM;
        double deltaDeg = radius / 111.32;
        return new Envelope(lon - deltaDeg, lon + deltaDeg, lat - deltaDeg, lat + deltaDeg);
    }

}
