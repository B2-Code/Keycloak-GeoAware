package org.b2code.authentication;

import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.b2code.config.TestRealmConfig;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testframework.ui.annotations.InjectPage;
import org.keycloak.testframework.ui.page.ErrorPage;

@Slf4j
abstract class BaseConditionalAuthenticatorProviderTest extends BaseTest {

    @InjectPage
    ErrorPage errorPage;

    @BeforeEach
    void setupFlow() {
        RealmRepresentation realmRep = realm.getCreatedRepresentation();
        realmRep.setBrowserFlow(getFlowAlias());
        realm.admin().update(realmRep);
    }

    private String getFlowAlias() {
        return TestRealmConfig.CONDITIONAL_FLOW_PREFIX + getConditionalProviderToTest();
    }

    protected void loginAndExpectDenied() {
        log.info("Logging in");
        log.info("Expecting login to be denied by conditional authenticator");
        oAuthClient.client(client.getClientId()).loginForm().open();
        loginPage.fillLogin(user.getUsername(), user.getPassword());
        loginPage.submit();
        errorPage.assertCurrent();
        log.info("Login denied as expected");
    }

    protected void loginFromIpAndExpectDenied(String ip) {
        setMockIp(ip);
        try {
            loginAndExpectDenied();
        } finally {
            setMockIp(null);
        }
    }

    abstract String getConditionalProviderToTest();
}
