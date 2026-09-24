package com.programminghoch10.MotionEventMod

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

// thanks https://farhanpatel.dev/index.php/2020/06/14/deep-clones-with-android-parcelable/
// slightly modified for compatibility, extension functions and nullability
fun <T : Parcelable> T.deepClone(): T {
    val parcel = Parcel.obtain()
    val clone = try {
        parcel.writeParcelable(this, 0)
        parcel.setDataPosition(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(this::class.java.classLoader, this::class.java)
        } else {
            @Suppress("DEPRECATION") parcel.readParcelable(this::class.java.classLoader)
        }
    } finally {
        // it is important to recycle parcel and free up resources once done
        parcel.recycle()
    }
    require(clone != null)
    return clone
}
