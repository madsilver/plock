package br.com.silver.plock;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class ConfigFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    EditTextPreference mUrl;
    EditTextPreference mUrlParam;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mUrl = (EditTextPreference) findPreference(getString(R.string.pref_url));
        mUrlParam = (EditTextPreference) findPreference(getString(R.string.pref_url_param));

        fillSummary(mUrl);
        fillSummary(mUrlParam);
    }

    private void fillSummary(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Object value = pref.getString(preference.getKey(), "");
        onPreferenceChange(preference, value);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
