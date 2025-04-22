package io.github.icepony.alwaysbatterysaver;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference mEnableModuleSwitch;
    private PreferenceCategory mSettingsPreferenceCategory;
    private SwitchPreference mLockAnySwitch;
    private PreferenceCategory mExperimentalCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getPreferenceManager().setSharedPreferencesName(BuildConfig.APPLICATION_ID + "_preferences");
        addPreferencesFromResource(R.xml.prefs);

        mEnableModuleSwitch = (SwitchPreference) findPreference("enable_module");
        mSettingsPreferenceCategory = (PreferenceCategory) findPreference("settings");
        mExperimentalCategory = (PreferenceCategory) findPreference("experimental");
        mLockAnySwitch = (SwitchPreference) findPreference("lock_any");

        updatePreferenceEnabledStates(mEnableModuleSwitch.isChecked());

        mEnableModuleSwitch.setOnPreferenceChangeListener(this);
        mLockAnySwitch.setOnPreferenceChangeListener(this);
    }

    private void updatePreferenceEnabledStates(boolean moduleEnabled) {
        if (!BuildConfig.DEBUG) {
            if (mSettingsPreferenceCategory != null) {
                mSettingsPreferenceCategory.setEnabled(moduleEnabled);
            }
            if (mExperimentalCategory != null) {
                mExperimentalCategory.setEnabled(moduleEnabled);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!(newValue instanceof Boolean)) {
            return false;
        }
        boolean isEnabled = (Boolean) newValue;
        String key = preference.getKey();

        switch (key) {
            case "enable_module":
                updatePreferenceEnabledStates(isEnabled);
                return true;

            case "lock_any":
                if (isEnabled) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.lock_any_warning_title)
                            .setMessage(R.string.lock_any_warning_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Adjust the top distance to avoid conflict with the ActionBar
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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
        if (mEnableModuleSwitch != null) {
            updatePreferenceEnabledStates(mEnableModuleSwitch.isChecked());
        }
    }
}