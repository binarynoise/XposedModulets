@file:Suppress("unused", "UNUSED_PARAMETER", "UnusedReceiverParameter")

package de.binarynoise.logger

import java.util.Collections.*
import android.view.View

object Logger {
    fun log(message: CharSequence) {}
    fun log(message: CharSequence, t: Throwable?) {}
    fun Any?.dump(name: String, forceInclude: Set<Any> = emptySet(), forceIncludeClasses: Set<Class<*>> = emptySet()) {}
    fun View.dump(indent: Int = 0) {}
}
