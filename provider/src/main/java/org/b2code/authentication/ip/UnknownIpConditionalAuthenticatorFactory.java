package org.b2code.authentication.ip;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.AbstractGeoAwareConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.condition.UnknownIpCondition;
import org.keycloak.authentication.AuthenticatorFactory;

@AutoService(AuthenticatorFactory.class)
public class UnknownIpConditionalAuthenticatorFactory extends AbstractGeoAwareConditionalAuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-condition-unknown-ip";

    public UnknownIpConditionalAuthenticatorFactory() {
        super(UnknownIpCondition.instance());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Condition - " + PluginConstants.PLUGIN_NAME + " Unknown IP";
    }

    @Override
    public String getHelpText() {
        return UnknownIpCondition.instance().getHelpText();
    }
}
