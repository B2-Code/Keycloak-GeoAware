---
title: Usage
layout: default
nav_order: 4
has_children: true
---

# Usage

Once you have configured the GeoAware extension, you can start using its features to strengthen the security of your Keycloak authentication processes.

## Two Ways to Use GeoAware

GeoAware offers two complementary approaches to integrating geolocation and device awareness into your authentication flows.
Understanding the difference will help you choose the right tool for your use case.

### GeoAware Authenticators (condition + action model)

The [IP Authenticator](./ip_authenticator.md) and [Device Authenticator](./device_authenticator.md) are self-contained authenticators that bundle a **condition** and an **action** into a single step.
You add one of them to your flow, configure which condition should trigger it and what action to perform, and you are done.
This approach is ideal when the built-in actions (send a notification email, deny access, log, or disable the user) cover your requirements.

**Use this approach when:**

- You want a quick setup with minimal flow configuration.
- One of the built-in actions is sufficient for your use case.

### GeoAware Conditional Authenticators (Keycloak conditional sub-flow model)

GeoAware also provides a set of [standalone conditional authenticators](./conditional_authenticators.md) that plug directly into Keycloak's native
[conditional sub-flow](https://www.keycloak.org/docs/latest/server_admin/#conditions-in-conditional-flows) mechanism.
In this model, a conditional sub-flow contains two parts: a **condition** step that decides whether the sub-flow runs,
and one or more **action** steps that execute if the condition is met.
The GeoAware conditional authenticators act as the condition step, while you are free to choose any Keycloak built-in
or custom authenticator as the action.

**Use this approach when:**

- You need an action that is not covered by the built-in GeoAware actions, such as triggering an OTP challenge or a custom authenticator.
- You want to combine a GeoAware condition with multiple action steps inside the same sub-flow.
- You prefer to compose flows entirely from Keycloak-native building blocks.

## Token Mappers

In addition to authentication flow integration, GeoAware provides [OIDC protocol mappers](./token_mappers.md) that let you include geolocation and device information as claims in your access tokens,
ID tokens, and userinfo responses.
