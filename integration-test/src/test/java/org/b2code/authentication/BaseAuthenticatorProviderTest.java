package org.b2code.authentication;

import lombok.extern.slf4j.Slf4j;
import org.b2code.config.RealmAConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationExecutionExportRepresentation;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.realm.ManagedRealm;

import java.util.List;

@Slf4j
abstract class BaseAuthenticatorProviderTest {

    @InjectRealm(lifecycle = LifeCycle.METHOD, config = RealmAConfig.class)
    protected ManagedRealm realm;

    @Test
    void testCreateFlow() {
        Assertions.assertEquals(7, realm.admin().flows().getFlows().size());
        createFlow();
        Assertions.assertEquals(8, realm.admin().flows().getFlows().size());
    }

    @Test
    abstract void testCreateFlowWithExecution();

    abstract void createFlow();

    abstract List<AuthenticationExecutionExportRepresentation> createExecutions();
}
