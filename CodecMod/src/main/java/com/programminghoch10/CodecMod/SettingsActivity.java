package com.programminghoch10.CodecMod;

import android.app.ActionBar;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SettingsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(
                    getSupportFragmentManager().getBackStackEntryCount() > 0
            );
        }
    }
    
    public static class SettingsFragment extends PreferenceFragmentCompat {
        private static final boolean SHOW_ALIASES = true;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //getPreferenceManager().setSharedPreferencesName("codecs");
            CodecStore codecStore = new CodecStore(requireContext());
            PreferenceCategory decodersPreferenceCategory = findPreference("category_decoders");
            PreferenceCategory encodersPreferenceCategory = findPreference("category_encoders");
            
            List<MediaCodecInfoWrapper> mediaCodecs;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
                mediaCodecs = Arrays.stream(mediaCodecList.getCodecInfos())
                        .map(MediaCodecInfoWrapper::new)
                        .toList();
            } else {
                mediaCodecs = new LinkedList<>();
                for (int i = 0; i < MediaCodecList.getCodecCount(); i++)
                    mediaCodecs.add(new MediaCodecInfoWrapper(MediaCodecList.getCodecInfoAt(i)));
            }
            for (MediaCodecInfoWrapper mediaCodecInfo : mediaCodecs) {
                if (mediaCodecInfo.isAlias() && !SHOW_ALIASES) continue;
                SwitchPreference preference = new SwitchPreference(requireContext());
                preference.setPersistent(false);
                preference.setDefaultValue(CodecStore.DEFAULT_VALUE);
                preference.setKey(CodecStore.getKey(mediaCodecInfo));
                preference.setOnPreferenceChangeListener((p, n) -> codecStore.setCodecPreference(mediaCodecInfo, (Boolean) n));
                codecStore.registerOnCodecPreferenceChangedListener(mediaCodecInfo, value -> {
                    if (preference.isChecked() != value) preference.setChecked(value);
                });
                preference.setTitle(mediaCodecInfo.getName()
                        + (mediaCodecInfo.getName().equals(mediaCodecInfo.getCanonicalName()) ? "" : " (" + mediaCodecInfo.getCanonicalName() + ")"));
                StringBuilder summaryBuilder = new StringBuilder();
                summaryBuilder.append(String.format(getString(R.string.supported_types), Arrays.toString(mediaCodecInfo.getSupportedTypes())));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    summaryBuilder.append("\n");
                    summaryBuilder.append(String.format(getString(R.string.hardware_accelerated), mediaCodecInfo.isHardwareAccelerated()));
                    summaryBuilder.append("\n");
                    summaryBuilder.append(String.format(getString(R.string.software_only), mediaCodecInfo.isSoftwareOnly()));
                    if (SHOW_ALIASES) {
                        summaryBuilder.append("\n");
                        summaryBuilder.append(String.format(getString(R.string.alias), mediaCodecInfo.isAlias()));
                    }
                    summaryBuilder.append("\n");
                    summaryBuilder.append(String.format(getString(R.string.vendor), mediaCodecInfo.isVendor()));
                }
                preference.setSummary(summaryBuilder);
                PreferenceCategory preferenceCategory = mediaCodecInfo.isEncoder() ? encodersPreferenceCategory : decodersPreferenceCategory;
                preferenceCategory.addPreference(preference);
                preference.setChecked(codecStore.getCodecPreference(mediaCodecInfo));
            }
        }
    }
}
