# OpenWifiOnTop

This Xposed module prioritizes open Wi-Fi networks in the Wi-Fi picker.

The order will be:

- currently connected
- saved && !open
- saved && open
- !saved && open
- !saved && !open
- other networks sorted by default
