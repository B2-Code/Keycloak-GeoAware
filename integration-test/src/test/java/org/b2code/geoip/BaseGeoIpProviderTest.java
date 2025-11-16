package org.b2code.geoip;

import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
abstract class BaseGeoIpProviderTest extends BaseTest {

    @Test
    void testLoginHistoryIsCreated() throws Exception {
        login();

        List<LoginRecordEntity> loginRecords = getLoginRecords();
        Assertions.assertEquals(1, loginRecords.size());
        LoginRecordEntity loginRecord = loginRecords.getFirst();

        Assertions.assertNotNull(loginRecord);
        Assertions.assertNotNull(loginRecord.getIp());
        Assertions.assertTrue(loginRecord.getIp().equals("127.0.0.1") || loginRecord.getIp().equals("0:0:0:0:0:0:0:1"));
    }


}