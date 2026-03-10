---
title: Maxmind File Autodownload
layout: default
parent: Configuration
nav_order: 3
---

# Maxmind File Autodownload

The Maxmind File Autodownload GeoIP provider allows you to automatically download and update Maxmind's GeoIP databases stored in local files to perform geolocation lookups. This provider is useful
when you want to avoid manual downloads and ensure that your GeoIP data is always up to date.

To use this provider, set `spi-geoaware-geoip--provider` to `maxmind-file-autodownload` and configure the following options:

| Name                                                                 | Description                                                                                                                                                                                                                                                                                                                                      |
|----------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| spi-geoaware-geoip--maxmind-file-autodownload--account-id            | Your Maxmind account ID.                                                                                                                                                                                                                                                                                                                         |
| spi-geoaware-geoip--maxmind-file-autodownload--license-key           | Your license key obtained from the [Maxmind website](https://support.maxmind.com/knowledge-base/articles/generate-a-maxmind-license-key).                                                                                                                                                                                                        |
| spi-geoaware-geoip--maxmind-file-autodownload--download-url          | The download URL. Defaults to `https://download.maxmind.com/geoip/databases/GeoLite2-City/download?suffix=tar.gz` (GeoLite2). To use a different database, copy the download link from the [Maxmind website](https://support.maxmind.com/knowledge-base/articles/download-and-update-maxmind-databases). The URL must point to a `.tar.gz` file. |
| spi-geoaware-geoip--maxmind-file-autodownload--db-path               | The directory where the downloaded database file is stored. Defaults to `data/geoaware`.                                                                                                                                                                                                                                                         |
| spi-geoaware-geoip--maxmind-file-autodownload--update-interval-hours | How often GeoAware checks for and downloads database updates, in hours. Defaults to `24`.                                                                                                                                                                                                                                                        |

Review Maxmind's [licensing terms](https://www.maxmind.com/en/geolite2/eula) to ensure compliance when using their GeoIP databases.
