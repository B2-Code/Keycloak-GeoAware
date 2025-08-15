package org.b2code.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.b2code.PluginConstants;
import org.b2code.config.RealmAConfig;
import org.b2code.config.TestUserConfig;
import org.b2code.loginhistory.LoginRecord;
import org.junit.jupiter.api.Assertions;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.InjectUser;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.oauth.OAuthClient;
import org.keycloak.testframework.oauth.annotations.InjectOAuthClient;
import org.keycloak.testframework.realm.ManagedRealm;
import org.keycloak.testframework.realm.ManagedUser;
import org.keycloak.testsuite.util.oauth.AccessTokenResponse;
import org.keycloak.testsuite.util.oauth.AuthorizationEndpointResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class BaseTest {

    @InjectRealm(lifecycle = LifeCycle.METHOD, config = RealmAConfig.class)
    protected ManagedRealm realm;

    @InjectOAuthClient
    protected OAuthClient oAuthClient;

    @InjectUser(config = TestUserConfig.class)
    protected ManagedUser user;

    protected List<LoginRecord> getLoginRecords() {
        UserRepresentation userRep = realm.admin().users().get(user.getId()).toRepresentation();
        Assertions.assertNotNull(userRep);

        Map<String, List<String>> attributes = userRep.getAttributes();
        Assertions.assertNotNull(attributes);

        List<String> ipAddresses = attributes.get("loginHistoryRecord");
        Assertions.assertNotNull(ipAddresses);
        return ipAddresses.stream().map(ip -> {
            try {
                return getObjectMapper().readValue(ip, LoginRecord.class);
            } catch (Exception e) {
                log.error("Failed to parse login record", e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    protected void logout() {
        realm.admin().users().get(user.getId()).logout();
    }

    protected void login() {
        this.login(false);
    }

    protected void loginAndExpectFail() {
        this.login(true);
    }

    protected void loginFromIp(String ip) {
        setMockIp(ip);
        login(false);
    }

    protected void loginFromIpAndExpectFail(String ip) {
        setMockIp(ip);
        login(true);
    }

    private void setMockIp(String ip) {
        RealmRepresentation realmRep = realm.admin().toRepresentation();
        Map<String, String> attributes = realmRep.getAttributes();
        attributes.put(PluginConstants.PLUGIN_NAME_LOWER_CASE + "-mock-ip", ip);
        realm.admin().update(realmRep);
    }

    private void login(boolean expectFail) {
        log.info("Logging in");
        log.info(expectFail ? "Expecting login to fail" : "Expecting login to succeed");

        AuthorizationEndpointResponse authorizationEndpointResponse = oAuthClient.doLogin(user.getUsername(), user.getPassword());

        if (expectFail) {
            Assertions.assertFalse(authorizationEndpointResponse.isRedirected());
            log.info("Login failed as expected");
            return;
        }

        Assertions.assertTrue(authorizationEndpointResponse.isRedirected());
        Assertions.assertNotNull(authorizationEndpointResponse.getCode());

        AccessTokenResponse accessTokenResponse = oAuthClient.doAccessTokenRequest(authorizationEndpointResponse.getCode());
        Assertions.assertTrue(accessTokenResponse.isSuccess());
        Assertions.assertNotNull(accessTokenResponse.getAccessToken());

        log.info("Login successful");
    }

    protected ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
