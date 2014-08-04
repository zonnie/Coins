package com.moneyifyapp.activities.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PrefActivity extends PreferenceActivity
{
    public static final String PREF_LIST_NAME = "pref_currency_list";
    public static final String KEY_CURRENCY_CHANGE = "currency";
    public static Map<String, Boolean> sPrefChanges;
    public static boolean sPrefChanged = false;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sPrefChanges = new HashMap<String, Boolean>();

        Utils.initializeActionBar(this);
        Utils.setLogo(this, R.drawable.contorls);
        Utils.setupBackButton(this);
        Utils.animateForward(this);

        PreferenceManager.setDefaultValues(this, R.xml.activity_pref_layout, false);
    }

    /**
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     */
    private void setupSimplePreferencesScreen()
    {
        if (!isSimplePreferences(this))
        {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.activity_pref_layout);

        // Add 'notifications' preferences, and a corresponding header.
        //PreferenceCategory fakeHeader = new PreferenceCategory(this);
        //fakeHeader.setTitle(R.string.pref_header_notifications);
        //getPreferenceScreen().addPreference(fakeHeader);
        //addPreferencesFromResource(R.xml.pref_notification);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        //bindPreferenceSummaryToValue(findPreference("example_text"));
        bindPreferenceSummaryToValue(findPreference(PREF_LIST_NAME));
        //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane()
    {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via ALWAYS_SIMPLE_PREFS, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context)
    {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target)
    {
        if (!isSimplePreferences(this))
            loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        /**
         *
         * @param preference
         * @param value
         * @return
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();

            if (preference instanceof ListPreference)
            {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary
                        (
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null
                );


                sPrefChanged = true;
                // Update that settings changed
                if(preference.getKey().equals(PREF_LIST_NAME))
                    sPrefChanges.put(PREF_LIST_NAME, true);
            }
            else
            {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "")
        );
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_pref_layout);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("pref_currency_list"));
        }
    }

    /**
     */
    @Override
    public void finish()
    {
        updateActivityResults();
        super.finish();
    }

    /**
     */
    private void updateActivityResults()
    {
        if(sPrefChanged == true)
        {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean(PREF_LIST_NAME, sPrefChanges.get(PREF_LIST_NAME));
            intent.putExtras(bundle);
            setResult(ExpensesActivity.RESP_CHANGED, intent);
            sPrefChanged = false;
        }
    }

    /**
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
            {
                NavUtils.navigateUpFromSameTask(this);
                Utils.animateBack(this);
                updateActivityResults();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Utils.animateBack(this);
    }
}
