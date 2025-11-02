package org.b2code.geoip.persistence;

import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.b2code.geoip.persistence.repository.LoginRecordEntityProviderFactory;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Collections;
import java.util.List;

public class LoginRecordEntityProvider implements JpaEntityProvider {


    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(LoginRecordEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "db/changelog/changelog-master.xml";
    }

    @Override
    public String getFactoryId() {
        return LoginRecordEntityProviderFactory.ID;
    }

    @Override
    public void close() {
        // NOOP
    }
}
