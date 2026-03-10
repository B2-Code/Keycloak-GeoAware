package org.b2code.authentication.device;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.AbstractGeoAwareConditionalAuthenticatorFactory;
import org.b2code.authentication.device.condition.OnDeviceChangeCondition;
import org.keycloak.authentication.AuthenticatorFactory;

@AutoService(AuthenticatorFactory.class)
public class OnDeviceChangeConditionalAuthenticatorFactory extends AbstractGeoAwareConditionalAuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-condition-on-device-change";

    public OnDeviceChangeConditionalAuthenticatorFactory() {
        super(OnDeviceChangeCondition.instance());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Condition - " + PluginConstants.PLUGIN_NAME + " On device change";
    }

    @Override
    public String getHelpText() {
        return OnDeviceChangeCondition.instance().getHelpText();
    }
}
