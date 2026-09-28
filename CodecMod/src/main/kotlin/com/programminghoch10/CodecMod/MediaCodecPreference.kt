package com.programminghoch10.CodecMod

import java.lang.String
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.text.StringBuilder
import kotlin.toString
import android.content.Context
import android.media.MediaCodecInfo
import android.os.Build
import androidx.preference.SwitchPreference
import com.programminghoch10.CodecMod.SettingsActivity.SettingsFragment.Companion.SHOW_ALIASES

class MediaCodecPreference(context: Context, codecStore: CodecStore, val mediaCodecInfoWrapper: MediaCodecInfoWrapper) : SwitchPreference(context) {
    
    constructor(context: Context, codecStore: CodecStore, mediaCodecInfo: MediaCodecInfo) : this(
        context, codecStore, MediaCodecInfoWrapper(mediaCodecInfo)
    )
    
    init {
        isPersistent = false
        setDefaultValue(CodecStore.DEFAULT_VALUE)
        key = CodecStore.getKey(mediaCodecInfoWrapper)
        
        setOnPreferenceChangeListener { _, newValue ->
            codecStore.setCodecPreference(
                mediaCodecInfoWrapper, newValue as Boolean
            )
        }
        
        codecStore.registerOnCodecPreferenceChangedListener(
            mediaCodecInfoWrapper, { value ->
                if (isChecked != value) setChecked(value)
            })
        
        title = mediaCodecInfoWrapper.name
        if (mediaCodecInfoWrapper.name != mediaCodecInfoWrapper.canonicalName) title = title.toString() + "(${mediaCodecInfoWrapper.canonicalName})"
        
        isChecked = codecStore.getCodecPreference(mediaCodecInfoWrapper)
    }
    
    override fun getSummary(): CharSequence {
        val summaryBuilder = StringBuilder()
        summaryBuilder.append(
            String.format(context.getString(R.string.supported_types), mediaCodecInfoWrapper.supportedTypes.contentToString())
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            summaryBuilder.append("\n")
            summaryBuilder.append(
                String.format(context.getString(R.string.hardware_accelerated), mediaCodecInfoWrapper.isHardwareAccelerated)
            )
            summaryBuilder.append("\n")
            summaryBuilder.append(String.format(context.getString(R.string.software_only), mediaCodecInfoWrapper.isSoftwareOnly))
            if (SHOW_ALIASES) {
                summaryBuilder.append("\n")
                summaryBuilder.append(String.format(context.getString(R.string.alias), mediaCodecInfoWrapper.isAlias))
            }
            summaryBuilder.append("\n")
            summaryBuilder.append(String.format(context.getString(R.string.vendor), mediaCodecInfoWrapper.isVendor))
        }
        return summaryBuilder
    }
}
