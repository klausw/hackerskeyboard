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

import java.text.Normalizer;

import android.os.Build;
import android.util.Log;

public class DeadAccentSequence extends ComposeSequence {
    private static final String TAG = "HK/DeadAccent";

    public DeadAccentSequence(ComposeSequencing user) {
        super(user);
    }
    
    private static void putAccent(String nonSpacing, String spacing, String ascii) {
        if (ascii == null) ascii = spacing;
        put("" + nonSpacing + " ", ascii);
        put(nonSpacing + nonSpacing, spacing);
        put(Keyboard.DEAD_KEY_PLACEHOLDER + nonSpacing, spacing);
    }
    
    public static String getSpacing(char nonSpacing) {
        String spacing = get("" + Keyboard.DEAD_KEY_PLACEHOLDER + nonSpacing);
        if (spacing == null) spacing = DeadAccentSequence.normalize(" " + nonSpacing);
        if (spacing == null) return "" + nonSpacing;
        return spacing;
    }
    
    static {
        // space + combining diacritical
        // cf. http://unicode.org/charts/PDF/U0300.pdf
        putAccent("\u0300", "\u02cb", "`");  // grave
        putAccent("\u0301", "\u02ca", "´");  // acute
        putAccent("\u0302", "\u02c6", "^");  // circumflex
        putAccent("\u0303", "\u02dc", "~");  // small tilde
        putAccent("\u0304", "\u02c9", "¯");  // macron
        putAccent("\u0305", "\u00af", "¯");  // overline
        putAccent("\u0306", "\u02d8", null);  // breve
        putAccent("\u0307", "\u02d9", null);  // dot above
        putAccent("\u0308", "\u00a8", "¨");  // diaeresis
        putAccent("\u0309", "\u02c0", null);  // hook above
        putAccent("\u030a", "\u02da", "°");  // ring above
        putAccent("\u030b", "\u02dd", "\"");  // double acute 
        putAccent("\u030c", "\u02c7", null);  // caron
        putAccent("\u030d", "\u02c8", null);  // vertical line above
        putAccent("\u030e", "\"", "\"");  // double vertical line above
        putAccent("\u0313", "\u02bc", null);  // comma above
        putAccent("\u0314", "\u02bd", null);  // reversed comma above

        put("\u0308\u0301\u03b9", "\u0390");  // Greek Dialytika+Tonos, iota
        put("\u0301\u0308\u03b9", "\u0390");  // Greek Dialytika+Tonos, iota
        put("\u0301\u03ca", "\u0390");        // Greek Dialytika+Tonos, iota
        put("\u0308\u0301\u03c5", "\u03b0");  // Greek Dialytika+Tonos, upsilon
        put("\u0301\u0308\u03c5", "\u03b0");  // Greek Dialytika+Tonos, upsilon
        put("\u0301\u03cb", "\u03b0");        // Greek Dialytika+Tonos, upsilon
   }

	private static String doNormalise(String input)
	{
		if (Build.VERSION.SDK_INT >= 9) {
			return Normalizer.normalize(input, Normalizer.Form.NFC);
		}
		return input;
	}

    public static String normalize(String input) {
    	String lookup = mMap.get(input);
        return lookup != null ? lookup : doNormalise(input);
    }
    
    public boolean execute(int code) {
    	String composed = executeToString(code);
    	if (composed != null) {
    	        //Log.i(TAG, "composed=" + composed + " len=" + composed.length());
    		if (composed.equals("")) {
    			// Unrecognised - try to use the built-in Java text normalisation
    			int c = composeBuffer.codePointAt(composeBuffer.length() - 1);
    			if (Character.getType(c) != Character.NON_SPACING_MARK) {
					StringBuilder buildComposed = new StringBuilder(10);
					buildComposed.append(composeBuffer);
					// FIXME? Put the combining character(s) temporarily at the end, else this won't work
					composed = doNormalise(buildComposed.reverse().toString());
    				if (composed.equals("")) {
    					return true; // incomplete :-)
    				}
    			} else {
    				return true; // there may be multiple combining accents
    			}
    		}

    		clear();
    		composeUser.onText(composed);
    		return false;
    	}
    	return true;
    }
}
