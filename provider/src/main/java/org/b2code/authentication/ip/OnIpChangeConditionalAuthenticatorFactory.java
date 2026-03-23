package org.b2code.authentication.ip;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.AbstractGeoAwareConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.condition.OnIpChangeCondition;
import org.keycloak.authentication.AuthenticatorFactory;

@AutoService(AuthenticatorFactory.class)
public class OnIpChangeConditionalAuthenticatorFactory extends AbstractGeoAwareConditionalAuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-condition-on-ip-change";

    public OnIpChangeConditionalAuthenticatorFactory() {
        super(OnIpChangeCondition.instance());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Condition - " + PluginConstants.PLUGIN_NAME + " On IP change";
    }

    @Override
    public String getHelpText() {
        return OnIpChangeCondition.instance().getHelpText();
    }
}
