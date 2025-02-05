package org.b2code.geoip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.b2code.config.RealmAConfig;
import org.b2code.config.TestUserConfig;
import org.b2code.service.loginhistory.LoginRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.InjectUser;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.oauth.nimbus.OAuthClient;
import org.keycloak.testframework.oauth.nimbus.annotations.InjectOAuthClient;
import org.keycloak.testframework.realm.ManagedRealm;
import org.keycloak.testframework.realm.ManagedUser;
import org.keycloak.testframework.ui.annotations.InjectPage;
import org.keycloak.testframework.ui.annotations.InjectWebDriver;
import org.keycloak.testframework.ui.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
abstract class BaseGeoIpProviderTest {

    @InjectRealm(lifecycle = LifeCycle.METHOD, config = RealmAConfig.class)
    protected ManagedRealm realm;

    @InjectOAuthClient
    protected OAuthClient oAuthClient;

    @InjectWebDriver
    protected WebDriver webDriver;

    @InjectPage
    protected LoginPage loginPage;

    @InjectUser(config = TestUserConfig.class)
    protected ManagedUser user;

    @Test
    void testLoginHistoryIsCreated() throws Exception {
        login();

        UserRepresentation userRep = realm.admin().users().get(user.getId()).toRepresentation();
        Assertions.assertNotNull(userRep);

        Map<String, List<String>> attributes = userRep.getAttributes();
        Assertions.assertNotNull(attributes);

        List<String> ipAddresses = attributes.get("loginHistoryRecord");
        Assertions.assertNotNull(ipAddresses);
        Assertions.assertEquals(1, ipAddresses.size());
        LoginRecord loginRecord = getObjectMapper().readValue(ipAddresses.getFirst(), LoginRecord.class);

        Assertions.assertNotNull(loginRecord);
        Assertions.assertNotNull(loginRecord.getIp());
        Assertions.assertTrue(loginRecord.getIp().equals("127.0.0.1") || loginRecord.getIp().equals("0:0:0:0:0:0:0:1"));
    }

    @Test
    void testLoginHistoryMaxLength() throws Exception {
        for (int i = 0; i < 10; i++) {
            login();
            logout();
        }
        List<LoginRecord> ipAddresses = getLoginRecords();
        Assertions.assertEquals(5, ipAddresses.size());
    }

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
        }).toList();
    }

    protected void logout() {
        realm.admin().users().get(user.getId()).logout();
    }

    protected void login() throws Exception {
        URL authorizationRequestURL = oAuthClient.authorizationRequest();
        webDriver.navigate().to(authorizationRequestURL);
        loginPage.fillLogin(user.getUsername(), user.getPassword());
        loginPage.submit();

        Assertions.assertEquals(1, oAuthClient.getCallbacks().size());

        URI callbackUri = oAuthClient.getCallbacks().removeFirst();

        AuthorizationResponse authorizationResponse = AuthorizationResponse.parse(callbackUri);
        Assertions.assertTrue(authorizationResponse.indicatesSuccess());
        Assertions.assertNotNull(authorizationResponse.toSuccessResponse().getAuthorizationCode());

        TokenResponse tokenResponse = oAuthClient.tokenRequest(authorizationResponse.toSuccessResponse().getAuthorizationCode());
        Assertions.assertTrue(tokenResponse.indicatesSuccess());
        Assertions.assertNotNull(tokenResponse.toSuccessResponse().getTokens().getAccessToken());
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

}