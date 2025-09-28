package org.b2code.authentication;

import jakarta.mail.internet.MimeMessage;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownDeviceAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    private static final String PROVIDER_ID = "geoaware-unknown-device";

    @Override
    AuthenticationExecutionRepresentation getAuthenticatorToTest() {
        AuthenticationExecutionRepresentation authenticatorToTest = new AuthenticationExecutionRepresentation();
        authenticatorToTest.setAuthenticator(PROVIDER_ID);
        authenticatorToTest.setRequirement("REQUIRED");
        return authenticatorToTest;
    }

    @Test
    void testSendAlwaysDeviceEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Always", "Notification Email (Device)");

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
        setConditionAndAction("Never", "Notification Email (Device)");

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(0, mailServer.getReceivedMessages().length);
        logout();
    }

    @Test
    void testSendDeviceChangedEmail() throws Exception {
        setConditionAndAction("Device Changed", "Notification Email (Device)");

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
    void testSendEmailOmStrictOnUnknownDevice() throws Exception {
        setConditionAndAction("Unknown Device", "Notification Email (Device)");

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
