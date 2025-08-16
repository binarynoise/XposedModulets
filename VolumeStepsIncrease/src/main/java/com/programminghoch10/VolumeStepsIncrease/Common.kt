package com.programminghoch10.VolumeStepsIncrease

class Common {
    companion object {
        val streams = arrayOf(
            "ro.config.vc_call_vol_steps",
            "ro.config.media_vol_steps",
            "ro.config.alarm_vol_steps",
            "ro.config.system_vol_steps",
        )
        
        val defaultStreamValues = mapOf<String, Int>(
            // default volumes from AudioService
            "ro.config.vc_call_vol_steps" to 5,
            "ro.config.media_vol_steps" to 15,
            "ro.config.alarm_vol_steps" to 7,
            "ro.config.system_vol_steps" to 7,
        ).mapValues { it.value * 2 }
        
        fun getMaxVolumeSteps(): Int {
            return defaultStreamValues.values.maxOf { it } * 3
        }
    }
}
