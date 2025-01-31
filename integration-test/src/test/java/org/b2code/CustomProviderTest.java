package org.b2code;

import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import org.b2code.config.MaxmindWebserviceServerConfig;
import org.b2code.config.RealmAConfig;
import org.b2code.config.TestUserConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.InjectUser;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
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

@KeycloakIntegrationTest(config = MaxmindWebserviceServerConfig.class)
class CustomProviderTest {

    @InjectOAuthClient
    OAuthClient oAuthClient;

    @InjectWebDriver
    WebDriver webDriver;

    @InjectPage
    LoginPage loginPage;

    @InjectRealm(lifecycle = LifeCycle.CLASS, config = RealmAConfig.class)
    private ManagedRealm realm;

    @InjectUser(config = TestUserConfig.class)
    ManagedUser user;

    @Test
    void testCreatedClient() {
        Assertions.assertEquals(1, realm.getCreatedRepresentation().getClients().size());
        Assertions.assertEquals("test-client", realm.getCreatedRepresentation().getClients().getFirst().getClientId());
    }

    @Test
    public void testAuthorizationCode() throws Exception {
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

}