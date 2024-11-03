package org.b2code.mapper;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapper;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

@JBossLog
@AutoService(ProtocolMapper.class)
public class UserAgentInfoMapper extends AbstractOIDCProtocolMapper implements AllTokenTypesMapper {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-oidc-user-agent-info-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, UserAgentInfoMapper.class);
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        log.tracef("Mapping user agent info to claim '%s'", mappingModel.getConfig().get(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME));
        DeviceRepresentationProvider deviceRepresentationProvider = keycloakSession.getProvider(DeviceRepresentationProvider.class);
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, deviceRepresentationProvider.deviceRepresentation());
    }

    @Override
    public String getHelpText() {
        return "Map user agent information to the token";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return PluginConstants.PLUGIN_NAME + " user agent info";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
