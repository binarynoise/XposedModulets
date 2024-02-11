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

class method(private val cls: Class<*>, private vararg val params: Class<*>) {
    operator fun getValue(thisRef: Nothing?, property: KProperty<*>): Method {
        return getMethod(property)
    }
    
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Method {
        return getMethod(property)
    }
    
    private fun getMethod(property: KProperty<*>): Method {
        val methods = mutableListOf<Method>()
        var c: Class<*>? = cls
        while (c != null) {
            methods += c.declaredMethods
            c = c.superclass
        }
        
        return methods.filter { it.name == property.name.removeSuffix("Method") }
            .first { params.isEmpty() || (it.parameterTypes.map { t -> t.name }) == (params.map { p -> p.name }) }
            .makeAccessible()
    }
}

fun <T : AccessibleObject> T.makeAccessible(): T = apply { isAccessible = true }
