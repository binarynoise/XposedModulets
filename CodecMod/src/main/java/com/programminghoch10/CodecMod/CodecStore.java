package com.programminghoch10.CodecMod;

import static android.content.Context.MODE_WORLD_READABLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.XSharedPreferences;

public class CodecStore {
    static final boolean DEFAULT_VALUE = true;
    private static final boolean REMOVE_DEFAULT_VALUE_FROM_CONFIG = true;
    private static final String PREFERENCES = "codecs";
    SharedPreferences sharedPreferences;
    List<OnCodecPreferenceChangedListenerMeta> receivers = new LinkedList<>();
    
    CodecStore(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_WORLD_READABLE);
    }
    
    CodecStore() {
        this.sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, PREFERENCES);
    }
    
    static String getKey(MediaCodecInfoWrapper mediaCodecInfo) {
        return "codec_" + mediaCodecInfo.getCanonicalName();
    }
    
    boolean getCodecPreference(MediaCodecInfoWrapper mediaCodecInfo) {
        return sharedPreferences.getBoolean(getKey(mediaCodecInfo), DEFAULT_VALUE);
    }
    
    boolean setCodecPreference(MediaCodecInfoWrapper mediaCodecInfo, boolean enabled) {
        boolean success = false;
        if (REMOVE_DEFAULT_VALUE_FROM_CONFIG && enabled == DEFAULT_VALUE) {
            success = sharedPreferences.edit().remove(getKey(mediaCodecInfo)).commit();
        } else {
            success = sharedPreferences.edit().putBoolean(getKey(mediaCodecInfo), enabled).commit();
        }
        if (!success)
            return success;
        dispatchOnCodecPreferenceChanged(mediaCodecInfo, enabled);
        return success;
    }
    
    void registerOnCodecPreferenceChangedListener(MediaCodecInfoWrapper mediaCodecInfo, OnCodecPreferenceChangedListener onCodecPreferenceChangedListener) {
        OnCodecPreferenceChangedListenerMeta listener = new OnCodecPreferenceChangedListenerMeta();
        listener.mediaCodecInfo = mediaCodecInfo;
        listener.callback = onCodecPreferenceChangedListener;
        receivers.add(listener);
    }
    
    private void dispatchOnCodecPreferenceChanged(MediaCodecInfoWrapper mediaCodecInfo, boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            receivers.stream()
                    .filter(r -> getKey(r.mediaCodecInfo).equals(getKey(mediaCodecInfo)))
                    .forEach(r -> r.callback.onCodecPreferenceChanged(enabled));
        } else {
            for (OnCodecPreferenceChangedListenerMeta receiver : receivers) {
                if (getKey(receiver.mediaCodecInfo).equals(getKey(mediaCodecInfo)))
                    receiver.callback.onCodecPreferenceChanged(enabled);
            }
        }
    }
    
    interface OnCodecPreferenceChangedListener {
        void onCodecPreferenceChanged(boolean value);
    }
    
    private class OnCodecPreferenceChangedListenerMeta {
        MediaCodecInfoWrapper mediaCodecInfo;
        OnCodecPreferenceChangedListener callback;
    }
    
}
