package org.b2code.persistence;

import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Collections;
import java.util.List;

public class EntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(LoginRecordEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/db/changelog/changelog-master.xml";
    }

    @Override
    public String getFactoryId() {
        return EntityProviderFactory.ID;
    }

    @Override
    public void close() {
        // NOOP
    }

}
