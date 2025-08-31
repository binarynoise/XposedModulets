package de.binarynoise.classHunter

import java.net.URLClassLoader
import java.security.SecureClassLoader
import android.os.Build
import android.util.Log
import dalvik.system.BaseDexClassLoader
import dalvik.system.DelegateLastClassLoader
import dalvik.system.DexClassLoader
import dalvik.system.InMemoryDexClassLoader
import dalvik.system.PathClassLoader
import de.binarynoise.ClassHunter.BuildConfig
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

const val TAG = "Hook"

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.d(TAG, "handleLoadPackage: ${lpparam.packageName}")
        
        val packageClassLoader: ClassLoader = lpparam.classLoader
        tryFindClass(packageClassLoader, "packageClassLoader", lpparam.packageName)
        
        val moduleClassLoader: ClassLoader = this::class.java.classLoader!!
        tryFindClass(moduleClassLoader, "moduleClassLoader", lpparam.packageName)
        
        val systemClassLoader: ClassLoader = ClassLoader.getSystemClassLoader()
        tryFindClass(systemClassLoader, "systemClassLoader", lpparam.packageName)
        
        XposedHelpers.findAndHookMethod(ClassLoader::class.java, "loadClass", String::class.java, Boolean::class.java, object : MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                with(param) {
                    val cls = result as? Class<*>? ?: return
                    if (!cls.isAssignableFrom(ClassLoader::class.java)) return
                    Log.i(TAG, "loadClass afterHookedMethod: loaded new classLoader class: ${cls.name}")
                }
            }
        })
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        @Suppress("RemoveExplicitTypeArguments") //
        val classLoaderClasses = mutableSetOf<Class<out ClassLoader>>(
            ClassLoader::class.java,
            SecureClassLoader::class.java,
            URLClassLoader::class.java,
            BaseDexClassLoader::class.java,
            PathClassLoader::class.java,
            InMemoryDexClassLoader::class.java,
            DexClassLoader::class.java,
            ClassLoader.getSystemClassLoader()::class.java, // BootClassLoader
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            classLoaderClasses.add(DelegateLastClassLoader::class.java)
        }
        
        try {
            @Suppress("UNCHECKED_CAST") // 
            classLoaderClasses.add(Class.forName("android.app.LoadedApk\$WarningContextClassLoader", false, null) as Class<out ClassLoader>)
        } catch (_: Throwable) {
        }
        
        // prevent nested classloader logs
        val constructorLock: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }
        
        classLoaderClasses.forEach { classLoaderClass ->
            try {
                XposedBridge.hookAllConstructors(classLoaderClass, object : MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        constructorLock.set(constructorLock.get()!! + 1)
                    }
                    
                    override fun afterHookedMethod(param: MethodHookParam) {
                        constructorLock.set(constructorLock.get()!! - 1)
                        if (constructorLock.get()!! > 0) return
                        constructorLock.remove()
                        with(param) {
                            val newClassLoader = thisObject as? ClassLoader? ?: return
                            val string = newClassLoader.toString()
                            
                            val ignored = arrayOf(
                                "dalvik.system.PathClassLoader[null]",
                                "dalvik.system.PathClassLoader[DexPathList[[],nativeLibraryDirectories=[/system/lib64, /system_ext/lib64]]]",
                                "LspModuleClassLoader[instantiating]",
                            )
                            if (string !in ignored) {
                                Log.v(TAG, "ClassLoader constructor: created new classLoader: ${newClassLoader.toObjectString()} $string")
                            }
                            
                            tryFindClass(newClassLoader, "constructor", lpparam.packageName)
                            
                        }
                    }
                })
//                Log.v(TAG, "initZygote: hooked constructor of ${classLoaderClass.name}")
            } catch (_: NoSuchMethodError) {
            } catch (_: NoSuchMethodException) {
            } catch (t: Throwable) {
                Log.w(TAG, "initZygote: failed to hook constructor of ${classLoaderClass.name}", t)
            }
        }
    }
    
    fun tryFindClass(classLoader: ClassLoader, classLoaderName: String, packageName: String) {
        BuildConfig.targetClass.forEach { className ->
            var cls: Class<*>? = null
            
            var classLoader: ClassLoader? = classLoader
            var successClassLoader: ClassLoader? = null
            var i = 0
            
            while (classLoader != null && i++ < 10) {
                try {
                    cls = Class.forName(className, false, classLoader)
                    successClassLoader = classLoader
                } catch (_: Throwable) {
                    break
                }
                classLoader = classLoader.parent
            }
            
            if (cls != null && successClassLoader != null) {
                logClass(cls, classLoaderName, successClassLoader, packageName)
            }
        }
    }
    
    private fun logClass(cls: Class<*>, classLoaderName: String, classLoader: ClassLoader, packageName: String) {
        val lines = mutableListOf<String>()
        lines.add("*".repeat(150))
        
        lines.add("found class: ${cls.name} in package $packageName by $classLoaderName: ${classLoader.toObjectString()} - $classLoader")
        
        lines.add(" - constructors:")
        cls.declaredConstructors.map { c -> "${c.name}(${c.parameters.joinToString(", ") { p -> p.name + " " + p.type.name }})" }
            .sorted()
            .forEach { c ->
                lines.add("   - $c")
            }
        
        lines.add(" - methods:")
        cls.declaredMethods.map { m -> "${m.returnType.name} ${m.name}(${m.parameters.joinToString(", ") { p -> p.name + " " + p.type.name }})" }
            .sorted()
            .forEach { m ->
                lines.add("   - $m")
            }
        
        lines.add(" - fields:")
        cls.declaredFields.map { f -> "${f.type.name} ${f.name}" }.sorted().forEach { f ->
            lines.add("   - $f")
        }
        
        lines.forEach {
            Log.i(TAG, it)
        }
    }
}

fun Any?.toObjectString(): String {
    if (this == null) return "null"
    return this::class.simpleName + "@" + Integer.toHexString(hashCode())
}
