package io.github.icepony.alwaysbatterysaver;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String KEY_ENABLE_MODULE = "enable_module";
    private static final String KEY_EXPERIMENTAL_CATEGORY = "experimental";
    private static final String KEY_SETTINGS_CATEGORY = "settings";

    private SwitchPreference mEnableModuleSwitch;
    private PreferenceCategory mExperimentalCategory, mSettingsPreferenceCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(BuildConfig.APPLICATION_ID + "_preferences");
        addPreferencesFromResource(R.xml.prefs);

        mEnableModuleSwitch = (SwitchPreference) findPreference(KEY_ENABLE_MODULE);
        mExperimentalCategory = (PreferenceCategory) findPreference(KEY_EXPERIMENTAL_CATEGORY);
        mSettingsPreferenceCategory = (PreferenceCategory) findPreference(KEY_SETTINGS_CATEGORY);

        boolean isModuleEnabled = mEnableModuleSwitch.isChecked();
        mExperimentalCategory.setEnabled(isModuleEnabled);
        mSettingsPreferenceCategory.setEnabled(isModuleEnabled);

        mEnableModuleSwitch.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals(KEY_ENABLE_MODULE)) {
            if (newValue instanceof Boolean) {
                boolean isEnabled = (Boolean) newValue;
                mExperimentalCategory.setEnabled(isEnabled);
                mSettingsPreferenceCategory.setEnabled(isEnabled);
                return true;
            }
            return false; // Should not happen
        }
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        view.setOnApplyWindowInsetsListener((v, insets) -> {
            view.setPadding(insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getStableInsetBottom());

            return insets.consumeSystemWindowInsets();
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mEnableModuleSwitch != null && mExperimentalCategory != null) {
            mExperimentalCategory.setEnabled(mEnableModuleSwitch.isChecked());
            mSettingsPreferenceCategory.setEnabled(mEnableModuleSwitch.isChecked());
        }
    }
}