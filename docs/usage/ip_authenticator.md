---
title: IP Authenticator
layout: default
parent: Usage
nav_order: 1
---

# IP Authenticator

The IP Authenticator allows you to enforce geolocation-based access control during the authentication process.
It evaluates the user's IP address and geographic location at login time and performs a configured action when a condition is met.

Search for `GeoAware IP` in the list of authenticators when configuring your authentication flows.

## Conditions

You can choose from the following conditions to determine when the IP Authenticator should be triggered:

- **Always**: The authenticator will always be executed.
- **On IP change**: The authenticator will be executed only when the user's IP address has changed since their last login.
- **Unknown IP**: The authenticator will be executed only when the user's IP address has never been seen in their login history.
- **Unknown location**: The authenticator will be executed only when the user's geolocation (country and city) has never been seen in their login history.
- **Never**: The authenticator will never be executed. This is useful for temporarily disabling the authenticator without removing it from the flow.

## Actions

You can choose from the following actions to define what happens when the IP Authenticator is triggered:

- **Notification email**: Sends a notification email to the user informing them of the login from a new IP address or location.
- **Deny Access**: Denies the login attempt immediately.
- **Log**: Logs the event without taking any further action. Useful for auditing or testing a condition before enforcing a stricter policy.
- **Disable user**: Disables the user's account. The user will not be able to log in again until an administrator re-enables the account.

## Limitations

IP-based geolocation relies on the accuracy of the configured GeoIP provider.
Logins from IP addresses that are not present in the database (for example, private or localhost addresses) will not carry location information,
and conditions such as **Unknown location** will always match for those addresses.
