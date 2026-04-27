package org.b2code.authentication.ip;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.ip.condition.LocationCondition;
import org.keycloak.Config;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

/**
 * Factory for {@link LocationCondition}, registered as a Keycloak conditional authenticator.
 * Provides configuration properties for location type (country, country ISO code, continent),
 * match values, and an option to invert the decision.
 */
@AutoService(AuthenticatorFactory.class)
public class LocationConditionalAuthenticatorFactory implements ConditionalAuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-condition-location";


    public static final String COUNTRY="Country name";
    public static final String COUNTRY_ISO_CODE="Country Iso Code";
    public static final String CONTINENT="Continent name";

    public static final String CONFIG_VALUE_TYPE="value-type";
    public static final String CONFIG_VALUES="values";
    public static final String CONFIG_REVERT="revert";

    private static final List<String> CONDITION_OPTIONS = List.of(COUNTRY_ISO_CODE, COUNTRY, CONTINENT);


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

    @Override
    public String getDisplayType() {
        return "Condition - " + PluginConstants.PLUGIN_NAME + " Location";
    }

    @Override
    public String getHelpText() {
        return LocationCondition.instance().getHelpText();
    }


    @Override
    public boolean isConfigurable() {
        return true;
    }

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

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

        ProviderConfigProperty valuesType = new ProviderConfigProperty();
        valuesType.setName(CONFIG_VALUE_TYPE);
        valuesType.setLabel("Values type");
        valuesType.setType(ProviderConfigProperty.LIST_TYPE);
        valuesType.setRequired(true);
        valuesType.setDefaultValue(COUNTRY_ISO_CODE);
        valuesType.setHelpText("Select the type of value.");
        valuesType.setOptions(CONDITION_OPTIONS);


        ProviderConfigProperty values = new ProviderConfigProperty();
        values.setName(CONFIG_VALUES);
        values.setLabel("Values");
        values.setType(ProviderConfigProperty.MULTIVALUED_STRING_TYPE);
        values.setDefaultValue("");
        values.setHelpText("List of the match values");

        ProviderConfigProperty revertDecision = new ProviderConfigProperty();
        revertDecision.setName(CONFIG_REVERT);
        revertDecision.setLabel("Inverse decision");
        revertDecision.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        revertDecision.setDefaultValue(false);
        revertDecision.setHelpText("Revert the condition decision");

        return ProviderConfigurationBuilder.create()
                .property(valuesType)
                .property(values)
                .property(revertDecision)
                .build();
    }

    @Override
    public ConditionalAuthenticator getSingleton() {
        return LocationCondition.SINGLETON;
    }
}
