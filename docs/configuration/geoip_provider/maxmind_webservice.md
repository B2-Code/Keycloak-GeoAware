# Maxmind Webservice

The Maxmind Webservice GeoIP provider allows you to use Maxmind's GeoIP web services to perform geolocation lookups.
This provider is useful when you want to leverage Maxmind's online databases without the need to maintain local database files.
Maxmind [claims](https://support.maxmind.com/knowledge-base/articles/latency-and-uptime-for-the-geoip-web-services) that their web services have low latency and high uptime, making them suitable for real-time geolocation needs.

To use the Maxmind Webservice GeoIP provider, you need to set the following configuration options:

| Name                                                | Description                                                                                                                                                                                                                                                                                                                                        |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| spi-geoaware-geoip--provider                        | Set to `maxmind-webservice`.                                                                                                                                                                                                                                                                                                                       |
| spi-geoaware-geoip--maxmind-webservice--host        | The host to use. Use `geoip.maxmind.com` to use the GeoIP2 databse. Set this to `geolite.info` to use the GeoLite2 web services. Set this to `sandbox.maxmind.com` to use the Sandbox GeoIP2 web services instead of the production GeoIP2 web services. The sandbox allows you to experiment with the API without affecting your production data. |
| spi-geoaware-geoip--maxmind-webservice--account-id  | Your Maxmind account id.                                                                                                                                                                                                                                                                                                                           |
| spi-geoaware-geoip--maxmind-webservice--license-key | Your license key obtained from the [Maxmind website](https://support.maxmind.com/knowledge-base/articles/generate-a-maxmind-license-key).                                                                                                                                                                                                          |

Please note that Maxmind web services have usage limits based on your account type.
Make sure to review their [pricing and plans](https://www.maxmind.com/en/geoip2-services-and-databases) to choose the one that best fits your needs.

Review Maxmind's [licensing terms](https://www.maxmind.com/en/geolite2/eula) to ensure compliance when using their GeoIP databases.
