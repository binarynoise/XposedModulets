# HideAbi

Hide specific CPU ABIs from apps.

This module hooks `android.os.Build.SUPPORTED_{,{64,32}_BIT_}ABIS` to filter out selected ABIs from being reported to applications.
Useful for controlling which architecture apps use or convincing an app store to download the correct apk.
