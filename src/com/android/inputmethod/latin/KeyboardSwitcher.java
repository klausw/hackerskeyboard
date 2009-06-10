/*
 * Copyright (C) 2008 Google Inc.
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

package com.android.inputmethod.latin;

import android.inputmethodservice.Keyboard;

import java.util.List;

public class KeyboardSwitcher {

    public static final int MODE_TEXT = 1;
    public static final int MODE_SYMBOLS = 2;
    public static final int MODE_PHONE = 3;
    public static final int MODE_URL = 4;
    public static final int MODE_EMAIL = 5;
    public static final int MODE_IM = 6;
    
    public static final int MODE_TEXT_QWERTY = 0;
    public static final int MODE_TEXT_ALPHA = 1;
    public static final int MODE_TEXT_COUNT = 2;
    
    public static final int KEYBOARDMODE_NORMAL = R.id.mode_normal;
    public static final int KEYBOARDMODE_URL = R.id.mode_url;
    public static final int KEYBOARDMODE_EMAIL = R.id.mode_email;
    public static final int KEYBOARDMODE_IM = R.id.mode_im;
    

    LatinKeyboardView mInputView;
    LatinIME mContext;
    
    private LatinKeyboard mPhoneKeyboard;
    private LatinKeyboard mPhoneSymbolsKeyboard;
    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mAlphaKeyboard;
    private LatinKeyboard mUrlKeyboard;
    private LatinKeyboard mEmailKeyboard;
    private LatinKeyboard mIMKeyboard;
    
    List<Keyboard> mKeyboards;
    
    private int mMode;
    private int mImeOptions;
    private int mTextMode = MODE_TEXT_QWERTY;

    private int mLastDisplayWidth;

    KeyboardSwitcher(LatinIME context) {
        mContext = context;
    }

    void setInputView(LatinKeyboardView inputView) {
        mInputView = inputView;
    }
    
    void makeKeyboards() {
        // Configuration change is coming after the keyboard gets recreated. So don't rely on that.
        // If keyboards have already been made, check if we have a screen width change and 
        // create the keyboard layouts again at the correct orientation
        if (mKeyboards != null) {
            int displayWidth = mContext.getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        // Delayed creation when keyboard mode is set.
        mQwertyKeyboard = null;
        mAlphaKeyboard = null;
        mUrlKeyboard = null;
        mEmailKeyboard = null;
        mIMKeyboard = null;
        mPhoneKeyboard = null;
        mPhoneSymbolsKeyboard = null;
        mSymbolsKeyboard = null;
        mSymbolsShiftedKeyboard = null;
    }

    void setKeyboardMode(int mode, int imeOptions) {
        mMode = mode;
        mImeOptions = imeOptions;
        LatinKeyboard keyboard = (LatinKeyboard) mInputView.getKeyboard();
        mInputView.setPreviewEnabled(true);
        switch (mode) {
            case MODE_TEXT:
                if (mTextMode == MODE_TEXT_QWERTY) {
                    if (mQwertyKeyboard == null) {
                        mQwertyKeyboard = new LatinKeyboard(mContext, R.xml.kbd_qwerty,
                                KEYBOARDMODE_NORMAL);
                        mQwertyKeyboard.enableShiftLock();
                    }
                    keyboard = mQwertyKeyboard;
                } else if (mTextMode == MODE_TEXT_ALPHA) {
                    if (mAlphaKeyboard == null) {
                        mAlphaKeyboard = new LatinKeyboard(mContext, R.xml.kbd_alpha,
                                KEYBOARDMODE_NORMAL);
                        mAlphaKeyboard.enableShiftLock();
                    }
                    keyboard = mAlphaKeyboard;
                }
                break;
            case MODE_SYMBOLS:
                if (mSymbolsKeyboard == null) {
                    mSymbolsKeyboard = new LatinKeyboard(mContext, R.xml.kbd_symbols);
                }
                if (mSymbolsShiftedKeyboard == null) {
                    mSymbolsShiftedKeyboard = new LatinKeyboard(mContext, R.xml.kbd_symbols_shift);
                }
                keyboard = mSymbolsKeyboard;
                break;
            case MODE_PHONE:
                if (mPhoneKeyboard == null) {
                    mPhoneKeyboard = new LatinKeyboard(mContext, R.xml.kbd_phone);
                }
                mInputView.setPhoneKeyboard(mPhoneKeyboard);
                if (mPhoneSymbolsKeyboard == null) {
                    mPhoneSymbolsKeyboard = new LatinKeyboard(mContext, R.xml.kbd_phone_symbols);
                }
                keyboard = mPhoneKeyboard;
                mInputView.setPreviewEnabled(false);
                break;
            case MODE_URL:
                if (mUrlKeyboard == null) {
                    mUrlKeyboard = new LatinKeyboard(mContext, R.xml.kbd_qwerty, KEYBOARDMODE_URL);
                    mUrlKeyboard.enableShiftLock();
                }
                keyboard = mUrlKeyboard;
                break;
            case MODE_EMAIL:
                if (mEmailKeyboard == null) {
                    mEmailKeyboard = new LatinKeyboard(mContext, R.xml.kbd_qwerty, KEYBOARDMODE_EMAIL);
                    mEmailKeyboard.enableShiftLock();
                }
                keyboard = mEmailKeyboard;
                break;
            case MODE_IM:
                if (mIMKeyboard == null) {
                    mIMKeyboard = new LatinKeyboard(mContext, R.xml.kbd_qwerty, KEYBOARDMODE_IM);
                    mIMKeyboard.enableShiftLock();
                }
                keyboard = mIMKeyboard;
                break;
        }
        mInputView.setKeyboard(keyboard);
        keyboard.setShifted(false);
        keyboard.setShiftLocked(keyboard.isShiftLocked());
        keyboard.setImeOptions(mContext.getResources(), mMode, imeOptions);
    }

    int getKeyboardMode() {
        return mMode;
    }
    
    boolean isTextMode() {
        return mMode == MODE_TEXT;
    }
    
    int getTextMode() {
        return mTextMode;
    }
    
    void setTextMode(int position) {
        if (position < MODE_TEXT_COUNT && position >= 0) {
            mTextMode = position;
        }
        if (isTextMode()) {
            setKeyboardMode(MODE_TEXT, mImeOptions);
        }
    }

    int getTextModeCount() {
        return MODE_TEXT_COUNT;
    }

    boolean isAlphabetMode() {
        Keyboard current = mInputView.getKeyboard();
        if (current == mQwertyKeyboard
                || current == mAlphaKeyboard
                || current == mUrlKeyboard
                || current == mIMKeyboard
                || current == mEmailKeyboard) {
            return true;
        }
        return false;
    }

    void toggleShift() {
        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            mInputView.setKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
            mSymbolsShiftedKeyboard.setImeOptions(mContext.getResources(), mMode, mImeOptions);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            mInputView.setKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
            mSymbolsKeyboard.setImeOptions(mContext.getResources(), mMode, mImeOptions);
        }
    }

    void toggleSymbols() {
        Keyboard current = mInputView.getKeyboard();
        if (mSymbolsKeyboard == null) {
            mSymbolsKeyboard = new LatinKeyboard(mContext, R.xml.kbd_symbols);
        }
        if (mSymbolsShiftedKeyboard == null) {
            mSymbolsShiftedKeyboard = new LatinKeyboard(mContext, R.xml.kbd_symbols_shift);
        }
        if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
            setKeyboardMode(mMode, mImeOptions); // Could be qwerty, alpha, url, email or im
            return;
        } else if (current == mPhoneKeyboard) {
            current = mPhoneSymbolsKeyboard;
            mPhoneSymbolsKeyboard.setImeOptions(mContext.getResources(), mMode, mImeOptions);
        } else if (current == mPhoneSymbolsKeyboard) {
            current = mPhoneKeyboard;
            mPhoneKeyboard.setImeOptions(mContext.getResources(), mMode, mImeOptions);
        } else {
            current = mSymbolsKeyboard;
            mSymbolsKeyboard.setImeOptions(mContext.getResources(), mMode, mImeOptions);
        }
        mInputView.setKeyboard(current);
        if (current == mSymbolsKeyboard) {
            current.setShifted(false);
        }
    }
}
