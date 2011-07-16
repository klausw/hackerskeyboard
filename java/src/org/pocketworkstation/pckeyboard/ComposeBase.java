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
import android.view.inputmethod.EditorInfo;

import java.util.HashMap;

interface ComposeSequencing {
    public void onText(CharSequence text);
    public void updateShiftKeyState(EditorInfo attr);
    public EditorInfo getCurrentInputEditorInfo();
}

public abstract class ComposeBase {
    private HashMap<Character, HashMap<String, String>> map =
            new HashMap<Character, HashMap<String, String>>();

    private String get(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        Character first = key.charAt(0);
        HashMap<String, String> submap = map.get(first);
        return submap == null ? null : submap.get(key);
    }

    private boolean isValid(String partialKey) {
        if (partialKey == null || partialKey.length() == 0) {
            return false;
        }
        Character first = partialKey.charAt(0);
        HashMap<String, String> submap = map.get(first);
        if (submap != null) {
            for (String search : submap.keySet()) {
                if (search.startsWith(partialKey)) {
                    return true; // partial match found
                }
            }
        }
        return false;
    }

    protected void put(String key, String value) {
        Character first = key.charAt(0);
        HashMap<String, String> submap = map.get(first);
        if (submap == null) {
            submap = new HashMap<String, String>();
            map.put(first, submap);
        }
        submap.put(key, value);
    }

    private String composeBuffer;
    private ComposeSequencing composeUser;

    protected void init(ComposeSequencing user) {
        composeBuffer = "";
        composeUser = user;
    }

    public void clear() {
        composeBuffer = "";
    }

    // returns true if the compose sequence is valid but incomplete
    public String executeToString(int code) {
        KeyboardSwitcher ks = KeyboardSwitcher.getInstance();
        if (ks.getInputView().isShifted()
                && ks.isAlphabetMode()
                && Character.isLowerCase(code)) {
            code = Character.toUpperCase(code);
        }
        composeBuffer += (char) code;
        composeUser.updateShiftKeyState(composeUser.getCurrentInputEditorInfo());

        String composed = get(composeBuffer);
        if (composed != null) {
            // If we get here, we have a complete compose sequence
            composeBuffer = "";
            return composed;
        } else if (!isValid(composeBuffer)) {
            // If we get here, then the sequence typed isn't recognised
            composeBuffer = "";
            return "";
        }
        return null;
    }

    public boolean execute(int code) {
        String composed = executeToString(code);
        if (composed != null) {
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
