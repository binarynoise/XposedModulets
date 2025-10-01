package com.programminghoch10.VolumeStepsIncrease

import com.programminghoch10.VolumeStepsIncrease.StockValues.MAX_STREAM_VOLUME
import com.programminghoch10.VolumeStepsIncrease.StockValues.MIN_STREAM_VOLUME
import com.programminghoch10.VolumeStepsIncrease.StockValues.STREAM_ALARM
import com.programminghoch10.VolumeStepsIncrease.StockValues.STREAM_MUSIC
import com.programminghoch10.VolumeStepsIncrease.StockValues.STREAM_SYSTEM
import com.programminghoch10.VolumeStepsIncrease.StockValues.STREAM_VOICE_CALL

object Common {
    val SHARED_PREFERENCES_NAME = "streams"
    
    val STREAMS = StockValues::class.java.declaredFields.filter { it.name.startsWith("STREAM_") }.associate { it.name to it.getInt(null) }
    
    val systemPropertyToStream = mapOf(
        "ro.config.vc_call_vol_steps" to STREAM_VOICE_CALL,
        "ro.config.media_vol_steps" to STREAM_MUSIC,
        "ro.config.alarm_vol_steps" to STREAM_ALARM,
        "ro.config.system_vol_steps" to STREAM_SYSTEM,
    )
    val streamsToSystemProperties = systemPropertyToStream.entries.associate { it.value to it.key }
    
    val moduleDefaultVolumeSteps = mapOf(
        STREAM_MUSIC to MAX_STREAM_VOLUME[STREAM_MUSIC] * 2,
        STREAM_VOICE_CALL to MAX_STREAM_VOLUME[STREAM_VOICE_CALL] * 2,
    )
    
    fun getSystemMaxVolumeSteps(stream: Int): Int {
        val default = MAX_STREAM_VOLUME[stream]
        if (streamsToSystemProperties.contains(stream)) {
            val systemProperty = streamsToSystemProperties[stream]
            try {
                val SystemPropertiesClass = Class.forName("android.os.SystemProperties")
                val getIntMethod = SystemPropertiesClass.getMethod("getInt", String::class.java, Int::class.java)
                return getIntMethod.invoke(null, systemProperty, default) as Int
            } catch (_: Exception) {
            }
        }
        return default
    }
    
    fun getModuleDefaultVolumeSteps(stream: Int): Int {
        return moduleDefaultVolumeSteps[stream] ?: getSystemMaxVolumeSteps(stream)
    }
    
    fun getModuleMaxVolumeSteps(stream: Int): Int {
        return getSystemMaxVolumeSteps(stream) * 3
    }
    
    fun getSystemMinVolumeSteps(stream: Int): Int {
        return MIN_STREAM_VOLUME[stream]
    }
    
    fun getModuleMinVolumeSteps(stream: Int): Int {
        return getSystemMinVolumeSteps((stream))
    }
    
    fun getPreferenceKey(stream: Int): String {
        return "STREAM_${stream}"
    }
}
