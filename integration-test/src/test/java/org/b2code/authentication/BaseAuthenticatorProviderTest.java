package org.b2code.authentication;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.b2code.config.TestRealmConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testframework.mail.MailServer;
import org.keycloak.testframework.mail.annotations.InjectMailServer;
import org.keycloak.testsuite.util.oauth.AccessTokenResponse;

import java.util.Map;

@Slf4j
abstract class BaseAuthenticatorProviderTest extends BaseTest {

    @InjectMailServer
    MailServer mailServer;

    @BeforeEach
    void setupFlow() {
        RealmRepresentation realmRep = realm.getCreatedRepresentation();
        realmRep.setBrowserFlow(getFlowAlias());
        realm.admin().update(realmRep);
    }

    private String getFlowAlias() {
        return TestRealmConfig.AUTHENTICATOR_FLOW_PREFIX + getAuthenticatorProviderToTest();
    }

    @Test
    void testLoginAlwaysLogMessage() {
        this.setConditionAndAction("Always", "Log");
        login();
    }

    @Test
    void testLoginNeverLogMessage() {
        this.setConditionAndAction("Never", "Log");
        login();
    }

    @Test
    void testLoginNeverDenyAccess() {
        this.setConditionAndAction("Never", "Deny Access");
        login();
    }

    @Test
    void testLoginAlwaysDenyAccess() {
        this.setConditionAndAction("Always", "Deny Access");
        loginAndExpectFail();
    }

    @Test
    void testLoginNeverDisableUser() {
        this.setConditionAndAction("Never", "Disable user");
        login();
        Assertions.assertTrue(user.admin().toRepresentation().isEnabled());
    }

    @Test
    void testLoginAlwaysDisableUser() {
        this.setConditionAndAction("Always", "Disable user");
        loginAndExpectFail();
        Assertions.assertFalse(user.admin().toRepresentation().isEnabled());
    }

    void setAuthenticatorConfig(Map<String, String> options) {
        AuthenticationExecutionInfoRepresentation execution = realm.admin().flows().getExecutions(getFlowAlias()).getLast();
        AuthenticatorConfigRepresentation config = new AuthenticatorConfigRepresentation();
        config.setId(execution.getId());
        config.setAlias("execution-" + execution.getId());
        config.setConfig(options);
        try (Response response = realm.admin().flows().newExecutionConfig(execution.getId(), config)) {
            Assertions.assertEquals(201, response.getStatus());
        }
    }

    void setConditionAndAction(String condition, String action) {
        this.setAuthenticatorConfig(Map.of("condition", condition, "action", action));
    }

    abstract String getAuthenticatorProviderToTest();

}
