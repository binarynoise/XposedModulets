package com.programminghoch10.CodecMod

import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

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
                val preference = SwitchPreference(requireContext())
                preference.isPersistent = false
                preference.setDefaultValue(CodecStore.DEFAULT_VALUE)
                preference.setKey(CodecStore.getKey(mediaCodecInfo))
                preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                    codecStore.setCodecPreference(mediaCodecInfo, (newValue as Boolean))
                }
                codecStore.registerOnCodecPreferenceChangedListener(mediaCodecInfo) { value: Boolean ->
                    if (preference.isChecked != value) preference.setChecked(value)
                }
                preference.title =
                    mediaCodecInfo.name + (if (mediaCodecInfo.name == mediaCodecInfo.canonicalName) "" else " (" + mediaCodecInfo.canonicalName + ")")
                val summaryBuilder = StringBuilder()
                summaryBuilder.append(
                    String.format(
                        getString(R.string.supported_types), mediaCodecInfo.supportedTypes.contentToString()
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    summaryBuilder.append("\n")
                    summaryBuilder.append(
                        String.format(
                            getString(R.string.hardware_accelerated), mediaCodecInfo.isHardwareAccelerated
                        )
                    )
                    summaryBuilder.append("\n")
                    summaryBuilder.append(
                        String.format(
                            getString(R.string.software_only), mediaCodecInfo.isSoftwareOnly
                        )
                    )
                    if (SHOW_ALIASES) {
                        summaryBuilder.append("\n")
                        summaryBuilder.append(String.format(getString(R.string.alias), mediaCodecInfo.isAlias))
                    }
                    summaryBuilder.append("\n")
                    summaryBuilder.append(String.format(getString(R.string.vendor), mediaCodecInfo.isVendor))
                }
                preference.setSummary(summaryBuilder)
                val preferenceCategory = if (mediaCodecInfo.isEncoder) encodersPreferenceCategory else decodersPreferenceCategory
                preferenceCategory.addPreference(preference)
                preference.setChecked(codecStore.getCodecPreference(mediaCodecInfo))
            }
        }
        
        companion object {
            private const val SHOW_ALIASES = true
        }
    }
}
