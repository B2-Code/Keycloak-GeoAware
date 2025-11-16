package org.b2code.geoip.persistence.repository;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(LoginRecordRepositoryFactory.class)
public class JpaLoginRecordRepositoryFactory implements LoginRecordRepositoryFactory {

    @Override
    public LoginRecordRepository create(KeycloakSession session) {
        return new JpaLoginRecordRepository(session);
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return "jpa";
    }
}
