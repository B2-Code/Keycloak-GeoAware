package org.b2code.service.useragent;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class UserAgentParserSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "user-agent-parser";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return UserAgentParserProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<UserAgentParserProvider>> getProviderFactoryClass() {
        return UserAgentParserProviderFactory.class;
    }
}
