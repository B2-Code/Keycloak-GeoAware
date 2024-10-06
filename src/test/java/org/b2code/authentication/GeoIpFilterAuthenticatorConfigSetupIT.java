package org.b2code.authentication;

import org.b2code.IntegrationTestBase;
import org.b2code.KeycloakTestContainer;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoIpFilterAuthenticatorConfigSetupIT extends IntegrationTestBase {

    @Test
    void testAuthenticatorUpdate() {
        GeoIpFilterAuthenticatorConfigTestHelper authenticatorConfig = new GeoIpFilterAuthenticatorConfigTestHelper(keycloak.getKeycloakAdminClient());
        String newIpRange = "1.3.3.7/42";
        authenticatorConfig.setAllowedIpRange(newIpRange);

        AuthenticationManagementResource flowResource = keycloak.getKeycloakAdminClient()
                .realms()
                .realm(KeycloakTestContainer.TEST_REALM)
                .flows();
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(GeoIpFilterAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(GeoIpFilterAuthenticatorConfig.ALLOWED_IP_RANGE).equals(newIpRange)));
    }

}
