/*
 * Copyright (C) 2008 The Android Open Source Project
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

package org.pocketworkstation.pckeyboard;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PrefScreenView extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mRenderModePreference;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_view);
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
        mRenderModePreference = (ListPreference) findPreference(LatinIME.PREF_RENDER_MODE);
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        (new BackupManager(this)).dataChanged();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (LatinKeyboardBaseView.sSetRenderMode == null) {
            mRenderModePreference.setEnabled(false);
            mRenderModePreference.setSummary(R.string.render_mode_unavailable);
        }

    }
}
