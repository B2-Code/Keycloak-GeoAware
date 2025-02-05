package org.b2code.authentication;

import jakarta.ws.rs.core.Response;
import org.apache.maven.lifecycle.internal.LifecycleStarter;
import org.b2code.config.UnknownDeviceServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationExecutionExportRepresentation;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

import java.util.ArrayList;
import java.util.List;

@KeycloakIntegrationTest(config = UnknownDeviceServerConfig.class)
public class UnknownDeviceAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    private static final String FLOW_NAME = "browser-with-unknown-device";
    private static final String PROVIDER_ID = "geoaware-unknown-device";

    @Override
    @Test
    void testCreateFlowWithExecution() {
        createFlow();
        Assertions.assertNotNull(realm.admin().flows().getFlow(FLOW_NAME));

        AuthenticationFlowRepresentation authenticationFlowRepresentation = realm.admin().flows().getFlow(FLOW_NAME);
        Assertions.assertNotNull(authenticationFlowRepresentation);

        Assertions.assertEquals(0, authenticationFlowRepresentation.getAuthenticationExecutions().size());

        authenticationFlowRepresentation.setAuthenticationExecutions(createExecutions());

        Assertions.assertEquals(1, authenticationFlowRepresentation.getAuthenticationExecutions().size());

    }

    @Override
    void createFlow() {
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(FLOW_NAME);
        flow.setAlias(FLOW_NAME);
        flow.setProviderId(FLOW_NAME);
        flow.setTopLevel(true);
        flow.setBuiltIn(false);

        try (Response response = realm.admin().flows().createFlow(flow)) {
            Assertions.assertEquals(201, response.getStatus());
        }
    }

    @Override
    List<AuthenticationExecutionExportRepresentation> createExecutions() {
        AuthenticationExecutionExportRepresentation execution = new AuthenticationExecutionExportRepresentation();
        execution.setAuthenticator(PROVIDER_ID);
        execution.setRequirement("REQUIRED");
        execution.setAuthenticatorFlow(true);

        return List.of(execution);
    }
}
