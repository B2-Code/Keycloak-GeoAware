---
title: Configuration
layout: default
nav_order: 3
---

## Configuration

In order to use the extension, you need to configure it correctly.
This section will guide you through the configuration process.

### Choose a Geolocation Provider

The extension supports multiple geolocation providers.
You need to decide which provider you want to use and configure it accordingly.
Currently supported providers are:

- [IPinfo](./geoip_provider/ipinfo.md)
- [Maxmind File](./geoip_provider/maxmind_file.md)
- [Maxmind File Autodownload](./geoip_provider/maxmind_file_autodownload.md)
- [Maxmind Webservice](./geoip_provider/maxmind_webservice.md)

Click on the links above to find detailed configuration instructions for each provider.
Once you have decided on a provider and configured it, proceed to the next step.

### General Configuration Options

In addition to the provider-specific configuration options, there are some general configuration options that you can set.
You don't have to change these unless you have specific requirements.

| Name                                                      | Description                                                                                                                                                                                                                                                               |
|-----------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| spi-geoaware-login-history--jpa--retention-hours          | Configure how long GeoAware should retain login information. After this time, the records will be deleted. Please note that this may affect the behaviour of the GeoAware authenticators, as they rely on login history to function. The default is 168 hours (one week). |
| spi-geoaware-login-history--jpa--cleanup-interval-minutes | Configure the cleanup interval. Default is 5 minutes.                                                                                                                                                                                                                     |
| spi-geoaware-geoip-cache--jpa--cache-duration-minutes     | If an IP address is looked up multiple times within this timeframe, GeoAware will use a cache instead of querying the configured GeoIP provider. The default is 60 minutes.                                                                                               |

### Activate login tracking
To enable login tracking, you need to activate the event listener in your configuration.
In the Admin Console, navigate to `Realm settings` -> `Events` and add `geoaware-login-tracker` under `Event listeners`.
This will ensure that GeoAware tracks login events and stores the relevant information for geolocation analysis.
If you do not activate login tracking, the GeoAware authenticators will not function correctly, as they rely on historical login data to make decisions.

### Use the Authenticators
Once you have configured the geolocation provider and activated login tracking, you can start using the GeoAware authenticators in your authentication flows.
How to configure authentication flows depends on your use case.
Refer to the [Usage](../usage/index.md) section for detailed instructions on how to set up the authenticators.
