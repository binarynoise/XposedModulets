package com.programminghoch10.CodecMod

import android.content.Context
import android.media.MediaCodecInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.preference.SwitchPreference

class MediaCodecPreference(context: Context, codecStore: CodecStore, val mediaCodecInfoWrapper: MediaCodecInfoWrapper) : SwitchPreference(context) {
    
    constructor(context: Context, codecStore: CodecStore, mediaCodecInfo: MediaCodecInfo) : this(
        context, codecStore, MediaCodecInfoWrapper(mediaCodecInfo)
    )
    
    private lateinit var filterSpec: FilterSpec
    fun setFilterSpec(filterSpec: FilterSpec) {
        this.filterSpec = filterSpec
        isVisible = when {
            !filterSpec.showDecoders && !mediaCodecInfoWrapper.isEncoder -> false
            !filterSpec.showEncoders && mediaCodecInfoWrapper.isEncoder -> false
            !filterSpec.showAliases && mediaCodecInfoWrapper.isAlias -> false
            !filterSpec.showVideoCodecs && mediaCodecInfoWrapper.isVideoCodec -> false
            !filterSpec.showAudioCodecs && mediaCodecInfoWrapper.isAudioCodec -> false
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !filterSpec.showHardwareCodecs && mediaCodecInfoWrapper.isHardwareAccelerated -> false
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !filterSpec.showSoftwareCodecs && mediaCodecInfoWrapper.isSoftwareOnly -> false
            else -> true
        }
        notifyChanged() // to update summary
    }
    
    init {
        preferenceDataStore = codecStore
        setDefaultValue(CodecStore.DEFAULT_VALUE)
        key = CodecStore.getKey(mediaCodecInfoWrapper)
        codecStore.registerOnCodecPreferenceChangedListener(mediaCodecInfoWrapper) { value -> isChecked = value }
    }
    
    override fun getTitle(): CharSequence {
        val stringBuilder = StringBuilder()
        stringBuilder.append(mediaCodecInfoWrapper.name)
        if (mediaCodecInfoWrapper.name != mediaCodecInfoWrapper.canonicalName) stringBuilder.append(" (${mediaCodecInfoWrapper.canonicalName})")
        return stringBuilder
    }
    
    override fun getSummary(): CharSequence {
        if (!this::filterSpec.isInitialized) return "awaiting ${FilterSpec::class.simpleName}"
        val summaryBuilder = StringBuilder()
        summaryBuilder.append(
            String.format(context.getString(R.string.supported_types), mediaCodecInfoWrapper.supportedTypes.contentToString())
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (filterSpec.showHardwareCodecs && filterSpec.showSoftwareCodecs) {
                summaryBuilder.append("\n")
                summaryBuilder.append(String.format(context.getString(R.string.hardware_accelerated), mediaCodecInfoWrapper.isHardwareAccelerated))
                summaryBuilder.append("\n")
                summaryBuilder.append(String.format(context.getString(R.string.software_only), mediaCodecInfoWrapper.isSoftwareOnly))
            }
            if (filterSpec.showAliases) {
                summaryBuilder.append("\n")
                summaryBuilder.append(String.format(context.getString(R.string.alias), mediaCodecInfoWrapper.isAlias))
            }
            summaryBuilder.append("\n")
            summaryBuilder.append(String.format(context.getString(R.string.vendor), mediaCodecInfoWrapper.isVendor))
        }
        return summaryBuilder
    }
    
    data class FilterSpec(
        val showEncoders: Boolean,
        val showDecoders: Boolean,
        val showAliases: Boolean,
        val showVideoCodecs: Boolean,
        val showAudioCodecs: Boolean,
        @RequiresApi(Build.VERSION_CODES.Q) val showHardwareCodecs: Boolean,
        @RequiresApi(Build.VERSION_CODES.Q) val showSoftwareCodecs: Boolean,
    )
}
