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
        boolean realmExists = client.realms().findAll().stream().anyMatch(r -> r.getRealm().equals(TEST_REALM));
        assertTrue(realmExists);
    }

    @Test
    void testUsersAreImported() {
        boolean userExist = realm.users().count() > 0;
        assertTrue(userExist);
    }

    @Test
    void testRealmHasAuthenticatorProvider() {
        boolean authenticatorExists = client
                .realms()
                .realm(TEST_REALM)
                .flows().getAuthenticatorProviders()
                .stream()
                .anyMatch(p -> p.get("id").equals(GeoIpFilterAuthenticatorFactory.PROVIDER_ID));
        assertTrue(authenticatorExists);
    }

    @Test
    void testBrowserFlowIsPresent() {
        List<AuthenticationFlowRepresentation> flows = client
                .realms()
                .realm(TEST_REALM)
                .flows()
                .getFlows();
        assertTrue(flows.stream().anyMatch(f -> f.getAlias().equals("browser-with-geo-ip-block")));
    }

}
