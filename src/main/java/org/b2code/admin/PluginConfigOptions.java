package org.b2code.admin;

import lombok.experimental.UtilityClass;
import org.b2code.PluginConstants;
import org.b2code.geoip.maxmind.MaxmindDatabase;
import org.b2code.geoip.maxmind.MaxmindFileProviderFactory;
import org.keycloak.provider.ProviderConfigProperty;

@UtilityClass
public class PluginConfigOptions {

    public static final ProviderConfigProperty ENABLED = new ProviderConfigProperty(
            "enabled",
            "Enabled",
            "Enable " + PluginConstants.PLUGIN_NAME + " for this realm",
            ProviderConfigProperty.BOOLEAN_TYPE,
            "false",
            false,
            true
    );

    public static final ProviderConfigProperty GEOIP_PROVIDER = new ProviderConfigProperty(
            "geoip_provider",
            "GeoIP Provider",
            "Select the GeoIP database provider",
            ProviderConfigProperty.LIST_TYPE,
            MaxmindFileProviderFactory.PROVIDER_ID,
            false,
            true
    );

    public static final ProviderConfigProperty GEOIP_CACHE_SIZE = new ProviderConfigProperty(
            "geoip_cache_size",
            "GeoIP Cache Size",
            "Size of the GeoIP cache",
            ProviderConfigProperty.STRING_TYPE,
            "1000",
            false,
            true
    );

    public static final ProviderConfigProperty MAXMIND_FILE_PATH = new ProviderConfigProperty(
            "maxmind_database_path",
            "Maxmind File Path",
            "Path to the Maxmind database file",
            ProviderConfigProperty.STRING_TYPE,
            "",
            false,
            false
    );

    public static final ProviderConfigProperty MAXMIND_ACCOUNT_ID = new ProviderConfigProperty(
            "maxmind_account_id",
            "Maxmind Account ID",
            "Maxmind account ID",
            ProviderConfigProperty.STRING_TYPE,
            "",
            false,
            false
    );

    public static final ProviderConfigProperty MAXMIND_LICENSE_KEY = new ProviderConfigProperty(
            "maxmind_license_key",
            "Maxmind License Key",
            "Maxmind license key",
            ProviderConfigProperty.PASSWORD,
            "",
            false,
            false
    );

    public static final ProviderConfigProperty MAXMIND_WEB_DATABASE = new ProviderConfigProperty(
            "maxmind_web_database",
            "Maxmind Web Service Database",
            "Specify the Maxmind database to use",
            ProviderConfigProperty.LIST_TYPE,
            MaxmindDatabase.GEO_LITE.getLabel()
    );

    public static final ProviderConfigProperty IPINFO_TOKEN = new ProviderConfigProperty(
            "ipinfo_token",
            "IPinfo Token",
            "IPinfo token",
            ProviderConfigProperty.PASSWORD,
            "",
            false,
            false
    );

    public static final ProviderConfigProperty IPINFO_CACHE_IN_DAYS = new ProviderConfigProperty(
            "ipinfo_cache_in_days",
            "IPinfo Cache In Days",
            "Number of days to cache IPinfo data",
            ProviderConfigProperty.STRING_TYPE,
            "7",
            false,
            true
    );

    public static final ProviderConfigProperty LOGIN_HISTORY_RETENTION_DAYS = new ProviderConfigProperty(
            "login_history_retention_days",
            "Login History Retention Days",
            "Number of days to retain login history for each user",
            ProviderConfigProperty.STRING_TYPE,
            "7",
            false,
            true
    );

    public static final ProviderConfigProperty LOGIN_HISTORY_MAX_RECORDS = new ProviderConfigProperty(
            "login_history_max_records",
            "Login History Max Records",
            "Maximum number of login history records to retain for each user",
            ProviderConfigProperty.STRING_TYPE,
            "100",
            false,
            true
    );
}
