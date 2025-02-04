package org.b2code.authentication;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest
public class UnknownIpAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    private static final String FLOW_NAME = "browser-with-unknown-ip";

    @Override
    @Test
    void testExecution() {
        createFlow();
        Assertions.assertNotNull(realm.admin().flows().getFlow(FLOW_NAME));

        AuthenticationFlowRepresentation authenticationFlowRepresentation = realm.admin().flows().getFlow("test");
        Assertions.assertNotNull(authenticationFlowRepresentation);

        Assertions.assertEquals(0, authenticationFlowRepresentation.getAuthenticationExecutions().size());
    }

    @Override
    void createFlow() {
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(FLOW_NAME);
        flow.setAlias(FLOW_NAME);
        flow.setTopLevel(true);
        flow.setBuiltIn(false);

        try (Response response = realm.admin().flows().createFlow(flow)) {
            Assertions.assertEquals(201, response.getStatus());
        }
    }
}
