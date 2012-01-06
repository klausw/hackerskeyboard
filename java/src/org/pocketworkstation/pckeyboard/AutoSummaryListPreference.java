/**
 *
 */
package org.pocketworkstation.pckeyboard;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class AutoSummaryListPreference extends ListPreference {
    private static final String TAG = "HK/AutoSummaryListPreference";

    public AutoSummaryListPreference(Context context) {
        super(context);
    }

    public AutoSummaryListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void trySetSummary() {
        CharSequence entry = null;
        try {
            entry = getEntry();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(TAG, "Malfunctioning ListPreference, can't get entry");
        }
        if (entry != null) {
            //String percent = getResources().getString(R.string.percent);
            String percent = "percent";
            setSummary(entry.toString().replace("%", " " + percent));
        }
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        trySetSummary();
    }

    @Override
    public void setEntryValues(CharSequence[] entryValues) {
        super.setEntryValues(entryValues);
        trySetSummary();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        trySetSummary();
    }
}
