# MotionEventMod

A simple XPosed mod to disable touch input for some seconds after the stylus was in use.

## What this is

When using a stylus, usually the touch display is disabled.
When putting the stylus aside, the touch display gets active again
and your palm may activate the touch panel, flinging the page away.
This makes continuous writing impossible.

This module disables touch input for some seconds after stylus usage
to prevent the palm from flinging the page away
in the short time you take to lift your palm up.
This way the stylus is properly usable.

## How to install

This module was developed and tested on `LineageOS 18.1`.
It should work on any Android `>= 4`.
It may **not** work on heavily modified Android versions,
like Samsungs `Touchwiz`/`OneUI`.

Just install it on your system.
If your Xposed Manager has an activation scope,
select the apps you want this mod enabled in.
