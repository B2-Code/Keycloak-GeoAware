package org.b2code.authentication;

import org.b2code.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnknownIPAuthenticatorConfigSetupIT extends IntegrationTestBase {

    @Test
    void testAuthenticatorDefaultConfig() {
        AuthenticationManagementResource flowResource = realm.flows();
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(UnknownIPAuthenticatorConfig.EMAIL_MODUS).equals(NotificationMode.ALWAYS.getLabel())));
    }

    @Test
    void testAuthenticatorUpdateToUnknownIP() {
        UnknownIPAuthenticatorConfigTestHelper authenticatorConfig = new UnknownIPAuthenticatorConfigTestHelper(keycloak.getKeycloakAdminClient());
        String emailModus = NotificationMode.UNKNOWN_IP.getLabel();
        authenticatorConfig.setEmailModus(emailModus);

        AuthenticationManagementResource flowResource = realm.flows();
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(UnknownIPAuthenticatorConfig.EMAIL_MODUS).equals(emailModus)));
    }

    @Test
    void testAuthenticatorUpdateToOnChange() {
        UnknownIPAuthenticatorConfigTestHelper authenticatorConfig = new UnknownIPAuthenticatorConfigTestHelper(keycloak.getKeycloakAdminClient());
        String emailModus = NotificationMode.ON_CHANGE.getLabel();
        authenticatorConfig.setEmailModus(emailModus);

        AuthenticationManagementResource flowResource = realm.flows();
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(UnknownIPAuthenticatorConfig.EMAIL_MODUS).equals(emailModus)));
    }
}
