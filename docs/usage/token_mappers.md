---
title: Token Mappers
layout: default
parent: Usage
nav_order: 4
---

# Token Mappers

GeoAware provides two OIDC protocol mappers that allow you to include geolocation and device information as claims in your tokens.
These mappers can be added to any client's dedicated scope or to a shared client scope via the Keycloak Admin Console under **Client scopes** or directly on a client under **Client details > Client
scopes > Add mapper**.

Both mappers support all standard token types: access token, ID token, userinfo endpoint, token introspection, and access token response.

---

## GeoAware GeoIp info

**Display name:** `GeoAware GeoIp info`

Resolves the IP address of the current user session through the configured GeoIP provider and maps the result as a structured claim.

The claim contains the following fields:

| Field            | Description                                        |
|------------------|----------------------------------------------------|
| `ip`             | The user's IP address.                             |
| `city`           | City name.                                         |
| `postalCode`     | Postal code.                                       |
| `country`        | Full country name.                                 |
| `countryIsoCode` | ISO 3166-1 alpha-2 country code (e.g. `DE`, `US`). |
| `continent`      | Continent name.                                    |
| `latitude`       | Latitude coordinate.                               |
| `longitude`      | Longitude coordinate.                              |
| `accuracyRadius` | Accuracy radius in kilometres for the coordinates. |

Fields that cannot be resolved by the configured GeoIP provider will be `null`.

**Example claim** (configured with name `geoip`):

```json
{
  "geoip": {
    "ip": "2.125.160.217",
    "city": "Watlington",
    "postalCode": "OX49",
    "country": "United Kingdom",
    "countryIsoCode": "GB",
    "continent": "Europe",
    "latitude": 51.6242,
    "longitude": -1.0543,
    "accuracyRadius": 5
  }
}
```

---

## GeoAware user agent info

**Display name:** `GeoAware user agent info`

Parses the `User-Agent` header of the current user session and maps the detected device information as a structured claim.

The claim contains the following fields:

| Field        | Description                                                                                                                                               |
|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `os`         | Operating system name (e.g. `Windows`, `macOS`, `Android`).                                                                                               |
| `osVersion`  | Operating system version.                                                                                                                                 |
| `browser`    | Browser name and version as reported by the User-Agent header (e.g. `Firefox/148.0`, `Chrome/124.0`).                                                     |
| `deviceType` | Device category as reported by Keycloak (e.g. `Desktop`, `Mobile`, `Tablet`). May be `Other` if the device type cannot be determined from the User-Agent. |
| `mobile`     | Boolean indicating whether the device is a mobile device.                                                                                                 |

**Example claim** (configured with name `device`):

```json
{
  "device": {
    "os": "Windows",
    "osVersion": "11",
    "browser": "Chrome/124.0",
    "deviceType": "Other",
    "mobile": false
  }
}
```
