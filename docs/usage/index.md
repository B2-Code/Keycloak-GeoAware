---
title: Usage
layout: default
nav_order: 4
---

# Usage

Once you have configured the GeoAware extension, you can start using its authenticators to strengthen the security of your Keycloak authentication flows.

## Prerequisites

Before adding any GeoAware authenticators, make sure you have:

1. **Configured a GeoIP provider.** See the [Configuration](../configuration/index.md) section.
2. **Activated the login tracker event listener.** Without this, GeoAware has no history to compare against and conditions like *Unknown IP* or *Unknown location* will always evaluate to true.

To activate the event listener, go to your realm in the Admin Console:

- Navigate to **Realm settings** → **Events** → **Event listeners**
- Add `geoaware-login-tracker` to the list

## Adding GeoAware Authenticators to a Flow

Both authenticators are added to authentication flows through the Keycloak Admin Console.

1. Navigate to **Authentication** → **Flows**
2. Select the flow you want to modify (e.g. *browser*), or create a new one
3. Click **Add step** and search for `GeoAware IP` or `GeoAware Device`
4. Set the requirement to **Required** or **Conditional** depending on your use case
5. Click the settings icon next to the authenticator to configure it

## GeoAware IP Authenticator

The IP authenticator evaluates the user's current IP address against their login history and optionally their geolocation.

### Conditions

Choose one condition that determines when the authenticator's actions are executed:

| Condition            | Description                                                                                                                                                                                                                                                                          |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Always**           | Actions are always executed.                                                                                                                                                                                                                                                         |
| **On IP change**     | Actions are executed when the user's IP address has changed since their last login.                                                                                                                                                                                                  |
| **Unknown IP**       | Actions are executed when the IP address has never been seen in the user's login history.                                                                                                                                                                                            |
| **Unknown location** | Actions are executed when the user's geographic location (based on their IP) is not recognised from previous logins. Location comparison uses accuracy radius overlap: if the circles around the current and last known location do not overlap, the location is considered unknown. |
| **Never**            | Actions are never executed. Useful for temporarily disabling the authenticator without removing it.                                                                                                                                                                                  |

### Actions

Choose one or more actions to execute when the condition is met:

| Action                 | Description                                                                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **Notification email** | Sends an alert email to the user informing them of a login from a new IP address or location. Includes IP, location, browser, and OS details. |
| **Deny access**        | Blocks the login attempt and returns an unauthorised error.                                                                                   |
| **Log**                | Logs a warning to the application log. Useful for auditing without disrupting users.                                                          |
| **Disable user**       | Disables the user's account. An administrator must re-enable the account before the user can log in again.                                    |

Multiple actions can be selected and they are all executed when the condition is triggered.

## GeoAware Device Authenticator

The device authenticator evaluates the user's current device (identified by the User-Agent header) against their login history.

### Conditions

| Condition          | Description                                                                                     |
|--------------------|-------------------------------------------------------------------------------------------------|
| **Always**         | Actions are always executed.                                                                    |
| **Device changed** | Actions are executed when the user's device is different from the one used in their last login. |
| **Unknown device** | Actions are executed when the device has never been seen in the user's login history.           |
| **Never**          | Actions are never executed.                                                                     |

### Actions

| Action                 | Description                                                                                                    |
|------------------------|----------------------------------------------------------------------------------------------------------------|
| **Notification email** | Sends an alert email to the user informing them of a login from a new device. Includes browser and OS details. |
| **Deny access**        | Blocks the login attempt.                                                                                      |
| **Log**                | Logs a warning to the application log.                                                                         |
| **Disable user**       | Disables the user's account.                                                                                   |

## Email Notifications

When a **Notification email** action is configured, GeoAware sends an HTML and plain-text email to the user.

For this to work, you must have SMTP configured in your realm under **Realm settings** → **Email**.

The email includes:

- The date and time of the login
- The IP address, city, and country (for IP alerts)
- The browser and operating system (for both IP and device alerts)
- A link to reset credentials (if password reset is enabled for the realm), or a message to contact an administrator

Emails are available in **English** and **German**. The language used matches the user's locale.

## Combining Authenticators

You can add both the IP and device authenticators to the same flow. They are evaluated independently, so each one will execute its own actions if its condition is met.

**Example configuration:**

| Authenticator   | Condition        | Action                  |
|-----------------|------------------|-------------------------|
| GeoAware IP     | Unknown location | Deny access             |
| GeoAware Device | Unknown device   | Notification email, Log |

In this example, a login from an unknown location is denied outright, while a login from an unknown device triggers a notification and a log entry but still allows access.
