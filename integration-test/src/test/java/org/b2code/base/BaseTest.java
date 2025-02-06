package org.b2code.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.b2code.config.RealmAConfig;
import org.b2code.config.TestUserConfig;
import org.b2code.loginhistory.LoginRecord;
import org.junit.jupiter.api.Assertions;
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
public abstract class BaseTest {

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
        this.login(false);
    }

    protected void login(boolean expectFail) throws Exception {
        log.info("Logging in");
        log.info(expectFail ? "Expecting login to fail" : "Expecting login to succeed");

        URL authorizationRequestURL = oAuthClient.authorizationRequest();
        webDriver.navigate().to(authorizationRequestURL);
        loginPage.fillLogin(user.getUsername(), user.getPassword());
        loginPage.submit();

        if (expectFail) {
            Assertions.assertEquals(0, oAuthClient.getCallbacks().size());
            log.info("Login failed as expected");
            return;
        }

        Assertions.assertEquals(1, oAuthClient.getCallbacks().size());

        URI callbackUri = oAuthClient.getCallbacks().removeFirst();

        AuthorizationResponse authorizationResponse = AuthorizationResponse.parse(callbackUri);
        Assertions.assertTrue(authorizationResponse.indicatesSuccess());
        Assertions.assertNotNull(authorizationResponse.toSuccessResponse().getAuthorizationCode());

        TokenResponse tokenResponse = oAuthClient.tokenRequest(authorizationResponse.toSuccessResponse().getAuthorizationCode());
        Assertions.assertTrue(tokenResponse.indicatesSuccess());
        Assertions.assertNotNull(tokenResponse.toSuccessResponse().getTokens().getAccessToken());

        log.info("Login successful");
    }

    protected ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
