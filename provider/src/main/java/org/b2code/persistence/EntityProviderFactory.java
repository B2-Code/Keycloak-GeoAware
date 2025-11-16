package org.b2code.persistence;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(JpaEntityProviderFactory.class)
public class EntityProviderFactory implements JpaEntityProviderFactory {

    protected static final String ID = PluginConstants.PLUGIN_NAME_LOWER_CASE;

    private static final EntityProvider entityProvider = new EntityProvider();

    @Override
    public JpaEntityProvider create(KeycloakSession keycloakSession) {
        return entityProvider;
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
