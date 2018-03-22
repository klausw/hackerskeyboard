package org.pocketworkstation.pckeyboard;

import android.content.Context;
import android.util.AttributeSet;

public class VibratePreference extends SeekBarPreferenceString {
    public VibratePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void onChange(float val) {
        LatinIME ime = LatinIME.sInstance;
        if (ime != null) ime.vibrate((int) val);
    }
}