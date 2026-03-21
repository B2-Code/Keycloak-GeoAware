package org.b2code.authentication.device;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.authentication.base.AbstractGeoAwareConditionalAuthenticatorFactory;
import org.b2code.authentication.device.condition.UnknownDeviceCondition;
import org.keycloak.authentication.AuthenticatorFactory;

@AutoService(AuthenticatorFactory.class)
public class UnknownDeviceConditionalAuthenticatorFactory extends AbstractGeoAwareConditionalAuthenticatorFactory {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-condition-unknown-device";

    public UnknownDeviceConditionalAuthenticatorFactory() {
        super(UnknownDeviceCondition.instance());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Condition - " + PluginConstants.PLUGIN_NAME + " Unknown device";
    }

    @Override
    public String getHelpText() {
        return UnknownDeviceCondition.instance().getHelpText();
    }
}
