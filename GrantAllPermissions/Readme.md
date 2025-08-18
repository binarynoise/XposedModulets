# GrantAllPermissions

Make Android grant all permissions requested by apps.

This module grants all permissions from system-side when applied to `android`
and pretends all permissions are granted when applied to individual apps.

Please note:
- This module should only be used for development and testing purposes
  and should never be run on production devices.
- This module will grant the 
  [notification permission](https://developer.android.com/develop/ui/views/notifications/notification-permission)
  and thus all apps will be able to send notifications.
