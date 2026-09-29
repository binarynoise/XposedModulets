package com.programminghoch10.CodecMod

import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import com.programminghoch10.CodecMod.CodecStore.Companion.DEFAULT_VALUE
import com.programminghoch10.CodecMod.databinding.SettingsActivityBinding

class SettingsActivity : FragmentActivity() {
    private lateinit var binding: SettingsActivityBinding
    val allHiddenByFiltersView get() = binding.allHiddenByFilters
    val buttonCategoryDecoders get() = binding.buttonCategoryDecoders
    val buttonCategoryEncoders get() = binding.buttonCategoryEncoders
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
        actionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
        buttonCategoryDecoders.isActivated = true
        buttonCategoryEncoders.isActivated = false
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        lateinit var menu: Menu
        val activity get() = requireActivity() as SettingsActivity
        
        val allMediaCodecPreferences get() = preferenceScreen.children.filterIsInstance(MediaCodecPreference::class.java)
        
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
                allMediaCodecPreferences.filter { it.isVisible }.forEach { it.isChecked = checked }
                return true
            }
            if (item.itemId == R.id.reset_configuration) {
                allMediaCodecPreferences.forEach { it.isChecked = DEFAULT_VALUE }
                return true
            }
            return super.onOptionsItemSelected(item)
        }
        
        fun reevaluateFilters() {
            val filterSpec = MediaCodecPreference.FilterSpec(
                activity.buttonCategoryEncoders.isActivated,
                activity.buttonCategoryDecoders.isActivated,
                menu.findItem(R.id.show_aliases).isChecked,
                menu.findItem(R.id.show_video_codecs).isChecked,
                menu.findItem(R.id.show_audio_codecs).isChecked,
                menu.findItem(R.id.show_hardware_codecs).isChecked,
                menu.findItem(R.id.show_software_codecs).isChecked,
            )
            Log.d(this::class.java.simpleName, "reevaluateFilters: $filterSpec")
            allMediaCodecPreferences.forEach { it.setFilterSpec(filterSpec) }
            activity.allHiddenByFiltersView.isVisible = allMediaCodecPreferences.all { !it.isVisible }
        }
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceScreen = preferenceManager.createPreferenceScreen(requireContext())
            setHasOptionsMenu(true)
            val codecStore = CodecStore(requireContext())
            
            val onCategoryChangeListener = View.OnClickListener {
                activity.buttonCategoryDecoders.isActivated = false
                activity.buttonCategoryEncoders.isActivated = false
                it.isActivated = true
                reevaluateFilters()
            }
            activity.buttonCategoryDecoders.setOnClickListener(onCategoryChangeListener)
            activity.buttonCategoryEncoders.setOnClickListener(onCategoryChangeListener)
            
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
                preferenceScreen.addPreference(preference)
            }
        }
    }
}
