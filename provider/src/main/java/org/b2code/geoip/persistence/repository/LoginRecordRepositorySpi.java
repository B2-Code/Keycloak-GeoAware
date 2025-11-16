package org.b2code.geoip.persistence.repository;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class LoginRecordRepositorySpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "loginRecordRepository";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return LoginRecordRepository.class;
    }

    @Override
    public Class<? extends ProviderFactory<?>> getProviderFactoryClass() {
        return LoginRecordRepositoryFactory.class;
    }
}
