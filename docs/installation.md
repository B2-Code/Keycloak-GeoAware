---
title: Installation
layout: default
nav_order: 2
---

## Download

### GitHub Releases

You can download all versions of the extension from the [list of releases][releases].
Download the JAR file listed under the `Assets` section of the release you want to use.

This JAR is a shaded (fat) JAR with all dependencies bundled. It is the recommended way to install the extension.

### Maven Central

The extension is also published to [Maven Central][maven-central] under the coordinates `org.b2code:keycloak-geoaware`.

Maven Central hosts two artifacts per release:

- The **slim JAR** (no classifier): for projects that compile against GeoAware's interfaces.
- The **shaded JAR** (classifier `shaded`): with all dependencies bundled, equivalent to the GitHub release JAR.

To download the shaded JAR for Keycloak installation:

```bash
mvn dependency:copy \
  -Dartifact=org.b2code:keycloak-geoaware:VERSION:jar:shaded \
  -DoutputDirectory=./providers
```

Replace `VERSION` with the version you want to install, e.g. `1.0.0`.

## Installation

To install the extension, copy the JAR file to the `providers` directory of your Keycloak installation and run the Keycloak build command.
Refer to the [official guide][keycloak guide] for detailed instructions.

### Docker

Keycloak's Quarkus distribution requires providers to be available during the build step, which optimises and bakes them into the runtime. Mounting a JAR at container startup is therefore not
sufficient. Instead, build a custom image that includes the provider and runs the build step:

```dockerfile
FROM quay.io/keycloak/keycloak:26
COPY keycloak-geoaware.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build
```

[keycloak guide]: https://www.keycloak.org/server/configuration-provider#_installing_and_uninstalling_a_provider

[releases]: https://github.com/B2-Code/Keycloak-GeoAware/releases

[maven-central]: https://central.sonatype.com/artifact/org.b2code/keycloak-geoaware
