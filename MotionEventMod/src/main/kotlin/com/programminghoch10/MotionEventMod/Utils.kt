package com.programminghoch10.MotionEventMod

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.view.InputDevice
import android.view.MotionEvent


val toolTypeFields = MotionEvent::class.java.declaredFields.filter { it.name.startsWith("TOOL_TYPE_") && it.type == Int::class.java }
val sourceClassFields = InputDevice::class.java.declaredFields.filter { it.name.startsWith("SOURCE_CLASS_") && it.type == Int::class.java }
    .filter { it.name != "SOURCE_CLASS_MASK" }
val actionTypeFields =
    MotionEvent::class.java.declaredFields.filter { it.name.startsWith("ACTION_") && it.type == Int::class.java }.filter { it.name != "ACTION_MASK" }
fun typeEnabledKey(fieldName: String): String = "${fieldName.lowercase()}_enabled"

// thanks https://farhanpatel.dev/index.php/2020/06/14/deep-clones-with-android-parcelable/
// slightly modified for compatibility, extension functions and nullability
fun <T : Parcelable> T.deepClone(): T {
    var parcel = Parcel.obtain()
    parcel.writeParcelable(this, 0)
    parcel.setDataPosition(0)
    val clone = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        parcel.readParcelable(this::class.java.classLoader, this::class.java)
    } else {
        @Suppress("DEPRECATION") parcel.readParcelable(this::class.java.classLoader)
    }
    // it is important to recycle parcel and free up resources once done
    parcel.recycle()
    require(clone != null)
    return clone
}
