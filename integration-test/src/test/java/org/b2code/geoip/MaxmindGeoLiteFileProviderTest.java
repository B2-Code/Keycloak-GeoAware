package org.b2code.geoip;

import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.b2code.loginhistory.LoginRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

import java.util.List;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
public class MaxmindGeoLiteFileProviderTest extends MaxmindFileProviderTest {

    @Test
    void testKnownIp1() throws Exception {
        loginFromIp("2.125.160.217");
        List<LoginRecord> loginRecords = getLoginRecords();
        Assertions.assertEquals(1, loginRecords.size());
        GeoIpInfo expected = GeoIpInfo.builder()
                .ip("2.125.160.217")
                .city("Boxford")
                .postalCode("OX1")
                .country("United Kingdom")
                .countryIsoCode("GB")
                .continent("Europe")
                .latitude(51.75)
                .longitude(-1.25)
                .accuracyRadius(100)
                .build();
        Assertions.assertEquals(expected, loginRecords.getFirst().getGeoIpInfo());
    }

    @Test
    void testKnownIp2() throws Exception {
        loginFromIp("216.160.83.58");
        List<LoginRecord> loginRecords = getLoginRecords();
        Assertions.assertEquals(1, loginRecords.size());
        GeoIpInfo expected = GeoIpInfo.builder()
                .ip("216.160.83.58")
                .city("Milton")
                .postalCode("98354")
                .country("United States")
                .countryIsoCode("US")
                .continent("North America")
                .latitude(47.2513)
                .longitude(-122.3149)
                .accuracyRadius(22)
                .build();
        Assertions.assertEquals(expected, loginRecords.getFirst().getGeoIpInfo());
    }

}
