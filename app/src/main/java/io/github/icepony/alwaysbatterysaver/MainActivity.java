package io.github.icepony.alwaysbatterysaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

public class MainActivity extends Activity {
    public static void showActivationWarning(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.module_not_loaded)
                .setMessage(R.string.will_not_save)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showActivationWarning(this);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

}