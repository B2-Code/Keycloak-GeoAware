package org.b2code.authentication;

import jakarta.mail.internet.MimeMessage;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownDeviceAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    @Override
    String getAuthenticatorProviderToTest() {
        return "geoaware-device";
    }

    @Test
    void testSendAlwaysDeviceEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Always", "Notification email (device)");

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
    void testSendNeverNewDeviceEmail() {
        setConditionAndAction("Never", "Notification email (device)");

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(0, mailServer.getReceivedMessages().length);
        logout();
    }

    @Test
    void testSendDeviceChangedEmail() throws Exception {
        setConditionAndAction("On device change", "Notification email (device)");

        login();
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        MimeMessage lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        logout();
    }

    @Test
    void testSendEmailStrictONUnknownDevice() throws Exception {
        setConditionAndAction("Unknown device", "Notification email (device)");

        login();
        mailServer.waitForIncomingEmail(1);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        MimeMessage lastReceivedMessage = mailServer.getLastReceivedMessage();
        Assertions.assertEquals("New login alert", lastReceivedMessage.getSubject());
        logout();

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(1, mailServer.getReceivedMessages().length);
        logout();
    }
}
