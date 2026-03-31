package de.binarynoise.HideAbi

import android.os.Build

enum class AbiCategory(val property: String, val getter: () -> Array<String>) {
    SUPPORTED_ABIS("ro.product.cpu.abilist", { Build.SUPPORTED_ABIS }),
    SUPPORTED_64_BIT_ABIS("ro.product.cpu.abilist64", { Build.SUPPORTED_64_BIT_ABIS }),
    SUPPORTED_32_BIT_ABIS("ro.product.cpu.abilist32", { Build.SUPPORTED_32_BIT_ABIS }),
    ;
    
    fun getPreferenceKeyFor(abi: String): String {
        return "${this.property}_$abi"
    }
    
    companion object {
        fun fromProperty(property: String): AbiCategory? {
            return entries.find { it.property == property }
        }
    }
}
