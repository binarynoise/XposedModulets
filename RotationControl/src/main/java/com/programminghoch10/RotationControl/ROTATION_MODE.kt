package com.programminghoch10.RotationControl

import android.app.Application
import android.content.pm.ActivityInfo

val context = run {
    val applicationContext = Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null) as Application?
    if (applicationContext?.packageName == BuildConfig.APPLICATION_ID) return@run applicationContext.resources
    return@run null
}

private fun getString(id: Int): String {
    if (context == null) return ""
    return context.getString(id)
}

enum class ROTATION_MODE(val key: String, val value: Int, val title: String, val summary: String) {
    // yoinked from https://developer.android.com/reference/android/R.attr.html#screenOrientation
    
    SCREEN_ORIENTATION_UNSET(
        "UNSET",
        -2,
        getString(R.string.screen_orientation_unset_title),
        getString(R.string.screen_orientation_unset_summary),
    ),
    SCREEN_ORIENTATION_UNSPECIFIED(
        "UNSPECIFIED",
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
        getString(R.string.screen_orientation_unspecified_title),
        getString(R.string.screen_orientation_unspecified_summary),
    ),
    SCREEN_ORIENTATION_LANDSCAPE(
        "LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
        getString(R.string.screen_orientation_landscape_title),
        getString(R.string.screen_orientation_landscape_summary),
    ),
    SCREEN_ORIENTATION_PORTRAIT(
        "PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
        getString(R.string.screen_orientation_portrait_title),
        getString(R.string.screen_orientation_portrait_summary),
    ),
    SCREEN_ORIENTATION_USER(
        "USER",
        ActivityInfo.SCREEN_ORIENTATION_USER,
        getString(R.string.screen_orientation_user_title),
        getString(R.string.screen_orientation_user_summary),
    ),
    SCREEN_ORIENTATION_BEHIND(
        "BEHIND",
        ActivityInfo.SCREEN_ORIENTATION_BEHIND,
        getString(R.string.screen_orientation_behind_title),
        getString(R.string.screen_orientation_behind_summary),
    ),
    SCREEN_ORIENTATION_SENSOR(
        "SENSOR",
        ActivityInfo.SCREEN_ORIENTATION_SENSOR,
        getString(R.string.screen_orientation_sensor_title),
        getString(R.string.screen_orientation_sensor_summary),
    ),
    SCREEN_ORIENTATION_NOSENSOR(
        "NOSENSOR",
        ActivityInfo.SCREEN_ORIENTATION_NOSENSOR,
        getString(R.string.screen_orientation_nosensor_title),
        getString(R.string.screen_orientation_nosensor_summary),
    ),
    SCREEN_ORIENTATION_SENSOR_LANDSCAPE(
        "SENSOR_LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
        getString(R.string.screen_orientation_sensor_landscape_title),
        getString(R.string.screen_orientation_sensor_landscape_summary),
    ),
    SCREEN_ORIENTATION_SENSOR_PORTRAIT(
        "SENSOR_PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
        getString(R.string.screen_orientation_sensor_portrait_title),
        getString(R.string.screen_orientation_sensor_portrait_summary),
    ),
    SCREEN_ORIENTATION_REVERSE_LANDSCAPE(
        "REVERSE_LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
        getString(R.string.screen_orientation_reverse_landscape_title),
        getString(R.string.screen_orientation_reverse_landscape_summary),
    ),
    SCREEN_ORIENTATION_REVERSE_PORTRAIT(
        "REVERSE_PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
        getString(R.string.screen_orientation_reverse_portrait_title),
        getString(R.string.screen_orientation_reverse_portrait_summary),
    ),
    SCREEN_ORIENTATION_FULL_SENSOR(
        "FULL_SENSOR",
        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
        getString(R.string.screen_orientation_full_sensor_title),
        getString(R.string.screen_orientation_full_sensor_summary),
    ),
    SCREEN_ORIENTATION_USER_LANDSCAPE(
        "USER_LANDSCAPE",
        ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
        getString(R.string.screen_orientation_user_landscape),
        getString(R.string.screen_orientation_user_landscape_summary),
    ),
    SCREEN_ORIENTATION_USER_PORTRAIT(
        "USER_PORTRAIT",
        ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
        getString(R.string.screen_orientation_user_portrait_title),
        getString(R.string.screen_orientation_user_portrait_summary),
    ),
    SCREEN_ORIENTATION_FULL_USER(
        "FULL_USER",
        ActivityInfo.SCREEN_ORIENTATION_FULL_USER,
        getString(R.string.screen_orientation_full_user_title),
        getString(R.string.screen_orientation_full_user_summary),
    ),
    SCREEN_ORIENTATION_LOCKED(
        "LOCKED",
        ActivityInfo.SCREEN_ORIENTATION_LOCKED,
        getString(R.string.screen_orientation_locked_title),
        getString(R.string.screen_orientation_locked_summary),
    ),
}
