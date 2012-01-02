/*
 * Copyright (C) 2011 Darren Salt
 *
 * Licensed under the Apache License, Version 2.0 (the "Licence"); you may
 * not use this file except in compliance with the Licence. You may obtain
 * a copy of the Licence at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

package org.pocketworkstation.pckeyboard;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

interface ComposeSequencing {
    public void onText(CharSequence text);
    public void updateShiftKeyState(EditorInfo attr);
    public EditorInfo getCurrentInputEditorInfo();
}

public abstract class ComposeBase {
    private static final String TAG = "HK/ComposeBase";
    
    protected static final Map<String, String> mMap =
    	new HashMap<String, String>();

    protected static final Set<String> mPrefixes =
    	new HashSet<String>();

    protected static String get(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        //Log.i(TAG, "ComposeBase get, key=" + showString(key) + " result=" + mMap.get(key));
        return mMap.get(key);
    }

    private static String showString(String in) {
        // TODO Auto-generated method stub
        StringBuilder out = new StringBuilder(in);
        out.append("{");
        for (int i = 0; i < in.length(); ++i) {
            if (i > 0) out.append(",");
            out.append((int) in.charAt(i));
        }
        out.append("}");
        return out.toString();
    }

    private static boolean isValid(String partialKey) {
        if (partialKey == null || partialKey.length() == 0) {
            return false;
        }
        return mPrefixes.contains(partialKey);
    }

    protected static void put(String key, String value) {
    	mMap.put(key, value);
    	for (int i = 1; i < key.length(); ++i) {
    		mPrefixes.add(key.substring(0, i));
    	}
    }

    protected StringBuilder composeBuffer = new StringBuilder(10);
    protected ComposeSequencing composeUser;

    protected void init(ComposeSequencing user) {
        clear();
        composeUser = user;
    }

    public void clear() {
        composeBuffer.setLength(0);
    }

    public void bufferKey(char code) {
    	composeBuffer.append(code);
    	//Log.i(TAG, "bufferKey code=" + (int) code + " => " + showString(composeBuffer.toString()));
    }

    // returns true if the compose sequence is valid but incomplete
    public String executeToString(int code) {
        KeyboardSwitcher ks = KeyboardSwitcher.getInstance();
        if (ks.getInputView().isShiftCaps()
                && ks.isAlphabetMode()
                && Character.isLowerCase(code)) {
            code = Character.toUpperCase(code);
        }
        bufferKey((char) code);
        composeUser.updateShiftKeyState(composeUser.getCurrentInputEditorInfo());

        String composed = get(composeBuffer.toString());
        if (composed != null) {
            // If we get here, we have a complete compose sequence
            return composed;
        } else if (!isValid(composeBuffer.toString())) {
            // If we get here, then the sequence typed isn't recognised
            return "";
        }
        return null;
    }

    public boolean execute(int code) {
        String composed = executeToString(code);
        if (composed != null) {
            clear();
            composeUser.onText(composed);
            return false;
        }
        return true;
    }

    public boolean execute(CharSequence sequence) {
        int i, len = sequence.length();
        boolean result = true;
        for (i = 0; i < len; ++i) {
            result = execute(sequence.charAt(i));
        }
        return result; // only last one matters
    }
}
