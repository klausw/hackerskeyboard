package org.pocketworkstation.pckeyboard;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import java.util.ArrayList;

/**
 * Starts voice input. Prefers any enabled IME that offers a "voice" subtype
 * (e.g. Google voice typing, FUTO Voice Input), otherwise falls back to the
 * RECOGNIZE_SPEECH intent and commits the result.
 */
public class VoiceTrigger {
    private static final String TAG = "PCKeyboardVoice";
    private static final String VOICE_MODE = "voice";

    private static String sPendingResult;

    private final InputMethodService mService;
    private InputMethodInfo mVoiceIme;
    private InputMethodSubtype mVoiceSubtype;
    private boolean mIntentAvailable;

    public VoiceTrigger(InputMethodService service) {
        mService = service;
        onStartInputView();
    }

    public boolean isInstalled() {
        return mVoiceIme != null || mIntentAvailable;
    }

    public void startVoiceRecognition() {
        if (mVoiceIme != null) {
            IBinder token = mService.getWindow().getWindow().getAttributes().token;
            getImm().setInputMethodAndSubtype(token, mVoiceIme.getId(), mVoiceSubtype);
        } else if (mIntentAvailable) {
            Intent intent = new Intent(mService, RecognitionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            mService.startActivity(intent);
        }
    }

    public void onStartInputView() {
        findVoiceIme();
        mIntentAvailable = !mService.getPackageManager().queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).isEmpty();
        if (sPendingResult != null) {
            InputConnection ic = mService.getCurrentInputConnection();
            if (ic != null) {
                ic.commitText(sPendingResult, 1);
                sPendingResult = null;
            }
        }
    }

    private InputMethodManager getImm() {
        return (InputMethodManager) mService.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void findVoiceIme() {
        mVoiceIme = null;
        mVoiceSubtype = null;
        String ownPackage = mService.getPackageName();
        InputMethodManager imm = getImm();
        for (InputMethodInfo imi : imm.getEnabledInputMethodList()) {
            if (imi.getPackageName().equals(ownPackage)) continue;
            for (InputMethodSubtype subtype : imm.getEnabledInputMethodSubtypeList(imi, true)) {
                if (VOICE_MODE.equals(subtype.getMode())) {
                    mVoiceIme = imi;
                    mVoiceSubtype = subtype;
                    return;
                }
            }
        }
    }

    /** Runs the speech recognizer and hands the result back to the IME. */
    public static class RecognitionActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            try {
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, "No speech recognizer available", e);
                finish();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> results =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && !results.isEmpty()) {
                    sPendingResult = results.get(0);
                }
            }
            finish();
        }
    }
}
