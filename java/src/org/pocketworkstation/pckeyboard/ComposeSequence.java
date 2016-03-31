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
import android.util.SparseArray;
import android.view.inputmethod.EditorInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

interface ComposeSequencing {
    public void onText(CharSequence text);
    public void updateShiftKeyState(EditorInfo attr);
    public EditorInfo getCurrentInputEditorInfo();
}

public class ComposeSequence {
    private static final String TAG = "HK/ComposeSequence";
    
    protected static final Map<String, String> mMap =
    	new HashMap<String, String>();

    protected static final Set<String> mPrefixes =
    	new HashSet<String>();

    // Some convenience aliases for use in compose strings
    protected static final char UP          = (char)LatinKeyboardView.KEYCODE_DPAD_UP;
    protected static final char DOWN        = (char)LatinKeyboardView.KEYCODE_DPAD_DOWN;
    protected static final char LEFT        = (char)LatinKeyboardView.KEYCODE_DPAD_LEFT;
    protected static final char RIGHT       = (char)LatinKeyboardView.KEYCODE_DPAD_RIGHT;
    protected static final char COMPOSE     = (char)LatinKeyboardView.KEYCODE_DPAD_CENTER;
    protected static final char PAGE_UP     = (char)LatinKeyboardView.KEYCODE_PAGE_UP;
    protected static final char PAGE_DOWN   = (char)LatinKeyboardView.KEYCODE_PAGE_DOWN;
    protected static final char ESCAPE      = (char)LatinKeyboardView.KEYCODE_ESCAPE;
    protected static final char DELETE      = (char)LatinKeyboardView.KEYCODE_FORWARD_DEL;
    protected static final char CAPS_LOCK   = (char)LatinKeyboardView.KEYCODE_CAPS_LOCK;
    protected static final char SCROLL_LOCK = (char)LatinKeyboardView.KEYCODE_SCROLL_LOCK;
    protected static final char SYSRQ       = (char)LatinKeyboardView.KEYCODE_SYSRQ;
    protected static final char BREAK       = (char)LatinKeyboardView.KEYCODE_BREAK;
    protected static final char HOME        = (char)LatinKeyboardView.KEYCODE_HOME;
    protected static final char END         = (char)LatinKeyboardView.KEYCODE_END;
    protected static final char INSERT      = (char)LatinKeyboardView.KEYCODE_INSERT;
    protected static final char F1          = (char)LatinKeyboardView.KEYCODE_FKEY_F1;
    protected static final char F2          = (char)LatinKeyboardView.KEYCODE_FKEY_F2;
    protected static final char F3          = (char)LatinKeyboardView.KEYCODE_FKEY_F3;
    protected static final char F4          = (char)LatinKeyboardView.KEYCODE_FKEY_F4;
    protected static final char F5          = (char)LatinKeyboardView.KEYCODE_FKEY_F5;
    protected static final char F6          = (char)LatinKeyboardView.KEYCODE_FKEY_F6;
    protected static final char F7          = (char)LatinKeyboardView.KEYCODE_FKEY_F7;
    protected static final char F8          = (char)LatinKeyboardView.KEYCODE_FKEY_F8;
    protected static final char F9          = (char)LatinKeyboardView.KEYCODE_FKEY_F9;
    protected static final char F10         = (char)LatinKeyboardView.KEYCODE_FKEY_F10;
    protected static final char F11         = (char)LatinKeyboardView.KEYCODE_FKEY_F11;
    protected static final char F12         = (char)LatinKeyboardView.KEYCODE_FKEY_F12;
    protected static final char NUM_LOCK    = (char)LatinKeyboardView.KEYCODE_NUM_LOCK;

    private static final SparseArray<String> keyNames = new SparseArray<String>() {
        {
            append('"', "quot");
            append(UP, "↑");
            append(DOWN, "↓");
            append(LEFT, "←");
            append(RIGHT, "→");
            append(COMPOSE, "◯");
            append(PAGE_UP, "PgUp");
            append(PAGE_DOWN, "PgDn");
            append(ESCAPE, "Esc");
            append(DELETE, "Del");
            append(CAPS_LOCK, "Caps");
            append(SCROLL_LOCK, "Scroll");
            append(SYSRQ, "SysRq");
            append(BREAK, "Break");
            append(HOME, "Home");
            append(END, "End");
            append(INSERT, "Insert");
            append(F1, "F1");
            append(F2, "F2");
            append(F3, "F3");
            append(F4, "F4");
            append(F5, "F5");
            append(F6, "F6");
            append(F7, "F7");
            append(F8, "F8");
            append(F9, "F9");
            append(F10, "F10");
            append(F11, "F11");
            append(F12, "F12");
            append(NUM_LOCK, "Num");
        }
    };

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

    protected static String format(String seq) {
        String output = "";
        boolean quoted = false;
        final int end = seq.length();

        for (int i = 0; i < end; ++i) {
            char c = seq.charAt(i);
            if (keyNames.get(c) != null) {
                output += (quoted ? "\" " : ' ') + keyNames.get(c);
                quoted = false;
            } else {
                if (!quoted)
                    output += output.length() != 0 ? " \"" : "\"";
                if (c < ' ' || c == '"' || c == '\\')
                    output += "\\" + (c < ' ' ? c + 64 : c);
                else
                    output += c;
                quoted = true;
            }
        }
        if (quoted)
            output += '"';

        return output;
    }

    protected static void put(String key, String value) {
        boolean found = false;

        if (key.length() == 0 || value.length() == 0)
            return;

        if (mMap.containsKey(key))
            Log.w(TAG, "compose sequence is a duplicate: " + format(key));
        else if (mPrefixes.contains(key))
            Log.w(TAG, "compose sequence is a subset: " + format(key));

        mMap.put(key, value);
    	for (int i = 1; i < key.length(); ++i) {
            String substr = key.substring(0, i);
            found |= mMap.containsKey(substr);
            mPrefixes.add(substr);
    	}

        if (found)
            Log.w(TAG, "compose sequence is a superset: " + format(key));
    }

    protected StringBuilder composeBuffer = new StringBuilder(10);
    protected ComposeSequencing composeUser;

    protected void init(ComposeSequencing user) {
        clear();
        composeUser = user;
    }

    public ComposeSequence(ComposeSequencing user) {
        init(user);
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

   private static void reset() {
        put("++", "#");
        put("' ", "'");
        put(" '", "'");
        put("AT", "@");
        put("((", "[");
        put("//", "\\");
        put("/<", "\\");
        put("</", "\\");
        put("))", "]");
        put("^ ", "^");
        put(" ^", "^");
        put("> ", "^");
        put(" >", "^");
        put("` ", "`");
        put(" `", "`");
        put(", ", "¸");
        put(" ,", "¸");
        put("(-", "{");
        put("-(", "{");
        put("/^", "|");
        put("^/", "|");
        put("VL", "|");
        put("LV", "|");
        put("vl", "|");
        put("lv", "|");
        put(")-", "}");
        put("-)", "}");
        put("~ ", "~");
        put(" ~", "~");
        put("- ", "~");
        put(" -", "~");
        put("  ", " ");
        put(" .", " ");
        put("oc", "©");
        put("oC", "©");
        put("Oc", "©");
        put("OC", "©");
        put("or", "®");
        put("oR", "®");
        put("Or", "®");
        put("OR", "®");
        put(".>", "›");
        put(".<", "‹");
        put("..", "…");
        put(".-", "·");
        put(".=", "•");
        put("!^", "¦");
        put("!!", "¡");
        put("p!", "¶");
        put("P!", "¶");
        put("+-", "±");
        put("??", "¿");
        put("-d", "đ");
        put("-D", "Đ");
        put("ss", "ß");
        put("SS", "ẞ");
        put("oe", "œ");
        put("OE", "Œ");
        put("ae", "æ");
        put("AE", "Æ");
        put("oo", "°");
        put("\"\\", "〝");
        put("\"/", "〞");
        put("<<", "«");
        put(">>", "»");
        put("<'", "‘");
        put("'<", "‘");
        put(">'", "’");
        put("'>", "’");
        put(",'", "‚");
        put("',", "‚");
        put("<\"", "“");
        put("\"<", "“");
        put(">\"", "”");
        put("\">", "”");
        put(",\"", "„");
        put("\",", "„");
        put("%o", "‰");
        put("CE", "₠");
        put("C/", "₡");
        put("/C", "₡");
        put("Cr", "₢");
        put("Fr", "₣");
        put("L=", "₤");
        put("=L", "₤");
        put("m/", "₥");
        put("/m", "₥");
        put("N=", "₦");
        put("=N", "₦");
        put("Pt", "₧");
        put("Rs", "₨");
        put("W=", "₩");
        put("=W", "₩");
        put("d-", "₫");
        put("C=", "€");
        put("=C", "€");
        put("c=", "€");
        put("=c", "€");
        put("E=", "€");
        put("=E", "€");
        put("e=", "€");
        put("=e", "€");
        put("|c", "¢");
        put("c|", "¢");
        put("c/", "¢");
        put("/c", "¢");
        put("L-", "£");
        put("-L", "£");
        put("Y=", "¥");
        put("=Y", "¥");
        put("fs", "ſ");
        put("fS", "ſ");
        put("--.", "–");
        put("---", "—");
        put("#b", "♭");
        put("#f", "♮");
        put("##", "♯");
        put("so", "§");
        put("os", "§");
        put("ox", "¤");
        put("xo", "¤");
        put("PP", "¶");
        put("No", "№");
        put("NO", "№");
        put("?!", "⸘");
        put("!?", "‽");
        put("CCCP", "☭");
        put("OA", "Ⓐ");
        put("<3", "♥");
        put(":)", "☺");
        put(":(", "☹");
        put(",-", "¬");
        put("-,", "¬");
        put("^_a", "ª");
        put("^2", "²");
        put("^3", "³");
        put("mu", "µ");
        put("^1", "¹");
        put("^_o", "º");
        put("14", "¼");
        put("12", "½");
        put("34", "¾");
        put("`A", "À");
        put("'A", "Á");
        put("^A", "Â");
        put("~A", "Ã");
        put("\"A", "Ä");
        put("oA", "Å");
        put(",C", "Ç");
        put("`E", "È");
        put("'E", "É");
        put("^E", "Ê");
        put("\"E", "Ë");
        put("`I", "Ì");
        put("'I", "Í");
        put("^I", "Î");
        put("\"I", "Ï");
        put("DH", "Ð");
        put("~N", "Ñ");
        put("`O", "Ò");
        put("'O", "Ó");
        put("^O", "Ô");
        put("~O", "Õ");
        put("\"O", "Ö");
        put("xx", "×");
        put("/O", "Ø");
        put("`U", "Ù");
        put("'U", "Ú");
        put("^U", "Û");
        put("\"U", "Ü");
        put("'Y", "Ý");
        put("TH", "Þ");
        put("`a", "à");
        put("'a", "á");
        put("^a", "â");
        put("~a", "ã");
        put("\"a", "ä");
        put("oa", "å");
        put(",c", "ç");
        put("`e", "è");
        put("'e", "é");
        put("^e", "ê");
        put("\"e", "ë");
        put("`i", "ì");
        put("'i", "í");
        put("^i", "î");
        put("\"i", "ï");
        put("dh", "ð");
        put("~n", "ñ");
        put("`o", "ò");
        put("'o", "ó");
        put("^o", "ô");
        put("~o", "õ");
        put("\"o", "ö");
        put(":-", "÷");
        put("-:", "÷");
        put("/o", "ø");
        put("`u", "ù");
        put("'u", "ú");
        put("^u", "û");
        put("\"u", "ü");
        put("'y", "ý");
        put("th", "þ");
        put("\"y", "ÿ");
        put("_A", "Ā");
        put("_a", "ā");
        put("UA", "Ă");
        put("bA", "Ă");
        put("Ua", "ă");
        put("ba", "ă");
        put(";A", "Ą");
        put(",A", "Ą");
        put(";a", "ą");
        put(",a", "ą");
        put("'C", "Ć");
        put("'c", "ć");
        put("^C", "Ĉ");
        put("^c", "ĉ");
        put(".C", "Ċ");
        put(".c", "ċ");
        put("cC", "Č");
        put("cc", "č");
        put("cD", "Ď");
        put("cd", "ď");
        put("/D", "Đ");
        put("/d", "đ");
        put("_E", "Ē");
        put("_e", "ē");
        put("UE", "Ĕ");
        put("bE", "Ĕ");
        put("Ue", "ĕ");
        put("be", "ĕ");
        put(".E", "Ė");
        put(".e", "ė");
        put(";E", "Ę");
        put(",E", "Ę");
        put(";e", "ę");
        put(",e", "ę");
        put("cE", "Ě");
        put("ce", "ě");
        //put("ff", "ﬀ"); // Not usable, interferes with ffi/ffl prefix
        put("+f", "ﬀ");
        put("f+", "ﬀ");
        put("fi", "ﬁ");
        put("fl", "ﬂ");
        put("ffi", "ﬃ");
        put("ffl", "ﬄ");
        put("^G", "Ĝ");
        put("^g", "ĝ");
        put("UG", "Ğ");
        put("bG", "Ğ");
        put("Ug", "ğ");
        put("bg", "ğ");
        put(".G", "Ġ");
        put(".g", "ġ");
        put(",G", "Ģ");
        put(",g", "ģ");
        put("^H", "Ĥ");
        put("^h", "ĥ");
        put("/H", "Ħ");
        put("/h", "ħ");
        put("~I", "Ĩ");
        put("~i", "ĩ");
        put("_I", "Ī");
        put("_i", "ī");
        put("UI", "Ĭ");
        put("bI", "Ĭ");
        put("Ui", "ĭ");
        put("bi", "ĭ");
        put(";I", "Į");
        put(",I", "Į");
        put(";i", "į");
        put(",i", "į");
        put(".I", "İ");
        put("i.", "ı");
        put("^J", "Ĵ");
        put("^j", "ĵ");
        put(",K", "Ķ");
        put(",k", "ķ");
        put("kk", "ĸ");
        put("'L", "Ĺ");
        put("'l", "ĺ");
        put(",L", "Ļ");
        put(",l", "ļ");
        put("cL", "Ľ");
        put("cl", "ľ");
        put("/L", "Ł");
        put("/l", "ł");
        put("'N", "Ń");
        put("'n", "ń");
        put(",N", "Ņ");
        put(",n", "ņ");
        put("cN", "Ň");
        put("cn", "ň");
        put("NG", "Ŋ");
        put("ng", "ŋ");
        put("_O", "Ō");
        put("_o", "ō");
        put("UO", "Ŏ");
        put("bO", "Ŏ");
        put("Uo", "ŏ");
        put("bo", "ŏ");
        put("=O", "Ő");
        put("=o", "ő");
        put("'R", "Ŕ");
        put("'r", "ŕ");
        put(",R", "Ŗ");
        put(",r", "ŗ");
        put("cR", "Ř");
        put("cr", "ř");
        put("'S", "Ś");
        put("'s", "ś");
        put("^S", "Ŝ");
        put("^s", "ŝ");
        put(",S", "Ş");
        put(",s", "ş");
        put("cS", "Š");
        put("cs", "š");
        put(",T", "Ţ");
        put(",t", "ţ");
        put("cT", "Ť");
        put("ct", "ť");
        put("/T", "Ŧ");
        put("/t", "ŧ");
        put("~U", "Ũ");
        put("~u", "ũ");
        put("_U", "Ū");
        put("_u", "ū");
        put("UU", "Ŭ");
        put("bU", "Ŭ");
        put("Uu", "ŭ");
        put("uu", "ŭ");
        put("bu", "ŭ");
        put("oU", "Ů");
        put("ou", "ů");
        put("=U", "Ű");
        put("=u", "ű");
        put(";U", "Ų");
        put(",U", "Ų");
        put(";u", "ų");
        put(",u", "ų");
        put("^W", "Ŵ");
        put("^w", "ŵ");
        put("^Y", "Ŷ");
        put("^y", "ŷ");
        put("\"Y", "Ÿ");
        put("'Z", "Ź");
        put("'z", "ź");
        put(".Z", "Ż");
        put(".z", "ż");
        put("cZ", "Ž");
        put("cz", "ž");
        put("/b", "ƀ");
        put("/I", "Ɨ");
        put("+O", "Ơ");
        put("+o", "ơ");
        put("+U", "Ư");
        put("+u", "ư");
        put("/Z", "Ƶ");
        put("/z", "ƶ");
        put("cA", "Ǎ");
        put("ca", "ǎ");
        put("cI", "Ǐ");
        put("ci", "ǐ");
        put("cO", "Ǒ");
        put("co", "ǒ");
        put("cU", "Ǔ");
        put("cu", "ǔ");
        put("_Ü", "Ǖ");
        put("_\"U", "Ǖ");
        put("_ü", "ǖ");
        put("_\"u", "ǖ");
        put("'Ü", "Ǘ");
        put("'\"U", "Ǘ");
        put("'ü", "ǘ");
        put("'\"u", "ǘ");
        put("cÜ", "Ǚ");
        put("c\"U", "Ǚ");
        put("cü", "ǚ");
        put("c\"u", "ǚ");
        put("`Ü", "Ǜ");
        put("`\"U", "Ǜ");
        put("`ü", "ǜ");
        put("`\"u", "ǜ");
        put("_Ä", "Ǟ");
        put("_\"A", "Ǟ");
        put("_ä", "ǟ");
        put("_\"a", "ǟ");
        put("_.A", "Ǡ");
        put("_.a", "ǡ");
        put("_Æ", "Ǣ");
        put("_æ", "ǣ");
        put("/G", "Ǥ");
        put("/g", "ǥ");
        put("cG", "Ǧ");
        put("cg", "ǧ");
        put("cK", "Ǩ");
        put("ck", "ǩ");
        put(";O", "Ǫ");
        put(";o", "ǫ");
        put("_;O", "Ǭ");
        put("_;o", "ǭ");
        put("cj", "ǰ");
        put("'G", "Ǵ");
        put("'g", "ǵ");
        put("`N", "Ǹ");
        put("`n", "ǹ");
        put("'Å", "Ǻ");
        put("o'A", "Ǻ");
        put("'å", "ǻ");
        put("o'a", "ǻ");
        put("'Æ", "Ǽ");
        put("'æ", "ǽ");
        put("'Ø", "Ǿ");
        put("'/O", "Ǿ");
        put("'ø", "ǿ");
        put("'/o", "ǿ");
        put("cH", "Ȟ");
        put("ch", "ȟ");
        put(".A", "Ȧ");
        put(".a", "ȧ");
        put("_Ö", "Ȫ");
        put("_\"O", "Ȫ");
        put("_ö", "ȫ");
        put("_\"o", "ȫ");
        put("_Õ", "Ȭ");
        put("_~O", "Ȭ");
        put("_õ", "ȭ");
        put("_~o", "ȭ");
        put(".O", "Ȯ");
        put(".o", "ȯ");
        put("_.O", "Ȱ");
        put("_.o", "ȱ");
        put("_Y", "Ȳ");
        put("_y", "ȳ");
        put("ee", "ə");
        put("/i", "ɨ");
        put("^_h", "ʰ");
        put("^_j", "ʲ");
        put("^_r", "ʳ");
        put("^_w", "ʷ");
        put("^_y", "ʸ");
        put("^_l", "ˡ");
        put("^_s", "ˢ");
        put("^_x", "ˣ");
        put("\"'", "̈́");
        put(".B", "Ḃ");
        put(".b", "ḃ");
        put("!B", "Ḅ");
        put("!b", "ḅ");
        put("'Ç", "Ḉ");
        put("'ç", "ḉ");
        put(".D", "Ḋ");
        put(".d", "ḋ");
        put("!D", "Ḍ");
        put("!d", "ḍ");
        put(",D", "Ḑ");
        put(",d", "ḑ");
        put("`Ē", "Ḕ");
        put("`_E", "Ḕ");
        put("`ē", "ḕ");
        put("`_e", "ḕ");
        put("'Ē", "Ḗ");
        put("'_E", "Ḗ");
        put("'ē", "ḗ");
        put("'_e", "ḗ");
        put("U,E", "Ḝ");
        put("b,E", "Ḝ");
        put("U,e", "ḝ");
        put("b,e", "ḝ");
        put(".F", "Ḟ");
        put(".f", "ḟ");
        put("_G", "Ḡ");
        put("_g", "ḡ");
        put(".H", "Ḣ");
        put(".h", "ḣ");
        put("!H", "Ḥ");
        put("!h", "ḥ");
        put("\"H", "Ḧ");
        put("\"h", "ḧ");
        put(",H", "Ḩ");
        put(",h", "ḩ");
        put("'Ï", "Ḯ");
        put("'\"I", "Ḯ");
        put("'ï", "ḯ");
        put("'\"i", "ḯ");
        put("'K", "Ḱ");
        put("'k", "ḱ");
        put("!K", "Ḳ");
        put("!k", "ḳ");
        put("!L", "Ḷ");
        put("!l", "ḷ");
        put("_!L", "Ḹ");
        put("_!l", "ḹ");
        put("'M", "Ḿ");
        put("'m", "ḿ");
        put(".M", "Ṁ");
        put(".m", "ṁ");
        put("!M", "Ṃ");
        put("!m", "ṃ");
        put(".N", "Ṅ");
        put(".n", "ṅ");
        put("!N", "Ṇ");
        put("!n", "ṇ");
        put("'Õ", "Ṍ");
        put("'~O", "Ṍ");
        put("'õ", "ṍ");
        put("'~o", "ṍ");
        put("\"Õ", "Ṏ");
        put("\"~O", "Ṏ");
        put("\"õ", "ṏ");
        put("\"~o", "ṏ");
        put("`Ō", "Ṑ");
        put("`_O", "Ṑ");
        put("`ō", "ṑ");
        put("`_o", "ṑ");
        put("'Ō", "Ṓ");
        put("'_O", "Ṓ");
        put("'ō", "ṓ");
        put("'_o", "ṓ");
        put("'P", "Ṕ");
        put("'p", "ṕ");
        put(".P", "Ṗ");
        put(".p", "ṗ");
        put(".R", "Ṙ");
        put(".r", "ṙ");
        put("!R", "Ṛ");
        put("!r", "ṛ");
        put("_!R", "Ṝ");
        put("_!r", "ṝ");
        put(".S", "Ṡ");
        put(".s", "ṡ");
        put("!S", "Ṣ");
        put("!s", "ṣ");
        put(".Ś", "Ṥ");
        put(".'S", "Ṥ");
        put(".ś", "ṥ");
        put(".'s", "ṥ");
        put(".Š", "Ṧ");
        put(".š", "ṧ");
        put(".!S", "Ṩ");
        put(".!s", "ṩ");
        put(".T", "Ṫ");
        put(".t", "ṫ");
        put("!T", "Ṭ");
        put("!t", "ṭ");
        put("'Ũ", "Ṹ");
        put("'~U", "Ṹ");
        put("'ũ", "ṹ");
        put("'~u", "ṹ");
        put("\"Ū", "Ṻ");
        put("\"_U", "Ṻ");
        put("\"ū", "ṻ");
        put("\"_u", "ṻ");
        put("~V", "Ṽ");
        put("~v", "ṽ");
        put("!V", "Ṿ");
        put("!v", "ṿ");
        put("`W", "Ẁ");
        put("`w", "ẁ");
        put("'W", "Ẃ");
        put("'w", "ẃ");
        put("\"W", "Ẅ");
        put("\"w", "ẅ");
        put(".W", "Ẇ");
        put(".w", "ẇ");
        put("!W", "Ẉ");
        put("!w", "ẉ");
        put(".X", "Ẋ");
        put(".x", "ẋ");
        put("\"X", "Ẍ");
        put("\"x", "ẍ");
        put(".Y", "Ẏ");
        put(".y", "ẏ");
        put("^Z", "Ẑ");
        put("^z", "ẑ");
        put("!Z", "Ẓ");
        put("!z", "ẓ");
        put("\"t", "ẗ");
        put("ow", "ẘ");
        put("oy", "ẙ");
        put("!A", "Ạ");
        put("!a", "ạ");
        put("?A", "Ả");
        put("?a", "ả");
        put("'Â", "Ấ");
        put("'^A", "Ấ");
        put("'â", "ấ");
        put("'^a", "ấ");
        put("`Â", "Ầ");
        put("`^A", "Ầ");
        put("`â", "ầ");
        put("`^a", "ầ");
        put("?Â", "Ẩ");
        put("?^A", "Ẩ");
        put("?â", "ẩ");
        put("?^a", "ẩ");
        put("~Â", "Ẫ");
        put("~^A", "Ẫ");
        put("~â", "ẫ");
        put("~^a", "ẫ");
        put("^!A", "Ậ");
        put("^!a", "ậ");
        put("'Ă", "Ắ");
        put("'bA", "Ắ");
        put("'ă", "ắ");
        put("'ba", "ắ");
        put("`Ă", "Ằ");
        put("`bA", "Ằ");
        put("`ă", "ằ");
        put("`ba", "ằ");
        put("?Ă", "Ẳ");
        put("?bA", "Ẳ");
        put("?ă", "ẳ");
        put("?ba", "ẳ");
        put("~Ă", "Ẵ");
        put("~bA", "Ẵ");
        put("~ă", "ẵ");
        put("~ba", "ẵ");
        put("U!A", "Ặ");
        put("b!A", "Ặ");
        put("U!a", "ặ");
        put("b!a", "ặ");
        put("!E", "Ẹ");
        put("!e", "ẹ");
        put("?E", "Ẻ");
        put("?e", "ẻ");
        put("~E", "Ẽ");
        put("~e", "ẽ");
        put("'Ê", "Ế");
        put("'^E", "Ế");
        put("'ê", "ế");
        put("'^e", "ế");
        put("`Ê", "Ề");
        put("`^E", "Ề");
        put("`ê", "ề");
        put("`^e", "ề");
        put("?Ê", "Ể");
        put("?^E", "Ể");
        put("?ê", "ể");
        put("?^e", "ể");
        put("~Ê", "Ễ");
        put("~^E", "Ễ");
        put("~ê", "ễ");
        put("~^e", "ễ");
        put("^!E", "Ệ");
        put("^!e", "ệ");
        put("?I", "Ỉ");
        put("?i", "ỉ");
        put("!I", "Ị");
        put("!i", "ị");
        put("!O", "Ọ");
        put("!o", "ọ");
        put("?O", "Ỏ");
        put("?o", "ỏ");
        put("'Ô", "Ố");
        put("'^O", "Ố");
        put("'ô", "ố");
        put("'^o", "ố");
        put("`Ô", "Ồ");
        put("`^O", "Ồ");
        put("`ô", "ồ");
        put("`^o", "ồ");
        put("?Ô", "Ổ");
        put("?^O", "Ổ");
        put("?ô", "ổ");
        put("?^o", "ổ");
        put("~Ô", "Ỗ");
        put("~^O", "Ỗ");
        put("~ô", "ỗ");
        put("~^o", "ỗ");
        put("^!O", "Ộ");
        put("^!o", "ộ");
        put("'Ơ", "Ớ");
        put("'+O", "Ớ");
        put("'ơ", "ớ");
        put("'+o", "ớ");
        put("`Ơ", "Ờ");
        put("`+O", "Ờ");
        put("`ơ", "ờ");
        put("`+o", "ờ");
        put("?Ơ", "Ở");
        put("?+O", "Ở");
        put("?ơ", "ở");
        put("?+o", "ở");
        put("~Ơ", "Ỡ");
        put("~+O", "Ỡ");
        put("~ơ", "ỡ");
        put("~+o", "ỡ");
        put("!Ơ", "Ợ");
        put("!+O", "Ợ");
        put("!ơ", "ợ");
        put("!+o", "ợ");
        put("!U", "Ụ");
        put("!u", "ụ");
        put("?U", "Ủ");
        put("?u", "ủ");
        put("'Ư", "Ứ");
        put("'+U", "Ứ");
        put("'ư", "ứ");
        put("'+u", "ứ");
        put("`Ư", "Ừ");
        put("`+U", "Ừ");
        put("`ư", "ừ");
        put("`+u", "ừ");
        put("?Ư", "Ử");
        put("?+U", "Ử");
        put("?ư", "ử");
        put("?+u", "ử");
        put("~Ư", "Ữ");
        put("~+U", "Ữ");
        put("~ư", "ữ");
        put("~+u", "ữ");
        put("!Ư", "Ự");
        put("!+U", "Ự");
        put("!ư", "ự");
        put("!+u", "ự");
        put("`Y", "Ỳ");
        put("`y", "ỳ");
        put("!Y", "Ỵ");
        put("!y", "ỵ");
        put("?Y", "Ỷ");
        put("?y", "ỷ");
        put("~Y", "Ỹ");
        put("~y", "ỹ");
        put("^0", "⁰");
        put("^_i", "ⁱ");
        put("^4", "⁴");
        put("^5", "⁵");
        put("^6", "⁶");
        put("^7", "⁷");
        put("^8", "⁸");
        put("^9", "⁹");
        put("^+", "⁺");
        put("^=", "⁼");
        put("^(", "⁽");
        put("^)", "⁾");
        put("^_n", "ⁿ");
        put("_0", "₀");
        put("_1", "₁");
        put("_2", "₂");
        put("_3", "₃");
        put("_4", "₄");
        put("_5", "₅");
        put("_6", "₆");
        put("_7", "₇");
        put("_8", "₈");
        put("_9", "₉");
        put("_+", "₊");
        put("_=", "₌");
        put("_(", "₍");
        put("_)", "₎");
        put("SM", "℠");
        put("sM", "℠");
        put("Sm", "℠");
        put("sm", "℠");
        put("TM", "™");
        put("tM", "™");
        put("Tm", "™");
        put("tm", "™");
        put("13", "⅓");
        put("23", "⅔");
        put("15", "⅕");
        put("25", "⅖");
        put("35", "⅗");
        put("45", "⅘");
        put("16", "⅙");
        put("56", "⅚");
        put("18", "⅛");
        put("38", "⅜");
        put("58", "⅝");
        put("78", "⅞");
        put("/←", "↚");
        put("/→", "↛");
        put("<-", "←");
        put("->", "→");
        put("/=", "≠");
        put("=/", "≠");
        put("<=", "≤");
        put(">=", "≥");
        put("(1)", "①");
        put("(2)", "②");
        put("(3)", "③");
        put("(4)", "④");
        put("(5)", "⑤");
        put("(6)", "⑥");
        put("(7)", "⑦");
        put("(8)", "⑧");
        put("(9)", "⑨");
        put("(10)", "⑩");
        put("(11)", "⑪");
        put("(12)", "⑫");
        put("(13)", "⑬");
        put("(14)", "⑭");
        put("(15)", "⑮");
        put("(16)", "⑯");
        put("(17)", "⑰");
        put("(18)", "⑱");
        put("(19)", "⑲");
        put("(20)", "⑳");
        put("(A)", "Ⓐ");
        put("(B)", "Ⓑ");
        put("(C)", "Ⓒ");
        put("(D)", "Ⓓ");
        put("(E)", "Ⓔ");
        put("(F)", "Ⓕ");
        put("(G)", "Ⓖ");
        put("(H)", "Ⓗ");
        put("(I)", "Ⓘ");
        put("(J)", "Ⓙ");
        put("(K)", "Ⓚ");
        put("(L)", "Ⓛ");
        put("(M)", "Ⓜ");
        put("(N)", "Ⓝ");
        put("(O)", "Ⓞ");
        put("(P)", "Ⓟ");
        put("(Q)", "Ⓠ");
        put("(R)", "Ⓡ");
        put("(S)", "Ⓢ");
        put("(T)", "Ⓣ");
        put("(U)", "Ⓤ");
        put("(V)", "Ⓥ");
        put("(W)", "Ⓦ");
        put("(X)", "Ⓧ");
        put("(Y)", "Ⓨ");
        put("(Z)", "Ⓩ");
        put("(a)", "ⓐ");
        put("(b)", "ⓑ");
        put("(c)", "ⓒ");
        put("(d)", "ⓓ");
        put("(e)", "ⓔ");
        put("(f)", "ⓕ");
        put("(g)", "ⓖ");
        put("(h)", "ⓗ");
        put("(i)", "ⓘ");
        put("(j)", "ⓙ");
        put("(k)", "ⓚ");
        put("(l)", "ⓛ");
        put("(m)", "ⓜ");
        put("(n)", "ⓝ");
        put("(o)", "ⓞ");
        put("(p)", "ⓟ");
        put("(q)", "ⓠ");
        put("(r)", "ⓡ");
        put("(s)", "ⓢ");
        put("(t)", "ⓣ");
        put("(u)", "ⓤ");
        put("(v)", "ⓥ");
        put("(w)", "ⓦ");
        put("(x)", "ⓧ");
        put("(y)", "ⓨ");
        put("(z)", "ⓩ");
        put("(0)", "⓪");
        put("(21)", "㉑");
        put("(22)", "㉒");
        put("(23)", "㉓");
        put("(24)", "㉔");
        put("(25)", "㉕");
        put("(26)", "㉖");
        put("(27)", "㉗");
        put("(28)", "㉘");
        put("(29)", "㉙");
        put("(30)", "㉚");
        put("(31)", "㉛");
        put("(32)", "㉜");
        put("(33)", "㉝");
        put("(34)", "㉞");
        put("(35)", "㉟");
        put("(36)", "㊱");
        put("(37)", "㊲");
        put("(38)", "㊳");
        put("(39)", "㊴");
        put("(40)", "㊵");
        put("(41)", "㊶");
        put("(42)", "㊷");
        put("(43)", "㊸");
        put("(44)", "㊹");
        put("(45)", "㊺");
        put("(46)", "㊻");
        put("(47)", "㊼");
        put("(48)", "㊽");
        put("(49)", "㊾");
        put("(50)", "㊿");
        put("\\o/", "🙌");
    }

    static { reset(); }
}
