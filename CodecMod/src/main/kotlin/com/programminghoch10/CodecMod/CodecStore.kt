package com.programminghoch10.CodecMod

import java.util.*
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import de.robv.android.xposed.XSharedPreferences

class CodecStore {
    private val sharedPreferences: SharedPreferences
    private val receivers: MutableList<OnCodecPreferenceChangedListenerMeta> = LinkedList<OnCodecPreferenceChangedListenerMeta>()
    
    internal constructor(context: Context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_WORLD_READABLE)
    }
    
    internal constructor() {
        this.sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, PREFERENCES)
    }
    
    fun getCodecPreference(mediaCodecInfo: MediaCodecInfoWrapper): Boolean {
        return sharedPreferences.getBoolean(getKey(mediaCodecInfo), DEFAULT_VALUE)
    }
    
    fun setCodecPreference(mediaCodecInfo: MediaCodecInfoWrapper, enabled: Boolean): Boolean {
        val success = sharedPreferences.edit().apply {
            if (REMOVE_DEFAULT_VALUE_FROM_CONFIG && enabled == DEFAULT_VALUE) {
                remove(getKey(mediaCodecInfo))
            } else {
                putBoolean(getKey(mediaCodecInfo), enabled)
            }
        }.commit()
        if (!success) return false
        dispatchOnCodecPreferenceChanged(mediaCodecInfo, enabled)
        return true
    }
    
    fun registerOnCodecPreferenceChangedListener(
        mediaCodecInfo: MediaCodecInfoWrapper,
        onCodecPreferenceChangedListener: OnCodecPreferenceChangedListener,
    ) {
        val listener = OnCodecPreferenceChangedListenerMeta()
        listener.mediaCodecInfo = mediaCodecInfo
        listener.callback = onCodecPreferenceChangedListener
        receivers.add(listener)
    }
    
    private fun dispatchOnCodecPreferenceChanged(mediaCodecInfo: MediaCodecInfoWrapper, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            receivers.stream().filter { r: OnCodecPreferenceChangedListenerMeta? ->
                getKey(r!!.mediaCodecInfo!!) == getKey(mediaCodecInfo)
            }.forEach { r: OnCodecPreferenceChangedListenerMeta? -> r!!.callback!!.onCodecPreferenceChanged(enabled) }
        } else {
            for (receiver in receivers) {
                if (getKey(receiver.mediaCodecInfo!!) == getKey(mediaCodecInfo)) receiver.callback!!.onCodecPreferenceChanged(
                    enabled
                )
            }
        }
    }
    
    fun interface OnCodecPreferenceChangedListener {
        fun onCodecPreferenceChanged(value: Boolean)
    }
    
    private class OnCodecPreferenceChangedListenerMeta {
        var mediaCodecInfo: MediaCodecInfoWrapper? = null
        var callback: OnCodecPreferenceChangedListener? = null
    }
    
    companion object {
        const val DEFAULT_VALUE: Boolean = true
        private const val REMOVE_DEFAULT_VALUE_FROM_CONFIG = true
        private const val PREFERENCES = "codecs"
        fun getKey(mediaCodecInfo: MediaCodecInfoWrapper): String {
            return "codec_" + mediaCodecInfo.canonicalName
        }
    }
}
