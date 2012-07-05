package org.pocketworkstation.pckeyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Variant of SeekBarPreference that stores values as string preferences.
 * 
 * This is for compatibility with existing preferences, switching types
 * leads to runtime errors when upgrading or downgrading.
 */
public class SeekBarPreferenceString extends SeekBarPreference {

    public SeekBarPreferenceString(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected Float onGetDefaultValue(TypedArray a, int index) {
        return Float.valueOf(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            setVal(Float.valueOf(getPersistedString("0.0")));
        } else {
            setVal(Float.valueOf((Float) defaultValue));
        }
        savePrevVal();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (!positiveResult) {
            restoreVal();
            return;
        }
        if (shouldPersist()) {
            savePrevVal();
            persistString(getValString());
        }
        notifyChanged();
    }
}