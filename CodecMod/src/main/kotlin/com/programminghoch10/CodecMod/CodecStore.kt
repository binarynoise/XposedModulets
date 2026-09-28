package com.programminghoch10.CodecMod

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceDataStore
import de.robv.android.xposed.XSharedPreferences

class CodecStore : PreferenceDataStore {
    private val sharedPreferences: SharedPreferences
    private val receivers: MutableList<OnCodecPreferenceChangedListenerMeta> = mutableListOf()
    
    constructor(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_WORLD_READABLE)
    }
    
    constructor() {
        sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, PREFERENCES)
    }
    
    fun getCodecPreference(mediaCodecInfo: MediaCodecInfoWrapper): Boolean {
        return getBoolean(getKey(mediaCodecInfo), DEFAULT_VALUE)
    }
    
    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }
    
    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit(commit = true) {
            if (REMOVE_DEFAULT_VALUE_FROM_CONFIG && value == DEFAULT_VALUE) {
                remove(key)
            } else {
                putBoolean(key, value)
            }
        }
        dispatchOnCodecPreferenceChanged(key, value)
    }
    
    fun registerOnCodecPreferenceChangedListener(
        mediaCodecInfo: MediaCodecInfoWrapper,
        onCodecPreferenceChangedListener: OnCodecPreferenceChangedListener,
    ) {
        val listener = OnCodecPreferenceChangedListenerMeta(
            mediaCodecInfo,
            onCodecPreferenceChangedListener,
        )
        receivers.add(listener)
    }
    
    private fun dispatchOnCodecPreferenceChanged(key: String, enabled: Boolean) {
        receivers.filter { getKey(it.mediaCodecInfo) == key }.forEach { it.callback.onCodecPreferenceChanged(enabled) }
    }
    
    fun interface OnCodecPreferenceChangedListener {
        fun onCodecPreferenceChanged(value: Boolean)
    }
    
    private data class OnCodecPreferenceChangedListenerMeta(
        val mediaCodecInfo: MediaCodecInfoWrapper,
        val callback: OnCodecPreferenceChangedListener,
    )
    
    companion object {
        const val DEFAULT_VALUE: Boolean = true
        private const val REMOVE_DEFAULT_VALUE_FROM_CONFIG = true
        private const val PREFERENCES = "codecs"
        fun getKey(mediaCodecInfo: MediaCodecInfoWrapper): String {
            return "codec_" + mediaCodecInfo.canonicalName
        }
    }
}
