---
title: Home
layout: home
nav_order: 1
---

[Keycloak-GeoAware][GitHub] is an extension for [Keycloak][Keycloak] that integrates geolocation and device monitoring into the authentication process.
It allows you to define access policies based on a user's IP address, geographic location, and device, and to alert users when suspicious login activity is detected.

## What it does

GeoAware adds two configurable authenticators to your Keycloak authentication flows:

- **GeoAware IP**: evaluates the user's IP address and geographic location against their login history. You can block logins from unknown locations, send alert emails, or disable compromised accounts.
- **GeoAware Device**: detects the user's device from the User-Agent header. You can trigger actions when a login comes from an unknown or changed device.

Both authenticators use a **condition-action** model: you choose when they trigger and what they do.

## How it works

GeoAware tracks login events in a persistent database. Each time a user logs in, their IP address, geolocation (city, country, coordinates), and device information are recorded. On the next login, the
authenticators compare the current session against this history to evaluate the configured condition.

Geolocation data is provided by an external GeoIP service. Several providers are supported. See the [Configuration](configuration/index.md) section for details.

## Getting started

1. [Install](installation.md) the extension into your Keycloak instance
2. [Configure](configuration/index.md) a GeoIP provider
3. [Use](usage/index.md) the authenticators in your authentication flows

[GitHub]: https://github.com/B2-Code/Keycloak-GeoAware

[Keycloak]: https://www.keycloak.org
