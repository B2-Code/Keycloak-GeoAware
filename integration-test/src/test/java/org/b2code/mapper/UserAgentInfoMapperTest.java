package org.b2code.mapper;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.b2code.util.AdminEventPaths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.UserInfo;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.events.AdminEventAssertion;
import org.keycloak.testframework.util.ApiUtil;
import org.keycloak.testsuite.util.oauth.AccessTokenResponse;
import org.keycloak.testsuite.util.oauth.UserInfoResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
public class UserAgentInfoMapperTest extends BaseMapperTest {

    private ProtocolMappersResource mappersRsc;

    @BeforeEach
    public void setProtocolMapperResources() {
        mappersRsc = client.admin().getProtocolMappers();
    }

    @Test
    void testGetMappersList() {
        Assertions.assertTrue(mappersRsc.getMappers().isEmpty());
    }

    @Test
    void testCreateUserAgentInfoProtocolMapper() {
        Map<String, String> config = new HashMap<>();
        config.put("claim.name", "user-agent");

        ProtocolMapperRepresentation rep = makeMapper("User-Agent-Info-Mapper", UserAgentInfoMapper.PROVIDER_ID, config);

        int totalMappers = mappersRsc.getMappers().size();
        int totalOidcMappers = mappersRsc.getMappersPerProtocol("openid-connect").size();
        Response resp = mappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);

        AdminEventAssertion.assertEvent(adminEvents.poll(), OperationType.CREATE, AdminEventPaths.clientProtocolMapperPath(client.getId(), createdId), rep, ResourceType.PROTOCOL_MAPPER);

        Assertions.assertEquals(totalMappers + 1, mappersRsc.getMappers().size());
        Assertions.assertEquals(totalOidcMappers + 1, mappersRsc.getMappersPerProtocol("openid-connect").size());

        ProtocolMapperRepresentation created = mappersRsc.getMapperById(createdId);
        assertEqualMappers(rep, created);
    }

    @Test
    void testIfClaimIsInIdToken() {
        Map<String, String> config = new HashMap<>();
        config.put("claim.name", "user-agent");
        config.put("id.token.claim", "true");

        ProtocolMapperRepresentation rep = makeMapper("User-Agent-Info-Mapper", UserAgentInfoMapper.PROVIDER_ID, config);

        Response resp = mappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);

        AdminEventAssertion.assertEvent(adminEvents.poll(), OperationType.CREATE, AdminEventPaths.clientProtocolMapperPath(client.getId(), createdId), rep, ResourceType.PROTOCOL_MAPPER);

        AccessTokenResponse accessTokenResponse = login();
        Assertions.assertNotNull(accessTokenResponse);

        String idToken = accessTokenResponse.getIdToken();
        Assertions.assertNotNull(idToken);

        IDToken token = oAuthClient.parseToken(idToken, IDToken.class);
        Assertions.assertNotNull(token);

        Object geoIpClaim = token.getOtherClaims().get("user-agent");
        Assertions.assertNotNull(geoIpClaim);
    }

    @Test
    void testIfClaimIsInAccessToken() {
        Map<String, String> config = new HashMap<>();
        config.put("claim.name", "user-agent");
        config.put("access.token.claim", "true");

        ProtocolMapperRepresentation rep = makeMapper("User-Agent-Info-Mapper", UserAgentInfoMapper.PROVIDER_ID, config);

        Response resp = mappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);

        AdminEventAssertion.assertEvent(adminEvents.poll(), OperationType.CREATE, AdminEventPaths.clientProtocolMapperPath(client.getId(), createdId), rep, ResourceType.PROTOCOL_MAPPER);

        AccessTokenResponse accessTokenResponse = login();
        Assertions.assertNotNull(accessTokenResponse);

        String accessToken = accessTokenResponse.getAccessToken();
        Assertions.assertNotNull(accessToken);

        AccessToken token = oAuthClient.parseToken(accessToken, AccessToken.class);
        Assertions.assertNotNull(token);

        Object geoIpClaim = token.getOtherClaims().get("user-agent");
        Assertions.assertNotNull(geoIpClaim);
    }

    @Test
    void testIfClaimIsInUserInfo() {
        Map<String, String> config = new HashMap<>();
        config.put("claim.name", "user-agent");
        config.put("userinfo.token.claim", "true");

        ProtocolMapperRepresentation rep = makeMapper("User-Agent-Info-Mapper", UserAgentInfoMapper.PROVIDER_ID, config);

        Response resp = mappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);

        AdminEventAssertion.assertEvent(adminEvents.poll(), OperationType.CREATE, AdminEventPaths.clientProtocolMapperPath(client.getId(), createdId), rep, ResourceType.PROTOCOL_MAPPER);

        AccessTokenResponse accessTokenResponse = login();
        Assertions.assertNotNull(accessTokenResponse);

        UserInfoResponse userInfoResponse = oAuthClient.doUserInfoRequest(accessTokenResponse.getAccessToken());
        Assertions.assertNotNull(userInfoResponse);

        UserInfo userInfo = userInfoResponse.getUserInfo();
        Assertions.assertNotNull(userInfo);

        Object geoIpClaim = userInfo.getOtherClaims().get("user-agent");
        Assertions.assertNotNull(geoIpClaim);
    }

}
