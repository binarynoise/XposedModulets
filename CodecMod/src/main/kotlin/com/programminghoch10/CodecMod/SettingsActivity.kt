package com.programminghoch10.CodecMod

import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import com.programminghoch10.CodecMod.CodecStore.Companion.DEFAULT_VALUE

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
        lateinit var menu: Menu
        
        val decodersPreferenceCategory get() = findPreference<PreferenceCategory>("category_decoders")!!
        val encodersPreferenceCategory get() = findPreference<PreferenceCategory>("category_encoders")!!
        val allHiddenByFiltersPreference get() = findPreference<Preference>("allHiddenByFilters")!!
        
        val allPreferences get() = (encodersPreferenceCategory.children + decodersPreferenceCategory.children).filterIsInstance(MediaCodecPreference::class.java)
        
        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.settings_options, menu)
            this.menu = menu
            reevaluateFilters()
            super.onCreateOptionsMenu(menu, inflater)
        }
        
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId in listOf(
                    R.id.show_aliases,
                    R.id.show_video_codecs,
                    R.id.show_audio_codecs,
                    R.id.show_hardware_codecs,
                    R.id.show_software_codecs,
                )
            ) {
                item.isChecked = !item.isChecked
                reevaluateFilters()
                return true
            }
            if (item.itemId in listOf(R.id.enable_all_visible, R.id.disable_all_visible)) {
                val checked = item.itemId == R.id.enable_all_visible
                allPreferences.forEach { it.isChecked = checked }
                return true
            }
            if (item.itemId == R.id.reset_configuration) {
                allPreferences.forEach { it.isChecked = DEFAULT_VALUE }
                return true
            }
            return super.onOptionsItemSelected(item)
        }
        
        fun reevaluateFilters() {
            val filterSpec = MediaCodecPreference.FilterSpec(
                menu.findItem(R.id.show_aliases).isChecked,
                menu.findItem(R.id.show_video_codecs).isChecked,
                menu.findItem(R.id.show_audio_codecs).isChecked,
                menu.findItem(R.id.show_hardware_codecs).isChecked,
                menu.findItem(R.id.show_software_codecs).isChecked,
            )
            Log.d(this::class.java.simpleName, "reevaluateFilters: $filterSpec")
            allPreferences.forEach { it.setFilterSpec(filterSpec) }
            allHiddenByFiltersPreference.isVisible = allPreferences.all { !it.isVisible }
        }
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            setHasOptionsMenu(true)
            //getPreferenceManager().setSharedPreferencesName("codecs");
            val codecStore = CodecStore(requireContext())
            
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
                val preference = MediaCodecPreference(requireContext(), codecStore, mediaCodecInfo)
                preference.isIconSpaceReserved = false
                val preferenceCategory = if (mediaCodecInfo.isEncoder) encodersPreferenceCategory else decodersPreferenceCategory
                preferenceCategory.addPreference(preference)
            }
        }
    }
}
