---
title: Device Authenticator
layout: default
parent: Usage
nav_order: 2
---

# Device Authenticator

The Device Authenticator allows you to enforce device-based access control during the authentication process.
It inspects the user's browser and operating system at login time and performs a configured action when a condition is met.

Search for `GeoAware Device` in the list of authenticators when configuring your authentication flows.

## Conditions

You can choose from the following conditions to determine when the Device Authenticator should be triggered:

- **Always**: The authenticator will always be executed.
- **On device change**: The authenticator will be executed only when the device (based on browser and operating system) differs from the one used in the user's last login.
- **Unknown device**: The authenticator will be executed only when the device has never been seen in the user's login history.
- **Never**: The authenticator will never be executed. This is useful for temporarily disabling the authenticator without removing it from the flow.

## Actions

You can choose from the following actions to define what happens when the Device Authenticator is triggered:

- **Notification email**: Sends a notification email to the user informing them of the login from a new or changed device.
- **Deny Access**: Denies the login attempt immediately.
- **Log**: Logs the event without taking any further action. Useful for auditing or testing a condition before enforcing a stricter policy.
- **Disable user**: Disables the user's account. The user will not be able to log in again until an administrator re-enables the account.

## Limitations

Device detection is based on the `User-Agent` header sent by the browser.
This header can be spoofed and does not uniquely identify a physical device.
It reflects the browser and operating system combination, so a user switching browsers on the same machine will appear as a different device.
