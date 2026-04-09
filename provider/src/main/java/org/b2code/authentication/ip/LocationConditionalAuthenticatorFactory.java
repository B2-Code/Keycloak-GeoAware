package org.b2code.authentication.ip;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.AbstractGeoAwareConditionalAuthenticatorFactory;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.authentication.device.DeviceAuthenticatorConfigProperties;
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

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[0];
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        ProviderConfigProperty values_type = new ProviderConfigProperty();
        values_type.setName(CONFIG_VALUE_TYPE);
        values_type.setLabel("Values type");
        values_type.setType(ProviderConfigProperty.LIST_TYPE);
        values_type.setRequired(true);
        values_type.setDefaultValue(COUNTRY_ISO_CODE);
        values_type.setHelpText("Select the type of value.\n");
        values_type.setOptions(CONDITION_OPTIONS);


        ProviderConfigProperty values = new ProviderConfigProperty();
        values.setName(CONFIG_VALUES);
        values.setLabel("Values");
        values.setType(ProviderConfigProperty.MULTIVALUED_STRING_TYPE);
        values.setDefaultValue(false);
        values.setHelpText("List of the match values\n");

        ProviderConfigProperty revert_decision = new ProviderConfigProperty();
        revert_decision.setName(CONFIG_REVERT);
        revert_decision.setLabel("Inverse decision");
        revert_decision.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        revert_decision.setDefaultValue(false);
        revert_decision.setHelpText("Revert de condition decision\n");

        return ProviderConfigurationBuilder.create()
                .property(values_type)
                .property(values)
                .property(revert_decision)
                .build();
    }

    @Override
    public ConditionalAuthenticator getSingleton() {
        return LocationCondition.SINGLETON;
    }
}
