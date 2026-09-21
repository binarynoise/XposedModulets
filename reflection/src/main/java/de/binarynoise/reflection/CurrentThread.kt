package de.binarynoise.reflection

import java.lang.reflect.Method

val currentStackTrace: Array<out StackTraceElement> get() = Thread.currentThread().stackTrace

fun StackTraceElement.isFromClass(className: String): Boolean = this.className == className
fun StackTraceElement.isFromClass(cls: Class<*>): Boolean = isFromClass(cls.name)
fun StackTraceElement.isFromMethod(method: String): Boolean = methodName == method
fun StackTraceElement.isFromMethod(method: Method): Boolean = isFromClass(method.declaringClass) && isFromMethod(method.name)


/**
 * returns true if the given class is present anywhere in the current thread's stack trace
 */
fun isCurrentThreadCalledFromClass(cls: Class<*>): Boolean = currentStackTrace.any { it.isFromClass(cls) }

/**
 * returns true if the given method is present anywhere in the current thread's stack trace
 */
fun isCurrentThreadCalledFromMethod(method: Method): Boolean = currentStackTrace.any { it.isFromMethod(method) }
