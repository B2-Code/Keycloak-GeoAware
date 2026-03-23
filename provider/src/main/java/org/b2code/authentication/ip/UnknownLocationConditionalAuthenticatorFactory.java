package org.b2code.authentication.ip;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.AbstractGeoAwareConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.condition.UnknownLocationCondition;
import org.keycloak.authentication.AuthenticatorFactory;

@AutoService(AuthenticatorFactory.class)
public class UnknownLocationConditionalAuthenticatorFactory extends AbstractGeoAwareConditionalAuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-condition-unknown-location";

    public UnknownLocationConditionalAuthenticatorFactory() {
        super(UnknownLocationCondition.instance());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Condition - " + PluginConstants.PLUGIN_NAME + " Unknown location";
    }

    @Override
    public String getHelpText() {
        return UnknownLocationCondition.instance().getHelpText();
    }
}
