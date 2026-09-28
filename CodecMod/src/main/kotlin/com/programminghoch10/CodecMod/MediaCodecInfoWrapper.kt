package com.programminghoch10.CodecMod

import android.media.MediaCodecInfo
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * drop in replacement for MediaCodecInfo
 * with compatibility checks for older SDKs
 * 
 * @see MediaCodecInfo
 */
class MediaCodecInfoWrapper internal constructor(val originalMediaCodecInfo: MediaCodecInfo) {
    val canonicalName: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) originalMediaCodecInfo.canonicalName
        else originalMediaCodecInfo.name
    
    val isAlias: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) originalMediaCodecInfo.isAlias
        else false
    
    val name: String
        get() = originalMediaCodecInfo.name
    
    @get:RequiresApi(Build.VERSION_CODES.Q)
    val isHardwareAccelerated: Boolean
        get() = originalMediaCodecInfo.isHardwareAccelerated
    
    @get:RequiresApi(Build.VERSION_CODES.Q)
    val isSoftwareOnly: Boolean
        get() = originalMediaCodecInfo.isSoftwareOnly
    
    @get:RequiresApi(Build.VERSION_CODES.Q)
    val isVendor: Boolean
        get() = originalMediaCodecInfo.isVendor
    
    val isEncoder: Boolean
        get() = originalMediaCodecInfo.isEncoder
    
    val isDecoder: Boolean
        get() = !this.isEncoder
    
    val supportedTypes: Array<String?>?
        get() = originalMediaCodecInfo.getSupportedTypes()
}
