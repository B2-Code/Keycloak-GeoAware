package org.b2code.authentication;

import jakarta.mail.internet.MimeMessage;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownIpAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    @Override
    String getAuthenticatorProviderToTest() {
        return "geoaware-ip";
    }

    @Test
    public void testSendAlwaysIpEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Always", "Notification email (IP)");

        login();
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();

        login();
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(2, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();
    }

    @Test
    public void testSendNeverNewIpEmail() throws Exception {
        setConditionAndAction("Never", "Notification email (IP)");

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(0, mailServer.getReceivedMessages().length);
        logout();
    }

    @Test
    public void testUnknownIpEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Unknown IP", "Notification email (IP)");

        login();
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        logout();
    }

    @Test
    public void testOnIpChangeEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("On IP change", "Notification email (IP)");

        login();
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();
    }
  
    @Test
    public void testUnknownLocationEmail_01() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Unknown location", "Notification email (IP)");

        loginFromIp("2.125.160.217");
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();

        loginFromIp("2.125.160.217");
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        logout();
    }
    @Test
    public void testUnknownLocationEmail_02() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Unknown location", "Notification email (IP)");

        loginFromIp("2.125.160.217");
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();

        loginFromIp("216.160.83.58");
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(2, mailServer.getReceivedMessages().length);
        lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();
    }

}
