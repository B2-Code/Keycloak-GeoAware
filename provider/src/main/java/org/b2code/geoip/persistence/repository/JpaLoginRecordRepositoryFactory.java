package org.b2code.geoip.persistence.repository;

import com.google.auto.service.AutoService;
import org.b2code.ServerInfoAwareFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(LoginRecordRepositoryFactory.class)
public class JpaLoginRecordRepositoryFactory extends ServerInfoAwareFactory implements LoginRecordRepositoryFactory {

    private static final String COMPARE_LIMIT_CONFIG_PARAM = "compareLimit";
    private static final int COMPARE_LIMIT_DEFAULT = 100;

    private Config.Scope config;

    @Override
    public LoginRecordRepository create(KeycloakSession session) {
        return new JpaLoginRecordRepository(session, getCompareLimit());
    }

    private int getCompareLimit() {
        return config.getInt(COMPARE_LIMIT_CONFIG_PARAM, COMPARE_LIMIT_DEFAULT);
    }

    @Override
    public void init(Config.Scope config) {
        this.config = config;
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
