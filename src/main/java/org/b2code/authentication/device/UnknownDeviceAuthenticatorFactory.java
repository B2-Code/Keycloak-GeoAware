package org.b2code.authentication.device;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.ConditionActionAuthenticator;
import org.b2code.ServerInfoAwareFactory;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

@JBossLog
@AutoService(AuthenticatorFactory.class)
public class UnknownDeviceAuthenticatorFactory extends ServerInfoAwareFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-unknown-device";
    public static final String DISPLAY_TYPE = PluginConstants.PLUGIN_NAME + " Unknown Device";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = new AuthenticationExecutionModel.Requirement[]{AuthenticationExecutionModel.Requirement.REQUIRED, AuthenticationExecutionModel.Requirement.DISABLED};

    @Override
    public String getDisplayType() {
        return DISPLAY_TYPE;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return UnknownDeviceAuthenticatorConfigProperties.CONFIG_PROPERTIES;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new ConditionActionAuthenticator(session);
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

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}