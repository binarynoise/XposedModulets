@file:Suppress("ClassName")

package de.binarynoise.reflection

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class field(private val cls: Class<*>) : ReadOnlyProperty<Any, Field> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Field {
        return cls.getDeclaredField(property.name.removeSuffix("Field")).makeAccessible()
    }
}

class method(private val cls: Class<*>) : ReadOnlyProperty<Any, Method> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Method {
        return cls.declaredMethods.first { it.name == property.name }.makeAccessible()
    }
}

fun <T : AccessibleObject> T.makeAccessible(): T = apply { isAccessible = true }
