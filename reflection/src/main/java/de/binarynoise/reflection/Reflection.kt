@file:Suppress("ClassName")

package de.binarynoise.reflection

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KProperty

class field(private val cls: Class<*>) {
    operator fun getValue(thisRef: Nothing?, property: KProperty<*>): Field {
        return cls.getDeclaredField(property.name.removeSuffix("Field")).makeAccessible()
    }
    
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Field {
        return cls.getDeclaredField(property.name.removeSuffix("Field")).makeAccessible()
    }
}

class method(private val cls: Class<*>) {
    operator fun getValue(thisRef: Nothing?, property: KProperty<*>): Method {
        return cls.declaredMethods.first { it.name == property.name }.makeAccessible()
    }
    
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Method {
        return cls.declaredMethods.first { it.name == property.name }.makeAccessible()
    }
}

fun <T : AccessibleObject> T.makeAccessible(): T = apply { isAccessible = true }
