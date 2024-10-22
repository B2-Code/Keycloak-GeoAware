package org.b2code.authentication;

import org.b2code.IntegrationTestBase;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UnknownIPAuthenticatorConfigTestHelper {

    private final Keycloak keycloak;

    public UnknownIPAuthenticatorConfigTestHelper(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void setEmailModus(String emailModus) {
        updateProperty(UnknownIPAuthenticatorConfig.EMAIL_MODUS, emailModus);
    }

    private void updateProperty(String propertyName, String value) {
        updateAuthenticatorConfig(authenticatorConfig -> {
            Map<String, String> config = authenticatorConfig.getConfig();
            config.put(propertyName, value);
            authenticatorConfig.setConfig(config);
        });
    }

    private void updateAuthenticatorConfig(Consumer<AuthenticatorConfigRepresentation> configurer) {
        AuthenticationManagementResource flowRessource = keycloak.realm(IntegrationTestBase.TEST_REALM).flows();
        List<AuthenticationExecutionInfoRepresentation> executions = flowRessource.getFlows().stream()
                .flatMap(f -> flowRessource.getExecutions(f.getAlias()).stream())
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .toList();
        for (AuthenticationExecutionInfoRepresentation execution : executions) {
            String authenticationConfigId = execution.getAuthenticationConfig();
            AuthenticatorConfigRepresentation authenticatorConfig = flowRessource.getAuthenticatorConfig(authenticationConfigId);
            configurer.accept(authenticatorConfig);
            flowRessource.updateAuthenticatorConfig(authenticationConfigId, authenticatorConfig);
        }
    }

}
