package org.b2code.service.loginhistory;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class LoginHistorySpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return PluginConstants.PLUGIN_NAME_LOWER_CASE + "-login-history";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return LoginHistoryProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<LoginHistoryProvider>> getProviderFactoryClass() {
        return LoginHistoryProviderFactory.class;
    }
}
