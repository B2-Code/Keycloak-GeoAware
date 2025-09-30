package org.b2code.authentication;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.jboss.logmanager.handlers.SyslogHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlow;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordFormFactory;
import org.keycloak.representations.idm.*;
import org.keycloak.testframework.annotations.InjectSysLogServer;
import org.keycloak.testframework.events.SysLogServer;
import org.keycloak.testframework.events.SysLogServerSupplier;
import org.keycloak.testframework.mail.MailServer;
import org.keycloak.testframework.mail.annotations.InjectMailServer;

import java.util.List;
import java.util.Map;

@Slf4j
abstract class BaseAuthenticatorProviderTest extends BaseTest {

    private static final String FLOW_ALIAS = "test-flow";

    @InjectMailServer
    MailServer mailServer;

    @BeforeEach
    void setupFlow() {
        log.info("Creating flow");
        createFlow();

        AuthenticationFlowRepresentation authenticationFlowRepresentation = getFlow();
        Assertions.assertNotNull(authenticationFlowRepresentation);

        for (AuthenticationExecutionRepresentation execution : createExecutions()) {
            execution.setParentFlow(getFlow().getId());
            try (Response response = realm.admin().flows().addExecution(execution)) {
                Assertions.assertEquals(201, response.getStatus());
            }
        }
        Assertions.assertEquals(2, getFlow().getAuthenticationExecutions().size());

        RealmRepresentation realmRep = realm.getCreatedRepresentation();
        realmRep.setBrowserFlow(FLOW_ALIAS);
        realm.admin().update(realmRep);

        log.info("Flow created");
    }

    @Test
    void testLoginAlwaysLogMessage()  {
        this.setConditionAndAction("Always", "Log");
        login();
    }

    @Test
    void testLoginNeverLogMessage()  {
        this.setConditionAndAction("Never", "Log");
        login();
    }
  
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

    private AuthenticationFlowRepresentation getFlow() {
        return realm.admin().flows().getFlows().stream()
                .filter(f -> f.getAlias().equals(FLOW_ALIAS)).findAny().orElseThrow();
    }

    private void createFlow() {
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setAlias(FLOW_ALIAS);
        flow.setProviderId(AuthenticationFlow.BASIC_FLOW);
        flow.setTopLevel(true);
        flow.setBuiltIn(false);

        try (Response response = realm.admin().flows().createFlow(flow)) {
            Assertions.assertEquals(201, response.getStatus());
        }
    }

    private List<AuthenticationExecutionRepresentation> createExecutions() {
        AuthenticationExecutionRepresentation authenticatorToTest = getAuthenticatorToTest();

        AuthenticationExecutionRepresentation usernamePasswordAuthenticator = new AuthenticationExecutionRepresentation();
        usernamePasswordAuthenticator.setAuthenticator(UsernamePasswordFormFactory.PROVIDER_ID);
        usernamePasswordAuthenticator.setRequirement("REQUIRED");

        return List.of(usernamePasswordAuthenticator, authenticatorToTest);
    }

    void setAuthenticatorConfig(Map<String, String> options) {
        AuthenticationExecutionInfoRepresentation execution = realm.admin().flows().getExecutions(getFlow().getAlias()).getLast();
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

    abstract AuthenticationExecutionRepresentation getAuthenticatorToTest();

}
