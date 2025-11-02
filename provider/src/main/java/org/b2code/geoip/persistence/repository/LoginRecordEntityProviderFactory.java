package org.b2code.geoip.persistence.repository;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.geoip.persistence.LoginRecordEntityProvider;
import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(JpaEntityProviderFactory.class)
public class LoginRecordEntityProviderFactory implements JpaEntityProviderFactory {

    public static final String ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-login-record";

    @Override
    public JpaEntityProvider create(KeycloakSession keycloakSession) {
        return new LoginRecordEntityProvider();
    }

    @Override
    public void init(Config.Scope scope) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return ID;
    }
}
