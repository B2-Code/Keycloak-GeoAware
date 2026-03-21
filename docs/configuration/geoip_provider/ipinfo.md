---
title: IPinfo
layout: default
parent: Configuration
nav_order: 1
---

# IPinfo

To use IPinfo as your geolocation provider, set `spi-geoaware-geoip--provider` to `ipinfo-webservice` and configure the following options.
You will need to obtain an API token from [IPinfo](https://ipinfo.io/) to use their web service.

| Name                                         | Description        |
|----------------------------------------------|--------------------|
| spi-geoaware-geoip--ipinfo-webservice--token | Your IPinfo token. |

Please note that IPinfo has usage limits based on your account type. Make sure to review their [pricing and plans](https://ipinfo.io/pricing) to choose the one that best fits your needs.
