# Maxmind File

The Maxmind File GeoIP provider allows you to use Maxmind's GeoIP databases stored in local files to perform geolocation lookups. This provider is useful when you want to avoid external API calls and
have the GeoIP data available locally.
To use the Maxmind File GeoIP provider, you need to set the following configuration options:

| Name                                      | Description                                                                                                                                                      |
|-------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| spi-geoaware-geoip--provider              | Set to `maxmind-file`                                                                                                                                            |
| spi-geoaware-geoip--maxmind-file--db-path | You need to specify the directory in which GeoAware should look for the database file. Set this variable to the relevant directory. Defaults to `data/geoaware`. |
| spi-geoaware-geoip--maxmind-file--db-name | The name of the database file.                                                                                                                                   |

Make sure to download the appropriate Maxmind GeoIP database file (e.g., GeoLite2-City.mmdb) and place it in the specified directory.
The file needs to be in the Maxmind DB format (MMDB).
You can obtain the database files from the [Maxmind website](https://support.maxmind.com/knowledge-base/articles/download-and-update-maxmind-databases).
Please note that Maxmind databases are updated regularly, so ensure you have a process in place to keep your local database files up to date.

Review Maxmind's [licensing terms](https://www.maxmind.com/en/geolite2/eula) to ensure compliance when using their GeoIP databases.
