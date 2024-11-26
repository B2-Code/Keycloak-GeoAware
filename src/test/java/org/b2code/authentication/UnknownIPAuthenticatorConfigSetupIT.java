package org.b2code.authentication;

import org.b2code.IntegrationTestBase;
import org.b2code.authentication.base.condition.AlwaysCondition;
import org.b2code.authentication.base.condition.ConditionFactory;
import org.b2code.authentication.unknownip.UnknownIPAuthenticatorFactory;
import org.b2code.authentication.unknownip.condition.OnIpChangeCondition;
import org.b2code.authentication.unknownip.condition.UnknownIpCondition;
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
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(ConditionFactory.CONDITION_PROPERTY_NAME).equals(AlwaysCondition.LABEL)));
    }

    @Test
    void testAuthenticatorUpdateToUnknownIP() {
        UnknownIPAuthenticatorConfigTestHelper authenticatorConfig = new UnknownIPAuthenticatorConfigTestHelper(keycloak.getKeycloakAdminClient());
        authenticatorConfig.setCondition(UnknownIpCondition.LABEL);

        AuthenticationManagementResource flowResource = realm.flows();
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(ConditionFactory.CONDITION_PROPERTY_NAME).equals(UnknownIpCondition.LABEL)));
    }

    @Test
    void testAuthenticatorUpdateToOnChange() {
        UnknownIPAuthenticatorConfigTestHelper authenticatorConfig = new UnknownIPAuthenticatorConfigTestHelper(keycloak.getKeycloakAdminClient());
        authenticatorConfig.setCondition(OnIpChangeCondition.LABEL);

        AuthenticationManagementResource flowResource = realm.flows();
        List<AuthenticatorConfigRepresentation> authenticatorConfigs = flowResource
                .getExecutions("browser-with-geo-ip-block")
                .stream()
                .filter(e -> e.getProviderId() != null && e.getProviderId().equals(UnknownIPAuthenticatorFactory.PROVIDER_ID))
                .map(AuthenticationExecutionInfoRepresentation::getAuthenticationConfig)
                .map(flowResource::getAuthenticatorConfig).toList();
        assertTrue(authenticatorConfigs.stream().allMatch(c -> c.getConfig().get(ConditionFactory.CONDITION_PROPERTY_NAME).equals(OnIpChangeCondition.LABEL)));
    }
}
