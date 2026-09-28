package com.programminghoch10.CodecMod

import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
        actionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            //getPreferenceManager().setSharedPreferencesName("codecs");
            val codecStore = CodecStore(requireContext())
            val decodersPreferenceCategory = findPreference<PreferenceCategory>("category_decoders")!!
            val encodersPreferenceCategory = findPreference<PreferenceCategory>("category_encoders")!!
            
            var mediaCodecs: List<MediaCodecInfoWrapper>
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val mediaCodecList = MediaCodecList(MediaCodecList.ALL_CODECS)
                mediaCodecs = mediaCodecList.codecInfos.map { mediaCodecInfo -> MediaCodecInfoWrapper(mediaCodecInfo) }
            } else {
                mediaCodecs = mutableListOf()
                for (i in 0..<MediaCodecList.getCodecCount()) mediaCodecs.add(
                    MediaCodecInfoWrapper(
                        MediaCodecList.getCodecInfoAt(
                            i
                        )
                    )
                )
            }
            for (mediaCodecInfo in mediaCodecs) {
                if (mediaCodecInfo.isAlias && !SHOW_ALIASES) continue
                val preference = MediaCodecPreference(requireContext(), codecStore, mediaCodecInfo)
                val preferenceCategory = if (mediaCodecInfo.isEncoder) encodersPreferenceCategory else decodersPreferenceCategory
                preferenceCategory.addPreference(preference)
            }
        }
        
        companion object {
            const val SHOW_ALIASES = true
        }
    }
}
