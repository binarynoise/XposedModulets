package com.programminghoch10.CodecMod;

import android.app.ActionBar;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.util.Arrays;

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
        CodecStore codecStore = null;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //getPreferenceManager().setSharedPreferencesName("codecs");
            CodecStore codecStore = new CodecStore(getContext());
            PreferenceCategory decodersPreferenceCategory = findPreference("category_decoders");
            PreferenceCategory encodersPreferenceCategory = findPreference("category_encoders");
            MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
            for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
                if (mediaCodecInfo.isAlias() && !SHOW_ALIASES) continue;
                SwitchPreference preference = new SwitchPreference(getContext());
                preference.setPersistent(false);
                preference.setDefaultValue(CodecStore.DEFAULT_VALUE);
                preference.setKey(CodecStore.getKey(mediaCodecInfo));
                preference.setOnPreferenceChangeListener((p, n) -> codecStore.setCodecPreference(mediaCodecInfo, (Boolean) n));
                codecStore.registerOnCodecPreferenceChangedListener(mediaCodecInfo, value -> {
                    if (preference.isChecked() != value) preference.setChecked(value);
                });
                preference.setTitle(mediaCodecInfo.getName()
                        + (mediaCodecInfo.getName().equals(mediaCodecInfo.getCanonicalName()) ? "" : " (" + mediaCodecInfo.getCanonicalName() + ")"));
                preference.setSummary(
                        String.format(getString(R.string.hardware_accelerated), mediaCodecInfo.isHardwareAccelerated())
                                + "\n" +
                                String.format(getString(R.string.software_only), mediaCodecInfo.isSoftwareOnly())
                                + "\n" +
                                String.format(getString(R.string.supported_types), Arrays.toString(mediaCodecInfo.getSupportedTypes())) +
                                (SHOW_ALIASES ? "\n" +
                                        String.format(getString(R.string.alias), mediaCodecInfo.isAlias()) : "")
                                + "\n" +
                                String.format(getString(R.string.vendor), mediaCodecInfo.isVendor())
                );
                PreferenceCategory preferenceCategory = mediaCodecInfo.isEncoder() ? encodersPreferenceCategory : decodersPreferenceCategory;
                preferenceCategory.addPreference(preference);
                preference.setChecked(codecStore.getCodecPreference(mediaCodecInfo));
            }
        }
    }
}
