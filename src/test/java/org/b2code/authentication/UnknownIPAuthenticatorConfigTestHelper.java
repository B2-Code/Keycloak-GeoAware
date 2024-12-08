package org.b2code.authentication;

import org.b2code.IntegrationTestBase;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.authentication.base.condition.AuthenticatorConditionOption;
import org.b2code.authentication.unknownip.UnknownIPAuthenticatorFactory;
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

    public void setCondition(String label) {
        updateProperty(AuthenticatorCondition.CONFIG_PROPERTY_NAME, label);
    }

    private void updateProperty(String propertyName, String value) {
        updateAuthenticatorConfig(authenticatorConfig -> {
            Map<String, String> config = authenticatorConfig.getConfig();
            config.put(propertyName, value);
            authenticatorConfig.setConfig(config);
        });
    }

    private void updateAuthenticatorConfig(Consumer<AuthenticatorConfigRepresentation> configurer) {
        AuthenticationManagementResource flowResource = keycloak.realm(IntegrationTestBase.TEST_REALM).flows();
        List<AuthenticationExecutionInfoRepresentation> executions = flowResource.getFlows().stream()
                .flatMap(f -> flowResource.getExecutions(f.getAlias()).stream())
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .toList();
        for (AuthenticationExecutionInfoRepresentation execution : executions) {
            String authenticationConfigId = execution.getAuthenticationConfig();
            AuthenticatorConfigRepresentation authenticatorConfig = flowResource.getAuthenticatorConfig(authenticationConfigId);
            configurer.accept(authenticatorConfig);
            flowResource.updateAuthenticatorConfig(authenticationConfigId, authenticatorConfig);
        }
    }

}
