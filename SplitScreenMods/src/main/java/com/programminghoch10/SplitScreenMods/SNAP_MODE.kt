package com.programminghoch10.SplitScreenMods

// yoinked from https://github.com/LineageOS/android_frameworks_base/blob/2dc97cf3d6234c87497ca78b2734bb3ed604c349/libs/WindowManager/Shell/src/com/android/wm/shell/common/split/DividerSnapAlgorithm.java

enum class SNAP_MODE(val key: String, val value: Int) {
    
    SNAP_MODE_UNCHANGED(
        "SYSTEM",
        -1,
    ),
    
    SNAP_MODE_16_9(
        "16_9",
        0,
    ),
    
    SNAP_FIXED_RATIO(
        "FIXED",
        1,
    ),
    
    SNAP_ONLY_1_1(
        "1_1",
        2,
    ),
    
    /*
    While this target does work,
    it is clearly intended for "select a second task to split" mode,
    where only one task's icon is shown as a preview for split selection.
    */
    SNAP_MODE_MINIMIZED(
        "MINIMIZED",
        3,
    ),
    
    /*
    This target provides the offscreen ratios, 
    but the required functionality seems to be stripped out on current builds.
    The fallback ratio is 33%, which can be obtained with the SNAP_FIXED_RATIO as well.
    */
    SNAP_FLEXIBLE_SPLIT(
        "FLEXIBLE",
        4,
    ),
}
