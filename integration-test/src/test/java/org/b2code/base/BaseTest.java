package org.b2code.base;

import lombok.extern.slf4j.Slf4j;
import org.b2code.PluginConstants;
import org.b2code.config.TestClientConfig;
import org.b2code.config.TestRealmConfig;
import org.b2code.config.TestUserConfig;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.b2code.util.LoginHistory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.util.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testframework.annotations.*;
import org.keycloak.testframework.database.TestDatabase;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.oauth.OAuthClient;
import org.keycloak.testframework.oauth.annotations.InjectOAuthClient;
import org.keycloak.testframework.realm.ManagedClient;
import org.keycloak.testframework.realm.ManagedRealm;
import org.keycloak.testframework.realm.ManagedUser;
import org.keycloak.testframework.ui.annotations.InjectPage;
import org.keycloak.testframework.ui.page.LoginPage;
import org.keycloak.testsuite.util.oauth.AccessTokenResponse;
import org.keycloak.testsuite.util.oauth.AuthorizationEndpointResponse;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseTest {

    @InjectRealm(lifecycle = LifeCycle.METHOD, config = TestRealmConfig.class)
    protected ManagedRealm realm;

    @InjectClient(config = TestClientConfig.class)
    protected ManagedClient client;

    @InjectOAuthClient
    protected OAuthClient oAuthClient;

    @InjectPage
    protected LoginPage loginPage;

    @InjectUser(lifecycle = LifeCycle.METHOD, config = TestUserConfig.class)
    protected ManagedUser user;

    @InjectTestDatabase
    protected TestDatabase testDatabase;

    @InjectAdminClient
    protected Keycloak adminClient;

    protected LoginHistory loginHistory;

    @BeforeEach
    public void beforeEach() {
        Map<String, String> config = testDatabase.serverConfig();
        loginHistory = new LoginHistory(config.get("db-url"), config.get("db-username"), config.get("db-password"));
    }

    @AfterEach
    public void afterEach() {
        if (loginHistory != null) {
            loginHistory.close();
        }
    }

    protected List<LoginRecordEntity> getLoginRecords() {

        return loginHistory.getAllByUserId(user.getId());
    }

    protected void logout() {
        realm.admin().users().get(user.getId()).logout();
    }

    protected AccessTokenResponse login() {
        return this.login(false);
    }

    protected void loginAndExpectFail() {
        this.login(true);
    }

    protected void loginFromIp(String ip) {
        setMockIp(ip);
        try {
            login(false);
        } finally {
            setMockIp(null);
        }
    }

    protected void loginFromIpAndExpectFail(String ip) {
        setMockIp(ip);
        try {
            login(true);
        } finally {
            setMockIp(null);
        }
    }

    protected void setMockIp(String ip) {
        RealmRepresentation realmRep = realm.admin().toRepresentation();
        Map<String, String> attributes = realmRep.getAttributes();
        String attrName = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-mock-ip";
        if (ip == null) {
            attributes.remove(attrName);
        } else {
            attributes.put(attrName, ip);
        }
        realm.admin().update(realmRep);
    }

    private AccessTokenResponse login(boolean expectFail) {
        log.info("Logging in");
        log.info(expectFail ? "Expecting login to fail" : "Expecting login to succeed");

        oAuthClient.client(client.getClientId()).loginForm().open();
        loginPage.fillLogin(user.getUsername(), user.getPassword());
        loginPage.submit();

        AuthorizationEndpointResponse authorizationEndpointResponse = oAuthClient.parseLoginResponse();

        Assertions.assertTrue(authorizationEndpointResponse.isRedirected());

        if (authorizationEndpointResponse.getCode() == null && expectFail) {
            if (StringUtils.isNotBlank(authorizationEndpointResponse.getError())) {
                log.info("Authorization Request Error: {}", authorizationEndpointResponse.getError());
            }
            log.info("Login failed as expected");
            return null;
        }

        Assertions.assertNotNull(authorizationEndpointResponse.getCode());

        AccessTokenResponse accessTokenResponse = oAuthClient.doAccessTokenRequest(authorizationEndpointResponse.getCode());

        if (!expectFail) {
            Assertions.assertTrue(accessTokenResponse.isSuccess());
            Assertions.assertNotNull(accessTokenResponse.getAccessToken());
            log.info("Login successful");
        } else {
            Assertions.assertFalse(accessTokenResponse.isSuccess());
            if (StringUtils.isNotBlank(accessTokenResponse.getError())) {
                log.info("Access Token Request Error: {}", accessTokenResponse.getError());
            }
            log.info("Login failed as expected");
        }

        return accessTokenResponse;
    }

}