package org.pocketworkstation.pckeyboard;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

/**
 * Global current settings for the keyboard.
 * 
 * <p>
 * Yes, globals are evil. But the persisted shared preferences are global data
 * by definition, and trying to hide this by propagating the current manually
 * just adds a lot of complication. This is especially annoying due to Views
 * getting constructed in a way that doesn't support adding additional
 * constructor arguments, requiring post-construction method calls, which is
 * error-prone and fragile.
 * 
 * <p>
 * The comments below indicate which class is responsible for updating the
 * value, and for recreating keyboards or views as necessary. Other classes
 * MUST treat the fields as read-only values, and MUST NOT attempt to save
 * these values or results derived from them across re-initializations.

 * 
 * @author klaus.weidner@gmail.com
 */
public final class GlobalKeyboardSettings {
    protected static final String TAG = "HK/Globals";

    /* Simple prefs updated by this class */
    //
    // Read by Keyboard
    public int popupKeyboardFlags = 0x1;
    public float topRowScale = 1.0f;
    //
    // Read by LatinKeyboardView
    public boolean showTouchPos = false;
    //
    // Read by LatinIME
    public String suggestedPunctuation = "!?,.";
    public int keyboardModePortrait = 0;
    public int keyboardModeLandscape = 2;
    public boolean compactModeEnabled = true;  // always on
    public int ctrlAOverride = 0;
    public int chordingCtrlKey = 0;
    public int chordingAltKey = 0;
    public int chordingMetaKey = 0;
    public float keyClickVolume = 0.0f;
    public int keyClickMethod = 0;
    public boolean capsLock = true;
    public boolean shiftLockModifiers = false;
    //
    // Read by LatinKeyboardBaseView
    public float labelScalePref = 1.0f;
    //
    // Read by CandidateView
    public float candidateScalePref = 1.0f;
    //
    // Read by PointerTracker
    public int sendSlideKeys = 0;
    
    /* Updated by LatinIME */
    //
    // Read by KeyboardSwitcher
    public int keyboardMode = 0;
    public boolean useExtension = false;
    //
    // Read by LatinKeyboardView and KeyboardSwitcher
    public float keyboardHeightPercent = 40.0f; // percent of screen height
    //
    // Read by LatinKeyboardBaseView
    public int hintMode = 0;
    public int renderMode = 1;
    //
    // Read by PointerTracker
    public int longpressTimeout = 400;
    //
    // Read by LatinIMESettings
    // These are cached values for informational display, don't use for other purposes
    public String editorPackageName; 
    public String editorFieldName; 
    public int editorFieldId; 
    public int editorInputType;

    /* Updated by KeyboardSwitcher */
    //
    // Used by LatinKeyboardBaseView and LatinIME

    /* Updated by LanguageSwitcher */
    //
    // Used by Keyboard and KeyboardSwitcher
    public Locale inputLocale = Locale.getDefault();

    // Auto pref implementation follows
    private Map<String, BooleanPref> mBoolPrefs = new HashMap<String, BooleanPref>();
    private Map<String, StringPref> mStringPrefs = new HashMap<String, StringPref>();
    public static final int FLAG_PREF_NONE = 0;
    public static final int FLAG_PREF_NEED_RELOAD = 0x1;
    public static final int FLAG_PREF_NEW_PUNC_LIST = 0x2;
    public static final int FLAG_PREF_RECREATE_INPUT_VIEW = 0x4;
    public static final int FLAG_PREF_RESET_KEYBOARDS = 0x8;
    public static final int FLAG_PREF_RESET_MODE_OVERRIDE = 0x10;
    private int mCurrentFlags = 0;
    
    private interface BooleanPref {
        void set(boolean val);
        boolean getDefault();
        int getFlags();
    }

    private interface StringPref {
        void set(String val);
        String getDefault();
        int getFlags();
    }

    public void initPrefs(SharedPreferences prefs, Resources resources) {
        final Resources res = resources;

        addStringPref("pref_keyboard_mode_portrait", new StringPref() {
            public void set(String val) { keyboardModePortrait = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_keyboard_mode_portrait); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS | FLAG_PREF_RESET_MODE_OVERRIDE; }
        });

        addStringPref("pref_keyboard_mode_landscape", new StringPref() {
            public void set(String val) { keyboardModeLandscape = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_keyboard_mode_landscape); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS | FLAG_PREF_RESET_MODE_OVERRIDE; }
        });

        addStringPref("pref_slide_keys_int", new StringPref() {
            public void set(String val) { sendSlideKeys = Integer.valueOf(val); }
            public String getDefault() { return "0"; }
            public int getFlags() { return FLAG_PREF_NONE; }
        });

        addBooleanPref("pref_touch_pos", new BooleanPref() {
            public void set(boolean val) { showTouchPos = val; }
            public boolean getDefault() { return false; }
            public int getFlags() { return FLAG_PREF_NONE; }
        });

        addStringPref("pref_popup_content", new StringPref() {
            public void set(String val) { popupKeyboardFlags = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_popup_content); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_suggested_punctuation", new StringPref() {
            public void set(String val) { suggestedPunctuation = val; }
            public String getDefault() { return res.getString(R.string.suggested_punctuations_default); }
            public int getFlags() { return FLAG_PREF_NEW_PUNC_LIST; }
        });

        addStringPref("pref_label_scale_v2", new StringPref() {
            public void set(String val) { labelScalePref = Float.valueOf(val); }
            public String getDefault() { return "1.0"; }
            public int getFlags() { return FLAG_PREF_RECREATE_INPUT_VIEW; }
        });

        addStringPref("pref_candidate_scale", new StringPref() {
            public void set(String val) { candidateScalePref = Float.valueOf(val); }
            public String getDefault() { return "1.0"; }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_top_row_scale", new StringPref() {
            public void set(String val) { topRowScale = Float.valueOf(val); }
            public String getDefault() { return "1.0"; }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_ctrl_a_override", new StringPref() {
            public void set(String val) { ctrlAOverride = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_ctrl_a_override); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_chording_ctrl_key", new StringPref() {
            public void set(String val) { chordingCtrlKey = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_chording_ctrl_key); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_chording_alt_key", new StringPref() {
            public void set(String val) { chordingAltKey = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_chording_alt_key); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_chording_meta_key", new StringPref() {
            public void set(String val) { chordingMetaKey = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_chording_meta_key); }
            public int getFlags() { return FLAG_PREF_RESET_KEYBOARDS; }
        });

        addStringPref("pref_click_volume", new StringPref() {
            public void set(String val) { keyClickVolume = Float.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_click_volume); }
            public int getFlags() { return FLAG_PREF_NONE; }
        });

        addStringPref("pref_click_method", new StringPref() {
            public void set(String val) { keyClickMethod = Integer.valueOf(val); }
            public String getDefault() { return res.getString(R.string.default_click_method); }
            public int getFlags() { return FLAG_PREF_NONE; }
        });

        addBooleanPref("pref_caps_lock", new BooleanPref() {
            public void set(boolean val) { capsLock = val; }
            public boolean getDefault() { return res.getBoolean(R.bool.default_caps_lock); }
            public int getFlags() { return FLAG_PREF_NONE; }
        });

        addBooleanPref("pref_shift_lock_modifiers", new BooleanPref() {
            public void set(boolean val) { shiftLockModifiers = val; }
            public boolean getDefault() { return res.getBoolean(R.bool.default_shift_lock_modifiers); }
            public int getFlags() { return FLAG_PREF_NONE; }
        });

        // Set initial values
        for (String key : mBoolPrefs.keySet()) {
            BooleanPref pref = mBoolPrefs.get(key);
            pref.set(prefs.getBoolean(key, pref.getDefault()));
        }
        for (String key : mStringPrefs.keySet()) {
            StringPref pref = mStringPrefs.get(key);
            pref.set(prefs.getString(key, pref.getDefault()));
        }
    }
    
    public void sharedPreferenceChanged(SharedPreferences prefs, String key) {
        boolean found = false;
        mCurrentFlags = FLAG_PREF_NONE;
        BooleanPref bPref = mBoolPrefs.get(key);
        if (bPref != null) {
            found = true;
            bPref.set(prefs.getBoolean(key, bPref.getDefault()));
            mCurrentFlags |= bPref.getFlags(); 
        }
        StringPref sPref = mStringPrefs.get(key);
        if (sPref != null) {
            found = true;
            sPref.set(prefs.getString(key, sPref.getDefault()));
            mCurrentFlags |= sPref.getFlags(); 
        }
        //if (!found) Log.i(TAG, "sharedPreferenceChanged: unhandled key=" + key);
    }
    
    public boolean hasFlag(int flag) {
        if ((mCurrentFlags & flag) != 0) {
            mCurrentFlags &= ~flag;
            return true;
        }
        return false;
    }
    
    public int unhandledFlags() {
        return mCurrentFlags;
    }

    private void addBooleanPref(String key, BooleanPref setter) {
        mBoolPrefs.put(key, setter);
    }

    private void addStringPref(String key, StringPref setter) {
        mStringPrefs.put(key, setter);
    }
}
