package org.b2code.geoip;

import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.b2code.loginhistory.LoginRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;

@Slf4j
abstract class BaseGeoIpProviderTest extends BaseTest {

    @Test
    void testLoginHistoryIsCreated() throws Exception {
        login();

        UserRepresentation userRep = realm.admin().users().get(user.getId()).toRepresentation();
        Assertions.assertNotNull(userRep);

        Map<String, List<String>> attributes = userRep.getAttributes();
        Assertions.assertNotNull(attributes);

        List<String> ipAddresses = attributes.get("loginHistoryRecord");
        Assertions.assertNotNull(ipAddresses);
        Assertions.assertEquals(1, ipAddresses.size());
        LoginRecord loginRecord = getObjectMapper().readValue(ipAddresses.getFirst(), LoginRecord.class);

        Assertions.assertNotNull(loginRecord);
        Assertions.assertNotNull(loginRecord.getIp());
        Assertions.assertTrue(loginRecord.getIp().equals("127.0.0.1") || loginRecord.getIp().equals("0:0:0:0:0:0:0:1"));
    }

    @Test
    void testLoginHistoryMaxLength() throws Exception {
        for (int i = 0; i < 10; i++) {
            login();
            logout();
        }
        List<LoginRecord> ipAddresses = getLoginRecords();
        Assertions.assertEquals(5, ipAddresses.size());
    }

}