---
title: Installation
layout: default
nav_order: 1
---

## Download
You can download all versions of the extension from the [list of releases][releases].
Please download the JAR under the `Assets` section of the release you want to use.

## Installation
To install custom providers, you can follow the [official guide][keycloak guide].

In short, you need to copy the JAR file to the `providers` directory of your Keycloak installation and run the build command.

If you are using a Docker container, you can mount the JAR file to the `/opt/keycloak/providers` directory.

[keycloak guide]: https://www.keycloak.org/server/configuration-provider#_installing_and_uninstalling_a_provider
[releases]: https://github.com/B2-Code/Keycloak-GeoAware/releases