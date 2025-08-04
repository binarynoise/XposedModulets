package com.programminghoch10.CodecMod;

import android.media.MediaCodecInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * drop in replacement for MediaCodecInfo
 * with compatibility checks for older SDKs
 *
 * @see MediaCodecInfo
 */
public class MediaCodecInfoWrapper {
    private final MediaCodecInfo mediaCodecInfo;
    
    MediaCodecInfoWrapper(MediaCodecInfo mediaCodecInfo) {
        this.mediaCodecInfo = mediaCodecInfo;
    }
    
    public MediaCodecInfo getOriginalMediaCodecInfo() {
        return mediaCodecInfo;
    }
    
    public String getCanonicalName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return mediaCodecInfo.getCanonicalName();
        }
        return mediaCodecInfo.getName();
    }
    
    public boolean isAlias() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return mediaCodecInfo.isAlias();
        }
        return false;
    }
    
    public String getName() {
        return mediaCodecInfo.getName();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isHardwareAccelerated() {
        return mediaCodecInfo.isHardwareAccelerated();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isSoftwareOnly() {
        return mediaCodecInfo.isSoftwareOnly();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isVendor() {
        return mediaCodecInfo.isVendor();
    }
    
    public boolean isEncoder() {
        return mediaCodecInfo.isEncoder();
    }
    
    public boolean isDecoder() {
        return !isEncoder();
    }
    
    public String[] getSupportedTypes() {
        return mediaCodecInfo.getSupportedTypes();
    }
}
