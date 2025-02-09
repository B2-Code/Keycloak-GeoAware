package org.b2code.geoip;

import org.b2code.config.MaxmindGeoIpFileServerConfig;
import org.b2code.loginhistory.LoginRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

import java.util.List;

@KeycloakIntegrationTest(config = MaxmindGeoIpFileServerConfig.class)
class MaxmindGeoIpFileProviderTest extends MaxmindFileProviderTest {

    @Test
    void testKnownIp1() throws Exception {
        loginFromIp("214.78.123.123");
        List<LoginRecord> loginRecords = getLoginRecords();
        Assertions.assertEquals(1, loginRecords.size());
        GeoIpInfo expected = GeoIpInfo.builder()
                .ip("214.78.123.123")
                .city("San Diego")
                .postalCode("92105")
                .country("United States")
                .countryIsoCode("US")
                .continent("North America")
                .latitude(32.7405)
                .longitude(-117.0935)
                .accuracyRadius(100)
                .build();
        Assertions.assertEquals(expected, loginRecords.getFirst().getGeoIpInfo());
    }

    @Test
    void testKnownIp2() throws Exception {
        loginFromIp("2001:480:10::");
        List<LoginRecord> loginRecords = getLoginRecords();
        Assertions.assertEquals(1, loginRecords.size());
        GeoIpInfo expected = GeoIpInfo.builder()
                .ip("2001:480:10::")
                .city("San Diego")
                .postalCode("92101")
                .country("United States")
                .countryIsoCode("US")
                .continent("North America")
                .latitude(32.7203)
                .longitude(-117.1552)
                .accuracyRadius(20)
                .build();
        Assertions.assertEquals(expected, loginRecords.getFirst().getGeoIpInfo());
    }

}
