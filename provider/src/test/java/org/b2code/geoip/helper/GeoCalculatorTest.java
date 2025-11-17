package org.b2code.geoip.helper;

import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoCalculatorTest {

    private static final double EPS = 1e-4;

    @Test
    void calculateBoundingBox_HappyPath() {
        double lat = 52.5200;
        double lon = 13.4050;
        int radiusKm = 10;
        double deltaDeg = radiusKm / 111.32d;

        BoundingBox box = GeoCalculator.calculateBoundingBox(lat, lon, radiusKm);

        assertEquals(lat - deltaDeg, box.getMinLatitude(), EPS);
        assertEquals(lat + deltaDeg, box.getMaxLatitude(), EPS);
        assertEquals(lon - deltaDeg, box.getMinLongitude(), EPS);
        assertEquals(lon + deltaDeg, box.getMaxLongitude(), EPS);
    }

    @Test
    void calculateBoundingBox_FallbackRadius() {
        double lat = 48.1372;
        double lon = 11.5756;
        int fallbackRadiusKm = 25; // internal constant
        double deltaDeg = fallbackRadiusKm / 111.32d;

        BoundingBox box = GeoCalculator.calculateBoundingBox(lat, lon, null);

        assertEquals(lat - deltaDeg, box.getMinLatitude(), EPS);
        assertEquals(lat + deltaDeg, box.getMaxLatitude(), EPS);
        assertEquals(lon - deltaDeg, box.getMinLongitude(), EPS);
        assertEquals(lon + deltaDeg, box.getMaxLongitude(), EPS);
    }

    @Test
    void calculateBoundingBox_RadiusZeroProducesPoint() {
        double lat = 40.7128;
        double lon = -74.0060;
        BoundingBox box = GeoCalculator.calculateBoundingBox(lat, lon, 0);
        assertEquals(lat, box.getMinLatitude(), 1e-9);
        assertEquals(lat, box.getMaxLatitude(), 1e-9);
        assertEquals(lon, box.getMinLongitude(), 1e-9);
        assertEquals(lon, box.getMaxLongitude(), 1e-9);
    }

    @Test
    void circlesOverlap_TrueWithinSumOfRadii() {
        GeoIpInfo a = GeoIpInfo.builder().latitude(52.5200).longitude(13.4050).accuracyRadius(10).ip("1.1.1.1").build();
        GeoIpInfo b = GeoIpInfo.builder().latitude(52.5250).longitude(13.4100).accuracyRadius(10).ip("2.2.2.2").build();
        assertTrue(GeoCalculator.circlesOverlap(a, b));
    }

    @Test
    void circlesOverlap_FalseFarApart() {
        GeoIpInfo berlin = GeoIpInfo.builder().latitude(52.5200).longitude(13.4050).accuracyRadius(10).ip("3.3.3.3").build();
        GeoIpInfo munich = GeoIpInfo.builder().latitude(48.1372).longitude(11.5756).accuracyRadius(10).ip("4.4.4.4").build();
        assertFalse(GeoCalculator.circlesOverlap(berlin, munich));
    }

    @Test
    void circlesOverlap_FallbackOneNullRadius() {
        GeoIpInfo a = GeoIpInfo.builder().latitude(52.5200).longitude(13.4050).accuracyRadius(null).ip("5.5.5.5").build();
        GeoIpInfo b = GeoIpInfo.builder().latitude(52.5400).longitude(13.3000).accuracyRadius(15).ip("6.6.6.6").build();
        assertTrue(GeoCalculator.circlesOverlap(a, b));
    }

    @Test
    void circlesOverlap_FallbackBothNullRadiusNear() {
        GeoIpInfo berlin = GeoIpInfo.builder().latitude(52.5200).longitude(13.4050).accuracyRadius(null).ip("7.7.7.7").build();
        GeoIpInfo potsdam = GeoIpInfo.builder().latitude(52.3906).longitude(13.0645).accuracyRadius(null).ip("8.8.8.8").build();
        assertTrue(GeoCalculator.circlesOverlap(berlin, potsdam));
    }

    @Test
    void circlesOverlap_NullCoordinatesReturnFalse() {
        GeoIpInfo invalid = GeoIpInfo.builder().latitude(null).longitude(8.0).accuracyRadius(10).ip("9.9.9.9").build();
        GeoIpInfo valid = GeoIpInfo.builder().latitude(50.0).longitude(8.0).accuracyRadius(10).ip("10.10.10.10").build();
        assertFalse(GeoCalculator.circlesOverlap(invalid, valid));
    }

    @Test
    void circlesOverlap_JustTouchingBoundary() {
        // radii 10 + 15 = 25 km; choose latitude difference so distance ~25km
        int r1 = 10;
        int r2 = 15;
        double lat1 = 0.0;
        double lon1 = 0.0;
        double lat2 = (r1 + r2) / 111.32d; // ~0.2246 degrees north
        double lon2 = 0.0;
        GeoIpInfo a = GeoIpInfo.builder().latitude(lat1).longitude(lon1).accuracyRadius(r1).ip("11.11.11.11").build();
        GeoIpInfo b = GeoIpInfo.builder().latitude(lat2).longitude(lon2).accuracyRadius(r2).ip("12.12.12.12").build();
        assertTrue(GeoCalculator.circlesOverlap(a, b));
    }

    @Test
    void circlesOverlap_NonOverlapJustBeyondBoundary() {
        int r1 = 8;
        int r2 = 12;
        double sum = r1 + r2; // 20km
        double lat1 = 0.0;
        double lon1 = 0.0;
        // Set distance slightly greater than 20km: add extra 0.002 deg (~0.22km)
        double lat2 = sum / 111.32d + 0.002d;
        double lon2 = 0.0;
        GeoIpInfo a = GeoIpInfo.builder().latitude(lat1).longitude(lon1).accuracyRadius(r1).ip("13.13.13.13").build();
        GeoIpInfo b = GeoIpInfo.builder().latitude(lat2).longitude(lon2).accuracyRadius(r2).ip("14.14.14.14").build();
        assertFalse(GeoCalculator.circlesOverlap(a, b));
    }

    @Test
    void calculateBoundingBox_NegativeCoordinates() {
        double lat = -33.8650; // südliche Hemisphäre
        double lon = -70.0000; // westliche Hemisphäre
        int radiusKm = 12;
        double deltaDeg = radiusKm / 111.32d;
        BoundingBox box = GeoCalculator.calculateBoundingBox(lat, lon, radiusKm);
        assertEquals(lat - deltaDeg, box.getMinLatitude(), EPS);
        assertEquals(lat + deltaDeg, box.getMaxLatitude(), EPS);
        assertEquals(lon - deltaDeg, box.getMinLongitude(), EPS);
        assertEquals(lon + deltaDeg, box.getMaxLongitude(), EPS);
    }
}
