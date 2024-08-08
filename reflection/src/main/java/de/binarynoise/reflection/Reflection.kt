@file:Suppress("ClassName")

package de.binarynoise.reflection

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> Class<T>.findDeclaredMethod(name: String, vararg params: Class<*>): Method {
    var c: Class<*>? = this
    while (c != null) {
        c.declaredMethods.filter { it.name == name }
            .firstOrNull() { params.isEmpty() || (it.parameterTypes.map { t -> t.name }) == (params.map { p -> p.name }) }
            ?.let { return it.makeAccessible() }
        c = c.superclass
    }
    
    throw NoSuchMethodException("$name(${params.joinToString { it.simpleName }})")
}

fun <T> Class<T>.findDeclaredField(name: String): Field {
    var c: Class<*>? = this
    while (c != null) {
        c.declaredFields.filter { it.name == name }.firstOrNull()?.let { return it.makeAccessible() }
        c = c.superclass
    }
    throw NoSuchFieldException(name)
}

fun <T : AccessibleObject> T.makeAccessible(): T = apply { isAccessible = true }

/**
 * Casts the object to T.
 * If T is nullable, a safe cast is performed.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> Any.cast(): T {
    return when {
        null is T -> {
            // T is nullable. Use safe cast.
            // Casting as T again is needed because compiler doesn't know in advance that T is nullable and complains otherwise.
            (this as? T) as T
        }
        else -> {
            // T is not nullable. Use direct cast.
            this as T
        }
    }
}
