package fr.noopy.landru.quizz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;


public class PrefActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new PrefsFragment())
                    .commit();
        }
    }


    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.general_pref);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            //IT NEVER GETS IN HERE!
            if (key.equals("level"))
            {
                Log.i("preference", key);
                changeIcon();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            changeIcon();
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        private void changeIcon() {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            Preference customPref = (Preference) findPreference("level");
            switch (settings.getString("level", "0")) {
                case "0":
                    customPref.setIcon(R.drawable.baby);
                    break;
                case "10":
                    customPref.setIcon(R.drawable.expert);
                    break;
                default:
            }
        }

    }

}
