package org.b2code;

import org.b2code.authentication.GeoIpFilterAuthenticatorConfig;
import org.b2code.authentication.GeoIpFilterAuthenticatorConfigTestHelper;
import org.b2code.authentication.GeoIpFilterAuthenticatorFactory;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KeycloakTestContainerSetupIT extends IntegrationTestBase {

    @Test
    void testKeycloakContainerIsRunning() {
        assertTrue(keycloak.isRunning());
    }

    @Test
    void testRealmIsImported() {
        boolean realmExists = keycloak.getKeycloakAdminClient().realms().findAll().stream().anyMatch(r -> r.getRealm().equals(KeycloakTestContainer.TEST_REALM));
        assertTrue(realmExists);
    }

    @Test
    void testUsersAreImported() {
        boolean userExist = keycloak.getKeycloakAdminClient().realm(KeycloakTestContainer.TEST_REALM).users().count() > 0;
        assertTrue(userExist);
    }

    @Test
    void testRealmHasAuthenticatorProvider() {
        boolean authenticatorExists = keycloak.getKeycloakAdminClient()
                .realms()
                .realm(KeycloakTestContainer.TEST_REALM)
                .flows().getAuthenticatorProviders()
                .stream()
                .anyMatch(p -> p.get("id").equals(GeoIpFilterAuthenticatorFactory.PROVIDER_ID));
        assertTrue(authenticatorExists);
    }

    @Test
    void testBrowserFlowIsPresent() {
        List<AuthenticationFlowRepresentation> flows = keycloak.getKeycloakAdminClient()
                .realms()
                .realm(KeycloakTestContainer.TEST_REALM)
                .flows()
                .getFlows();
        assertTrue(flows.stream().anyMatch(f -> f.getAlias().equals("browser-with-geo-ip-block")));
    }

    @Test
    void testAuthenticatorUpdate() {
        GeoIpFilterAuthenticatorConfigTestHelper authenticatorConfig = new GeoIpFilterAuthenticatorConfigTestHelper(keycloak.getKeycloakAdminClient());
        AuthenticationManagementResource flowResource = keycloak.getKeycloakAdminClient()
                .realms()
                .realm(KeycloakTestContainer.TEST_REALM)
                .flows();
        String newIpRange = "1.3.3.7/42";
        authenticatorConfig.setAllowedIpRange(newIpRange);
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(GeoIpFilterAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(GeoIpFilterAuthenticatorConfig.ALLOWED_IP_RANGE).equals(newIpRange)));
    }

}
