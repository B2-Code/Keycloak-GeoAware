# Usage

Once you have configured the GeoAware extension, you can start using its features to strengthen the security of your Keycloak authentication processes.
This section provides an overview of how to use the GeoAware authenticators effectively.

## How to Use GeoAware Authenticators
The GeoAware authenticators operate according to the condition and action model.
For each authenticator that you wish to use, you will need to configure a condition and one or more actions.
However, the configuration process is straightforward and can be completed via the Keycloak Admin Console.

## IP Authenticator
The IP Authenticator allows you to enforce geolocation-based access control during the authentication process.
Search for `GeoAware IP` in the list of authenticators when configuring your authentication flows.

### Conditions
You can choose from the following conditions to determine when the IP Authenticator should be triggered:
- **Always**: The authenticator will always be executed.
- **On IP change**: The authenticator will be executed only when the user's IP address has changed since their last login.
- **Unknown IP**: The authenticator will be executed only when the user's IP address is not recognized from previous logins.
- **Unknown location**: The authenticator will be executed only when the user's geolocation is not recognized from previous logins.
- **Never**: The authenticator will never be executed.

### Actions
You can choose from the following actions to define what happens when the IP Authenticator is triggered:
- **Notification email**: Sends a notification email to the user informing them of the login attempt from a new IP address or location.
- **Deny Access**: Denies access to the user, preventing them from logging in.
- **Log**: Logs the event.
- **Disable user**: Disables the user's account. This will prevent the user from logging in until an administrator re-enables the account.

## Device Authenticator
The Device Authenticator allows you to enforce device-based access control during the authentication process.
Search for `GeoAware Device` in the list of authenticators when configuring your authentication flows.

### Conditions
You can choose from the following conditions to determine when the Device Authenticator should be triggered:
- **Always**: The authenticator will always be executed.
- **Device changed**: The authenticator will be executed only when the user is logging in from a different device than their last login.
- **Unknown device**: The authenticator will be executed only when the user is logging in from a device that is not recognized from previous logins.
- **Never**: The authenticator will never be executed.

You can choose from the following actions to define what happens when the IP Authenticator is triggered:
- **Notification email**: Sends a notification email to the user informing them of the login attempt from a new IP address or location.
- **Deny Access**: Denies access to the user, preventing them from logging in.
- **Log**: Logs the event.
- **Disable user**: Disables the user's account. This will prevent the user from logging in until an administrator re-enables the account.
