---
title: Conditional Authenticators
layout: default
parent: Usage
nav_order: 3
---

# Conditional Authenticators

GeoAware provides a set of standalone conditional authenticators designed to be used as the condition step inside a Keycloak
[conditional sub-flow](https://www.keycloak.org/docs/latest/server_admin/#conditions-in-conditional-flows).

Unlike the [IP Authenticator](./ip_authenticator.md) and [Device Authenticator](./device_authenticator.md), which bundle a condition and an action together,
these authenticators only evaluate a condition and leave the choice of action entirely up to you.
Any Keycloak built-in or custom authenticator can be placed as the action step in the same sub-flow.

## Available Conditional Authenticators

| Authenticator                           | Condition                                                                             |
|-----------------------------------------|---------------------------------------------------------------------------------------|
| `Condition - GeoAware On IP change`     | The user's IP address has changed since their last login.                             |
| `Condition - GeoAware Unknown IP`       | The user's IP address has never been seen in their login history.                     |
| `Condition - GeoAware Unknown location` | The user's geolocation (country and city) has never been seen in their login history. |
| `Condition - GeoAware On device change` | The device differs from the one used in the user's last login.                        |
| `Condition - GeoAware Unknown device`   | The device has never been seen in the user's login history.                           |

## How to Set Up a Conditional Sub-flow

1. In the Keycloak Admin Console, navigate to **Authentication** and open or create a browser flow.
2. Add a new sub-flow and set its type to **Conditional**.
3. Inside the sub-flow, add a GeoAware conditional authenticator as a **CONDITIONAL** step. This is the condition that controls whether the rest of the sub-flow runs.
4. Add one or more action authenticators as **REQUIRED** steps inside the same sub-flow.

The action steps will only execute when the GeoAware condition is met.

## Example: Requiring OTP on Unknown Device

To require an OTP challenge whenever a user logs in from an unknown device:

1. Add a conditional sub-flow to your browser authentication flow.
2. Inside the sub-flow, add `Condition - GeoAware Unknown device` as a **CONDITIONAL** step.
3. Add `OTP Form` (or any other step-up authenticator) as a **REQUIRED** step inside the same sub-flow.

The OTP step will only be triggered when the device is unrecognized, leaving normal logins from known devices unaffected.

## Example: Blocking Logins from Unknown Locations

To deny access whenever a user logs in from a geolocation they have never used before:

1. Add a conditional sub-flow to your browser authentication flow.
2. Inside the sub-flow, add `Condition - GeoAware Unknown location` as a **CONDITIONAL** step.
3. Add the Keycloak built-in `Deny Access` authenticator as a **REQUIRED** step inside the same sub-flow.

This mirrors the behaviour of the IP Authenticator's **Deny Access** action but is composed entirely from Keycloak-native building blocks.
