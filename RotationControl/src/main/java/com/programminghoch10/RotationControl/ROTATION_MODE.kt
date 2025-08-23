package com.programminghoch10.RotationControl

import android.content.pm.ActivityInfo

enum class ROTATION_MODE(val key: String, val value: Int, val title: String, val summary: String) {
    // yoinked from https://developer.android.com/reference/android/R.attr.html#screenOrientation
    
    SCREEN_ORIENTATION_UNSET(
        "UNSET",
        -2,
        "UNSET",
        "Do not override screen orientation. " +
                "You might as well disable the module instead of using this option.",
    ),
    SCREEN_ORIENTATION_UNSPECIFIED(
        "UNSPECIFIED",
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
        "UNSPECIFIED",
        "No preference specified: " +
                "let the system decide the best orientation. " +
                "This will either be the orientation selected by the activity below, " +
                "or the user's preferred orientation if this activity is the bottom of a task. " +
                "If the user explicitly turned off sensor based orientation " +
                "through settings sensor based device rotation will be ignored. " +
                "If not by default sensor based orientation will be taken into account " +
                "and the orientation will changed based on how the user rotates the device.",
    ),
    SCREEN_ORIENTATION_LANDSCAPE(
        "LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
        "LANDSCAPE",
        "Would like to have the screen in a landscape orientation: " +
                "that is, with the display wider than it is tall, ignoring sensor data.",
    ),
    SCREEN_ORIENTATION_PORTRAIT(
        "PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
        "PORTRAIT",
        "Would like to have the screen in a portrait orientation: " +
                "that is, with the display taller than it is wide, ignoring sensor data.",
    ),
    SCREEN_ORIENTATION_USER(
        "USER",
        ActivityInfo.SCREEN_ORIENTATION_USER,
        "USER",
        "Use the user's current preferred orientation of the handset.",
    ),
    SCREEN_ORIENTATION_BEHIND(
        "BEHIND",
        ActivityInfo.SCREEN_ORIENTATION_BEHIND,
        "BEHIND",
        "Keep the screen in the same orientation as whatever is behind this activity.",
    ),
    SCREEN_ORIENTATION_SENSOR(
        "SENSOR",
        ActivityInfo.SCREEN_ORIENTATION_SENSOR,
        "SENSOR",
        "Orientation is determined by a physical orientation sensor: " +
                "the display will rotate based on how the user moves the device. " +
                "Ignores user's setting to turn off sensor-based rotation.",
    ),
    SCREEN_ORIENTATION_NOSENSOR(
        "NOSENSOR",
        ActivityInfo.SCREEN_ORIENTATION_NOSENSOR,
        "NOSENSOR",
        "Always ignore orientation determined by orientation sensor: " +
                "the display will not rotate when the user moves the device.",
    ),
    SCREEN_ORIENTATION_SENSOR_LANDSCAPE(
        "SENSOR_LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
        "SENSOR_LANDSCAPE",
        "Would like to have the screen in landscape orientation, " +
                "but can use the sensor to change which direction the screen is facing. ",
    ),
    SCREEN_ORIENTATION_SENSOR_PORTRAIT(
        "SENSOR_PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
        "SENSOR_PORTRAIT",
        "Would like to have the screen in portrait orientation, " +
                "but can use the sensor to change which direction the screen is facing.",
    ),
    SCREEN_ORIENTATION_REVERSE_LANDSCAPE(
        "REVERSE_LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
        "REVERSE_LANDSCAPE",
        "Would like to have the screen in landscape orientation, " +
                "turned in the opposite direction from normal landscape.",
    ),
    SCREEN_ORIENTATION_REVERSE_PORTRAIT(
        "REVERSE_PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
        "REVERSE_PORTRAIT",
        "Would like to have the screen in portrait orientation, " +
                "turned in the opposite direction from normal portrait.",
    ),
    SCREEN_ORIENTATION_FULL_SENSOR(
        "FULL_SENSOR",
        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
        "FULL_SENSOR",
        "Orientation is determined by a physical orientation sensor: " +
                "the display will rotate based on how the user moves the device. " +
                "This allows any of the 4 possible rotations, " +
                "regardless of what the device will normally do " +
                "(for example some devices won't normally use 180 degree rotation).",
    ),
    SCREEN_ORIENTATION_USER_LANDSCAPE(
        "USER_LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
        "USER_LANDSCAPE",
        "Would like to have the screen in landscape orientation, " +
                "but if the user has enabled sensor-based rotation " +
                "then we can use the sensor to change which direction the screen is facing.",
    ),
    SCREEN_ORIENTATION_USER_PORTRAIT(
        "USER_PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
        "USER_PORTRAIT",
        "Would like to have the screen in portrait orientation, " +
                "but if the user has enabled sensor-based rotation " +
                "then we can use the sensor to change which direction the screen is facing.",
    ),
    SCREEN_ORIENTATION_FULL_USER(
        "FULL_USER",
        ActivityInfo.SCREEN_ORIENTATION_FULL_USER,
        "FULL_USER",
        "Respect the user's sensor-based rotation preference, " +
                "but if sensor-based rotation is enabled " +
                "then allow the screen to rotate in all 4 possible directions " +
                "regardless of what the device will normally do " +
                "(for example some devices won't normally use 180 degree rotation).",
    ),
    SCREEN_ORIENTATION_LOCKED(
        "LOCKED",
        ActivityInfo.SCREEN_ORIENTATION_LOCKED,
        "LOCKED",
        "Screen is locked to its current rotation, whatever that is.",
    ),
}
