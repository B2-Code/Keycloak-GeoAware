package org.b2code.mapper;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.GeoIpProvider;
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
public class GeoIpInfoMapper extends AbstractOIDCProtocolMapper implements AllTokenTypesMapper {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-oidc-geoip-info-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, GeoIpInfoMapper.class);
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        log.tracef("Mapping GeoIp info to claim '%s'", mappingModel.getConfig().get(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME));
        GeoIpProvider geoipProvider = keycloakSession.getProvider(GeoIpProvider.class);
        GeoIpInfo geoIpInfo = geoipProvider.getIpInfo(userSession.getIpAddress());
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, geoIpInfo);
    }

    @Override
    public String getHelpText() {
        return "Map GeoIp information to the token";
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
        return PluginConstants.PLUGIN_NAME + " GeoIp info";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
