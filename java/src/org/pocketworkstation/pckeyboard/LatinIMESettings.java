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

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.speech.SpeechRecognizer;
import android.text.AutoText;
import android.util.Log;

import com.android.inputmethod.voice.SettingsUtil;
import com.android.inputmethod.voice.VoiceInputLogger;

public class LatinIMESettings extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        DialogInterface.OnDismissListener {

    private static final String QUICK_FIXES_KEY = "quick_fixes";
    private static final String PREDICTION_SETTINGS_KEY = "prediction_settings";
    private static final String VOICE_SETTINGS_KEY = "voice_mode";
    /* package */ static final String PREF_SETTINGS_KEY = "settings_key";

    private static final String TAG = "LatinIMESettings";

    // Dialog ids
    private static final int VOICE_INPUT_CONFIRM_DIALOG = 0;

    private CheckBoxPreference mQuickFixes;
    private ListPreference mVoicePreference;
    private ListPreference mSettingsKeyPreference;
    private ListPreference mHeightInPortraitPreference;
    private ListPreference mHeightInLandscapePreference;
    private ListPreference mHintModePreference;
    private ListPreference mVibrateDurationPreference;
    private CheckBoxPreference mFullInPortraitPreference;
    private CheckBoxPreference mSuggestionsInLandscapePreference;
    private CheckBoxPreference mStandardViewInLandscapePreference;
    private CheckBoxPreference mConnectbotHackPreference;
    private boolean mVoiceOn;

    private VoiceInputLogger mLogger;

    private boolean mOkClicked = false;
    private String mVoiceModeOff;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs);
        mQuickFixes = (CheckBoxPreference) findPreference(QUICK_FIXES_KEY);
        mVoicePreference = (ListPreference) findPreference(VOICE_SETTINGS_KEY);
        mSettingsKeyPreference = (ListPreference) findPreference(PREF_SETTINGS_KEY);
        mHeightInPortraitPreference = (ListPreference) findPreference(LatinIME.PREF_HEIGHT_PORTRAIT);
        mHeightInLandscapePreference = (ListPreference) findPreference(LatinIME.PREF_HEIGHT_LANDSCAPE);
        mHintModePreference = (ListPreference) findPreference(LatinIME.PREF_HINT_MODE);
        mVibrateDurationPreference = (ListPreference) findPreference(LatinIME.PREF_VIBRATE_LEN);
        mFullInPortraitPreference = (CheckBoxPreference) findPreference(LatinIME.PREF_FULL_KEYBOARD_IN_PORTRAIT);
        mSuggestionsInLandscapePreference = (CheckBoxPreference) findPreference(LatinIME.PREF_SUGGESTIONS_IN_LANDSCAPE);
        mStandardViewInLandscapePreference = (CheckBoxPreference) findPreference(LatinIME.PREF_FULLSCREEN_OVERRIDE);
        mConnectbotHackPreference = (CheckBoxPreference) findPreference(LatinIME.PREF_CONNECTBOT_TAB_HACK);
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);

        mVoiceModeOff = getString(R.string.voice_mode_off);
        mVoiceOn = !(prefs.getString(VOICE_SETTINGS_KEY, mVoiceModeOff).equals(mVoiceModeOff));
        mLogger = VoiceInputLogger.getLogger(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int autoTextSize = AutoText.getSize(getListView());
        if (autoTextSize < 1) {
            ((PreferenceGroup) findPreference(PREDICTION_SETTINGS_KEY))
                    .removePreference(mQuickFixes);
        }
        if (!LatinIME.VOICE_INSTALLED
                || !SpeechRecognizer.isRecognitionAvailable(this)) {
            getPreferenceScreen().removePreference(mVoicePreference);
        } else {
            updateVoiceModeSummary();
        }
        updateSummaries();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        (new BackupManager(this)).dataChanged();
        // If turning on voice input, show dialog
        if (key.equals(VOICE_SETTINGS_KEY) && !mVoiceOn) {
            if (!prefs.getString(VOICE_SETTINGS_KEY, mVoiceModeOff)
                    .equals(mVoiceModeOff)) {
                showVoiceConfirmation();
            }
        }
        mVoiceOn = !(prefs.getString(VOICE_SETTINGS_KEY, mVoiceModeOff).equals(mVoiceModeOff));
        updateVoiceModeSummary();
        updateSummaries();
    }

    static private void setSummaryToEntry(ListPreference pref, CharSequence defVal) {
        CharSequence val = pref.getEntry();
        //Log.i("PCKeyboard", "setSummaryToEntry " + pref.getKey() + " val=" + val + " defVal=" + defVal);
        if (val == null) {
            pref.setSummary(defVal);
        } else {
            pref.setSummary(pref.getEntry().toString().replace("%", "%%"));
        }
    }

    static private void setSummaryForBoolean(CheckBoxPreference pref, String trueDesc, String falseDesc) {
        Boolean val = pref.isChecked();
        String desc = val ? trueDesc : falseDesc;
        pref.setSummary(desc.replace("%", "%%"));
    }

    private void updateSummaries() {
        Resources res = getResources();
        mSettingsKeyPreference.setSummary(
                res.getStringArray(R.array.settings_key_modes)
                [mSettingsKeyPreference.findIndexOfValue(mSettingsKeyPreference.getValue())]);

        setSummaryToEntry(mHeightInPortraitPreference, res.getString(R.string.default_height_portrait));
        setSummaryToEntry(mHeightInLandscapePreference, res.getString(R.string.default_height_landscape));
        setSummaryToEntry(mHintModePreference, res.getString(R.string.default_hint_mode));
        setSummaryToEntry(mVibrateDurationPreference, res.getString(R.string.vibrate_duration_ms));
        setSummaryForBoolean(mFullInPortraitPreference,
                res.getString(R.string.summary_full_in_portrait_true),
                res.getString(R.string.summary_full_in_portrait_false));
        setSummaryForBoolean(mSuggestionsInLandscapePreference,
                res.getString(R.string.summary_suggestions_in_landscape_true),
                res.getString(R.string.summary_suggestions_in_landscape_false));
        setSummaryForBoolean(mStandardViewInLandscapePreference,
                res.getString(R.string.summary_fullscreen_override_true),
                res.getString(R.string.summary_fullscreen_override_false));
        setSummaryForBoolean(mConnectbotHackPreference,
                res.getString(R.string.summary_connectbot_tab_hack_true),
                res.getString(R.string.summary_connectbot_tab_hack_false));
    }

    private void showVoiceConfirmation() {
        mOkClicked = false;
        showDialog(VOICE_INPUT_CONFIRM_DIALOG);
    }

    private void updateVoiceModeSummary() {
        mVoicePreference.setSummary(
                getResources().getStringArray(R.array.voice_input_modes_summary)
                [mVoicePreference.findIndexOfValue(mVoicePreference.getValue())]);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case VOICE_INPUT_CONFIRM_DIALOG:
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                            mVoicePreference.setValue(mVoiceModeOff);
                            mLogger.settingsWarningDialogCancel();
                        } else if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                            mOkClicked = true;
                            mLogger.settingsWarningDialogOk();
                        }
                        updateVoicePreference();
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.voice_warning_title)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener);

                // Get the current list of supported locales and check the current locale against
                // that list, to decide whether to put a warning that voice input will not work in
                // the current language as part of the pop-up confirmation dialog.
                String supportedLocalesString = SettingsUtil.getSettingsString(
                        getContentResolver(),
                        SettingsUtil.LATIN_IME_VOICE_INPUT_SUPPORTED_LOCALES,
                        LatinIME.DEFAULT_VOICE_INPUT_SUPPORTED_LOCALES);
                ArrayList<String> voiceInputSupportedLocales =
                        LatinIME.newArrayList(supportedLocalesString.split("\\s+"));
                boolean localeSupported = voiceInputSupportedLocales.contains(
                        Locale.getDefault().toString());

                if (localeSupported) {
                    String message = getString(R.string.voice_warning_may_not_understand) + "\n\n" +
                            getString(R.string.voice_hint_dialog_message);
                    builder.setMessage(message);
                } else {
                    String message = getString(R.string.voice_warning_locale_not_supported) +
                            "\n\n" + getString(R.string.voice_warning_may_not_understand) + "\n\n" +
                            getString(R.string.voice_hint_dialog_message);
                    builder.setMessage(message);
                }

                AlertDialog dialog = builder.create();
                dialog.setOnDismissListener(this);
                mLogger.settingsWarningDialogShown();
                return dialog;
            default:
                Log.e(TAG, "unknown dialog " + id);
                return null;
        }
    }

    public void onDismiss(DialogInterface dialog) {
        mLogger.settingsWarningDialogDismissed();
        if (!mOkClicked) {
            // This assumes that onPreferenceClick gets called first, and this if the user
            // agreed after the warning, we set the mOkClicked value to true.
            mVoicePreference.setValue(mVoiceModeOff);
        }
    }

    private void updateVoicePreference() {
        boolean isChecked = !mVoicePreference.getValue().equals(mVoiceModeOff);
        if (isChecked) {
            mLogger.voiceInputSettingEnabled();
        } else {
            mLogger.voiceInputSettingDisabled();
        }
    }
}
