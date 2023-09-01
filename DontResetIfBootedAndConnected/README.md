# Don't reset if booted and connected

This module solves a rather exotic problem. When a special condition related to storage partitions
(somehow related to mounting/unmounting/decrypting), all apps that are actively using those partitions it get killed.
In my case this always happened a minute or so after I unlocked my phone the first time after booting, 
basically resulting in a hard reset of all apps that already managed to start.
This module replaces the resetIfBootedAndConnected with a NOOP.

One side-effect is that direct access to `/sdcard` and alike, and work profiles can (temporarily) break because storage isn't available.

Clean flashing my phone eventually "solved" the issue.
