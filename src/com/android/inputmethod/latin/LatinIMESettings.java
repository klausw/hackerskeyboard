/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.inputmethod.latin;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

public class LatinIMESettings extends PreferenceActivity 
        implements OnSharedPreferenceChangeListener{
    
    private static final String CORRECTION_MODE_KEY = "prediction_mode";
    private static final String PREDICTION_SETTINGS_KEY = "prediction_settings";
    private static final String PREDICTION_LANDSCAPE_KEY = "prediction_landscape";
    
    private ListPreference mCorrectionMode;
    private PreferenceGroup mPredictionSettings;
    private Preference mPredictionLandscape;
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs);
        mCorrectionMode = (ListPreference) findPreference(CORRECTION_MODE_KEY);
        mPredictionSettings = (PreferenceGroup) findPreference(PREDICTION_SETTINGS_KEY);
        mPredictionLandscape = findPreference(PREDICTION_LANDSCAPE_KEY);
        updatePredictionSettings();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onDestroy() {
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
    
    private void updatePredictionSettings() {
        if (mCorrectionMode != null && mPredictionSettings != null) {
            String correctionMode = mCorrectionMode.getValue();
            if (correctionMode.equals(getResources().getString(R.string.prediction_none))) {
                mPredictionSettings.setEnabled(false);
            } else {
                mPredictionSettings.setEnabled(true);
                boolean suggestionsInLandscape = 
                    !correctionMode.equals(getResources().getString(R.string.prediction_full));
                mPredictionLandscape.setEnabled(suggestionsInLandscape);
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(CORRECTION_MODE_KEY)) {
            updatePredictionSettings();
        }
    }
}
