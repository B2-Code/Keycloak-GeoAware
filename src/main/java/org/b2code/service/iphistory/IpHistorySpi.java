package org.b2code.service.iphistory;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class IpHistorySpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "ip-history";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return IpHistoryProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<IpHistoryProvider>> getProviderFactoryClass() {
        return IpHistoryProviderFactory.class;
    }
}
