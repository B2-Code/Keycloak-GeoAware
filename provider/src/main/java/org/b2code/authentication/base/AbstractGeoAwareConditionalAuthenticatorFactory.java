package org.b2code.authentication.base;

import org.b2code.ServerInfoAwareFactory;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.keycloak.Config;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public abstract class AbstractGeoAwareConditionalAuthenticatorFactory extends ServerInfoAwareFactory implements ConditionalAuthenticatorFactory {

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    private final ConditionalAuthenticator singleton;

    protected AbstractGeoAwareConditionalAuthenticatorFactory(AuthenticatorCondition condition) {
        this.singleton = new GeoAwareConditionalAuthenticator(condition);
    }

    @Override
    public ConditionalAuthenticator getSingleton() {
        return singleton;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES.clone();
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }
}
