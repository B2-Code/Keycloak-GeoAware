package org.b2code.authentication;

import jakarta.mail.internet.MimeMessage;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownIpAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    private static final String PROVIDER_ID = "geoaware-unknown-ip";

    @Override
    AuthenticationExecutionRepresentation getAuthenticatorToTest() {
        AuthenticationExecutionRepresentation authenticatorToTest = new AuthenticationExecutionRepresentation();
        authenticatorToTest.setAuthenticator(PROVIDER_ID);
        authenticatorToTest.setRequirement("REQUIRED");
        return authenticatorToTest;
    }

    @Test
    public void testSendAlwaysIpEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Always", "Notification Email (IP)");

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
        setConditionAndAction("Never", "Notification Email (IP)");

        login();
        mailServer.waitForIncomingEmail(0);
        Assertions.assertEquals(0, mailServer.getReceivedMessages().length);
        logout();
    }

    @Test
    public void testUnknownIpEmail() throws Exception {
        MimeMessage lastReceivedMessage;
        setConditionAndAction("Unknown IP", "Notification Email (IP)");

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
}
