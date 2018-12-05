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

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import org.pocketworkstation.pckeyboard.Keyboard.Key;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LatinKeyboardView extends LatinKeyboardBaseView {
    static final String TAG = "HK/LatinKeyboardView";

	// The keycode list needs to stay in sync with the
	// res/values/keycodes.xml file.

	// FIXME: The following keycodes should really be renumbered
	// since they conflict with existing KeyEvent keycodes.
    static final int KEYCODE_OPTIONS = -100;
    static final int KEYCODE_OPTIONS_LONGPRESS = -101;
    static final int KEYCODE_VOICE = -102;
    static final int KEYCODE_F1 = -103;
    static final int KEYCODE_NEXT_LANGUAGE = -104;
    static final int KEYCODE_PREV_LANGUAGE = -105;
    static final int KEYCODE_COMPOSE = -10024;
    
	// The following keycodes match (negative) KeyEvent keycodes.
    // Would be better to use the real KeyEvent values, but many
    // don't exist prior to the Honeycomb API (level 11).
    static final int KEYCODE_DPAD_UP = -19;
    static final int KEYCODE_DPAD_DOWN = -20;
    static final int KEYCODE_DPAD_LEFT = -21;
    static final int KEYCODE_DPAD_RIGHT = -22;
    static final int KEYCODE_DPAD_CENTER = -23;
    static final int KEYCODE_ALT_LEFT = -57;
    static final int KEYCODE_PAGE_UP = -92;
    static final int KEYCODE_PAGE_DOWN = -93;
    static final int KEYCODE_ESCAPE = -111;
    static final int KEYCODE_FORWARD_DEL = -112;
    static final int KEYCODE_CTRL_LEFT = -113;
    static final int KEYCODE_CAPS_LOCK = -115;
    static final int KEYCODE_SCROLL_LOCK = -116;
    static final int KEYCODE_META_LEFT = -117;
    static final int KEYCODE_FN = -119;
    static final int KEYCODE_SYSRQ = -120;
    static final int KEYCODE_BREAK = -121;
    static final int KEYCODE_HOME = -122;
    static final int KEYCODE_END = -123;
    static final int KEYCODE_INSERT = -124;
    static final int KEYCODE_FKEY_F1 = -131;
    static final int KEYCODE_FKEY_F2 = -132;
    static final int KEYCODE_FKEY_F3 = -133;
    static final int KEYCODE_FKEY_F4 = -134;
    static final int KEYCODE_FKEY_F5 = -135;
    static final int KEYCODE_FKEY_F6 = -136;
    static final int KEYCODE_FKEY_F7 = -137;
    static final int KEYCODE_FKEY_F8 = -138;
    static final int KEYCODE_FKEY_F9 = -139;
    static final int KEYCODE_FKEY_F10 = -140;
    static final int KEYCODE_FKEY_F11 = -141;
    static final int KEYCODE_FKEY_F12 = -142;
    static final int KEYCODE_NUM_LOCK = -143;

    private Keyboard mPhoneKeyboard;

    /** Whether the extension of this keyboard is visible */
    private boolean mExtensionVisible;
    /** The view that is shown as an extension of this keyboard view */
    private LatinKeyboardView mExtension;
    /** The popup window that contains the extension of this keyboard */
    private PopupWindow mExtensionPopup;
    /** Whether this view is an extension of another keyboard */
    private boolean mIsExtensionType;
    private boolean mFirstEvent;

    /** Whether we've started dropping move events because we found a big jump */
    private boolean mDroppingEvents;
    /**
     * Whether multi-touch disambiguation needs to be disabled for any reason. There are 2 reasons
     * for this to happen - (1) if a real multi-touch event has occured and (2) we've opened an 
     * extension keyboard.
     */
    private boolean mDisableDisambiguation;
    /** The distance threshold at which we start treating the touch session as a multi-touch */
    private int mJumpThresholdSquare = Integer.MAX_VALUE;
    /** The y coordinate of the last row */
    private int mLastRowY;
    private int mExtensionLayoutResId = 0;
    private LatinKeyboard mExtensionKeyboard;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // TODO(klausw): migrate attribute styles to LatinKeyboardView?
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.LatinKeyboardBaseView, defStyle, R.style.LatinKeyboardBaseView);
        LayoutInflater inflate =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int previewLayout = 0;
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);

            switch (attr) {
            case R.styleable.LatinKeyboardBaseView_keyPreviewLayout:
                previewLayout = a.getResourceId(attr, 0);
                if (previewLayout == R.layout.null_layout) previewLayout = 0;
                break;
            case R.styleable.LatinKeyboardBaseView_keyPreviewOffset:
                mPreviewOffset = a.getDimensionPixelOffset(attr, 0);
                break;
            case R.styleable.LatinKeyboardBaseView_keyPreviewHeight:
                mPreviewHeight = a.getDimensionPixelSize(attr, 80);
                break;
            case R.styleable.LatinKeyboardBaseView_popupLayout:
                mPopupLayout = a.getResourceId(attr, 0);
                if (mPopupLayout == R.layout.null_layout) mPopupLayout = 0;
                break;
            }
        }

        final Resources res = getResources();

        // If true, popups are forced to remain inside the keyboard area. If false,
        // they can extend above it. Enable clipping just for Android P since drawing
        // outside the keyboard area doesn't work on that version.
        boolean clippingEnabled = (Build.VERSION.SDK_INT >= 28 /* Build.VERSION_CODES.P */);

        if (previewLayout != 0) {
            mPreviewPopup = new PopupWindow(context);
            if (!isInEditMode())
                Log.i(TAG, "new mPreviewPopup " + mPreviewPopup + " from " + this);
            mPreviewText = (TextView) inflate.inflate(previewLayout, null);
            mPreviewTextSizeLarge = (int) res.getDimension(R.dimen.key_preview_text_size_large);
            mPreviewPopup.setContentView(mPreviewText);
            mPreviewPopup.setBackgroundDrawable(null);
            mPreviewPopup.setTouchable(false);
            mPreviewPopup.setAnimationStyle(R.style.KeyPreviewAnimation);
            mPreviewPopup.setClippingEnabled(clippingEnabled);
        } else {
            mShowPreview = false;
        }

        if (mPopupLayout != 0) {
            mMiniKeyboardParent = this;
            mMiniKeyboardPopup = new PopupWindow(context);
            if (!isInEditMode())
                Log.i(TAG, "new mMiniKeyboardPopup " + mMiniKeyboardPopup + " from " + this);
            mMiniKeyboardPopup.setBackgroundDrawable(null);
            mMiniKeyboardPopup.setAnimationStyle(R.style.MiniKeyboardAnimation);
            mMiniKeyboardPopup.setClippingEnabled(clippingEnabled);
            mMiniKeyboardVisible = false;
        }
    }

    public void setPhoneKeyboard(Keyboard phoneKeyboard) {
        mPhoneKeyboard = phoneKeyboard;
    }

    public void setExtensionLayoutResId (int id) {
        mExtensionLayoutResId = id;
    }
    
    @Override
    public void setPreviewEnabled(boolean previewEnabled) {
        if (getKeyboard() == mPhoneKeyboard) {
            // Phone keyboard never shows popup preview (except language switch).
            super.setPreviewEnabled(false);
        } else {
            super.setPreviewEnabled(previewEnabled);
        }
    }

    @Override
    public void setKeyboard(Keyboard newKeyboard) {
        final Keyboard oldKeyboard = getKeyboard();
        if (oldKeyboard instanceof LatinKeyboard) {
            // Reset old keyboard state before switching to new keyboard.
            ((LatinKeyboard)oldKeyboard).keyReleased();
        }
        super.setKeyboard(newKeyboard);
        // One-seventh of the keyboard width seems like a reasonable threshold
        mJumpThresholdSquare = newKeyboard.getMinWidth() / 7;
        mJumpThresholdSquare *= mJumpThresholdSquare;
        // Get Y coordinate of the last row based on the row count, assuming equal height
        int numRows = newKeyboard.mRowCount;
        mLastRowY = (newKeyboard.getHeight() * (numRows - 1)) / numRows;
        mExtensionKeyboard = ((LatinKeyboard) newKeyboard).getExtension();
        if (mExtensionKeyboard != null && mExtension != null) mExtension.setKeyboard(mExtensionKeyboard);
        setKeyboardLocal(newKeyboard);
    }

    @Override
    /*package*/ boolean enableSlideKeyHack() {
        return true;
    }

    @Override
    protected boolean onLongPress(Key key) {
        PointerTracker.clearSlideKeys();

        int primaryCode = key.codes[0];
        if (primaryCode == KEYCODE_OPTIONS) {
            return invokeOnKey(KEYCODE_OPTIONS_LONGPRESS);
        } else if (primaryCode == KEYCODE_DPAD_CENTER) {
            return invokeOnKey(KEYCODE_COMPOSE);
        } else if (primaryCode == '0' && getKeyboard() == mPhoneKeyboard) {
            // Long pressing on 0 in phone number keypad gives you a '+'.
            return invokeOnKey('+');
        } else {
            return super.onLongPress(key);
        }
    }

    private boolean invokeOnKey(int primaryCode) {
        getOnKeyboardActionListener().onKey(primaryCode, null,
                LatinKeyboardBaseView.NOT_A_TOUCH_COORDINATE,
                LatinKeyboardBaseView.NOT_A_TOUCH_COORDINATE);
        return true;
    }

    /**
     * This function checks to see if we need to handle any sudden jumps in the pointer location
     * that could be due to a multi-touch being treated as a move by the firmware or hardware.
     * Once a sudden jump is detected, all subsequent move events are discarded
     * until an UP is received.<P>
     * When a sudden jump is detected, an UP event is simulated at the last position and when
     * the sudden moves subside, a DOWN event is simulated for the second key.
     * @param me the motion event
     * @return true if the event was consumed, so that it doesn't continue to be handled by
     * KeyboardView.
     */
    private boolean handleSuddenJump(MotionEvent me) {
        final int action = me.getAction();
        final int x = (int) me.getX();
        final int y = (int) me.getY();
        boolean result = false;

        // Real multi-touch event? Stop looking for sudden jumps
        if (me.getPointerCount() > 1) {
            mDisableDisambiguation = true;
        }
        if (mDisableDisambiguation) {
            // If UP, reset the multi-touch flag
            if (action == MotionEvent.ACTION_UP) mDisableDisambiguation = false;
            return false;
        }

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // Reset the "session"
            mDroppingEvents = false;
            mDisableDisambiguation = false;
            break;
        case MotionEvent.ACTION_MOVE:
            // Is this a big jump?
            final int distanceSquare = (mLastX - x) * (mLastX - x) + (mLastY - y) * (mLastY - y);
            // Check the distance and also if the move is not entirely within the bottom row
            // If it's only in the bottom row, it might be an intentional slide gesture
            // for language switching
            if (distanceSquare > mJumpThresholdSquare
                    && (mLastY < mLastRowY || y < mLastRowY)) {
                // If we're not yet dropping events, start dropping and send an UP event
                if (!mDroppingEvents) {
                    mDroppingEvents = true;
                    // Send an up event
                    MotionEvent translated = MotionEvent.obtain(me.getEventTime(), me.getEventTime(),
                            MotionEvent.ACTION_UP,
                            mLastX, mLastY, me.getMetaState());
                    super.onTouchEvent(translated);
                    translated.recycle();
                }
                result = true;
            } else if (mDroppingEvents) {
                // If moves are small and we're already dropping events, continue dropping
                result = true;
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mDroppingEvents) {
                // Send a down event first, as we dropped a bunch of sudden jumps and assume that
                // the user is releasing the touch on the second key.
                MotionEvent translated = MotionEvent.obtain(me.getEventTime(), me.getEventTime(),
                        MotionEvent.ACTION_DOWN,
                        x, y, me.getMetaState());
                super.onTouchEvent(translated);
                translated.recycle();
                mDroppingEvents = false;
                // Let the up event get processed as well, result = false
            }
            break;
        }
        // Track the previous coordinate
        mLastX = x;
        mLastY = y;
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        if (LatinIME.sKeyboardSettings.showTouchPos || DEBUG_LINE) {
            mLastX = (int) me.getX();
            mLastY = (int) me.getY();
            invalidate();
        }

        // If an extension keyboard is visible or this is an extension keyboard, don't look
        // for sudden jumps. Otherwise, if there was a sudden jump, return without processing the
        // actual motion event.
        if (!mExtensionVisible && !mIsExtensionType
                && handleSuddenJump(me)) return true;
        // Reset any bounding box controls in the keyboard
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            keyboard.keyReleased();
        }

        if (me.getAction() == MotionEvent.ACTION_UP) {
            int languageDirection = keyboard.getLanguageChangeDirection();
            if (languageDirection != 0) {
                getOnKeyboardActionListener().onKey(
                        languageDirection == 1 ? KEYCODE_NEXT_LANGUAGE : KEYCODE_PREV_LANGUAGE,
                        null, mLastX, mLastY);
                me.setAction(MotionEvent.ACTION_CANCEL);
                keyboard.keyReleased();
                return super.onTouchEvent(me);
            }
        }

        // If we don't have an extension keyboard, don't go any further.
        if (keyboard.getExtension() == null) {
            return super.onTouchEvent(me);
        }
        // If the motion event is above the keyboard and it's not an UP event coming
        // even before the first MOVE event into the extension area
        if (me.getY() < 0 && (mExtensionVisible || me.getAction() != MotionEvent.ACTION_UP)) {
            if (mExtensionVisible) {
                int action = me.getAction();
                if (mFirstEvent) action = MotionEvent.ACTION_DOWN;
                mFirstEvent = false;
                MotionEvent translated = MotionEvent.obtain(me.getEventTime(), me.getEventTime(),
                        action,
                        me.getX(), me.getY() + mExtension.getHeight(), me.getMetaState());
                if (me.getActionIndex() > 0)
                    return true;  // ignore second touches to avoid "pointerIndex out of range"
                boolean result = mExtension.onTouchEvent(translated);
                translated.recycle();
                if (me.getAction() == MotionEvent.ACTION_UP
                        || me.getAction() == MotionEvent.ACTION_CANCEL) {
                    closeExtension();
                }
                return result;
            } else {
                if (swipeUp()) {
                    return true;
                } else if (openExtension()) {
                    MotionEvent cancel = MotionEvent.obtain(me.getDownTime(), me.getEventTime(),
                            MotionEvent.ACTION_CANCEL, me.getX() - 100, me.getY() - 100, 0);
                    super.onTouchEvent(cancel);
                    cancel.recycle();
                    if (mExtension.getHeight() > 0) {
                        MotionEvent translated = MotionEvent.obtain(me.getEventTime(),
                                me.getEventTime(),
                                MotionEvent.ACTION_DOWN,
                                me.getX(), me.getY() + mExtension.getHeight(),
                                me.getMetaState());
                        mExtension.onTouchEvent(translated);
                        translated.recycle();
                    } else {
                        mFirstEvent = true;
                    }
                    // Stop processing multi-touch errors
                    mDisableDisambiguation  = true;
                }
                return true;
            }
        } else if (mExtensionVisible) {
            closeExtension();
            // Send a down event into the main keyboard first
            MotionEvent down = MotionEvent.obtain(me.getEventTime(), me.getEventTime(),
                    MotionEvent.ACTION_DOWN,
                    me.getX(), me.getY(), me.getMetaState());
            super.onTouchEvent(down, true);
            down.recycle();
            // Send the actual event
            return super.onTouchEvent(me);
        } else {
            return super.onTouchEvent(me);
        }
    }

    private void setExtensionType(boolean isExtensionType) {
        mIsExtensionType = isExtensionType;
    }

    private boolean openExtension() {
        // If the current keyboard is not visible, or if the mini keyboard is active, don't show the popup
        if (!isShown() || popupKeyboardIsShowing()) {
            return false;
        }
        PointerTracker.clearSlideKeys();
        if (((LatinKeyboard) getKeyboard()).getExtension() == null) return false;
        makePopupWindow();
        mExtensionVisible = true;
        return true;
    }

    private void makePopupWindow() {
        dismissPopupKeyboard();
        if (mExtensionPopup == null) {
            int[] windowLocation = new int[2];
            mExtensionPopup = new PopupWindow(getContext());
            mExtensionPopup.setBackgroundDrawable(null);
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            mExtension = (LatinKeyboardView) li.inflate(mExtensionLayoutResId == 0 ?
                    R.layout.input_trans : mExtensionLayoutResId, null);
            Keyboard keyboard = mExtensionKeyboard;
            mExtension.setKeyboard(keyboard);
            mExtension.setExtensionType(true);
            mExtension.setPadding(0, 0, 0, 0);
            mExtension.setOnKeyboardActionListener(
                    new ExtensionKeyboardListener(getOnKeyboardActionListener()));
            mExtension.setPopupParent(this);
            mExtension.setPopupOffset(0, -windowLocation[1]);
            mExtensionPopup.setContentView(mExtension);
            mExtensionPopup.setWidth(getWidth());
            mExtensionPopup.setHeight(keyboard.getHeight());
            mExtensionPopup.setAnimationStyle(-1);
            getLocationInWindow(windowLocation);
            // TODO: Fix the "- 30". 
            mExtension.setPopupOffset(0, -windowLocation[1] - 30);
            mExtensionPopup.showAtLocation(this, 0, 0, -keyboard.getHeight()
                    + windowLocation[1] + this.getPaddingTop());
        } else {
            mExtension.setVisibility(VISIBLE);
        }
        mExtension.setShiftState(getShiftState()); // propagate shift state
    }

    @Override
    public void closing() {
        super.closing();
        if (mExtensionPopup != null && mExtensionPopup.isShowing()) {
            mExtensionPopup.dismiss();
            mExtensionPopup = null;
        }
    }

    private void closeExtension() {
        mExtension.closing();
        mExtension.setVisibility(INVISIBLE);
        mExtensionVisible = false;
    }

    private static class ExtensionKeyboardListener implements OnKeyboardActionListener {
        private OnKeyboardActionListener mTarget;
        ExtensionKeyboardListener(OnKeyboardActionListener target) {
            mTarget = target;
        }
        public void onKey(int primaryCode, int[] keyCodes, int x, int y) {
            mTarget.onKey(primaryCode, keyCodes, x, y);
        }
        public void onPress(int primaryCode) {
            mTarget.onPress(primaryCode);
        }
        public void onRelease(int primaryCode) {
            mTarget.onRelease(primaryCode);
        }
        public void onText(CharSequence text) {
            mTarget.onText(text);
        }
        public void onCancel() {
            mTarget.onCancel();
        }
        public boolean swipeDown() {
            // Don't pass through
            return true;
        }
        public boolean swipeLeft() {
            // Don't pass through
            return true;
        }
        public boolean swipeRight() {
            // Don't pass through
            return true;
        }
        public boolean swipeUp() {
            // Don't pass through
            return true;
        }
    }

    /****************************  INSTRUMENTATION  *******************************/

    static final boolean DEBUG_AUTO_PLAY = false;
    static final boolean DEBUG_LINE = false;
    private static final int MSG_TOUCH_DOWN = 1;
    private static final int MSG_TOUCH_UP = 2;

    Handler mHandler2;

    private String mStringToPlay;
    private int mStringIndex;
    private boolean mDownDelivered;
    private Key[] mAsciiKeys = new Key[256];
    private boolean mPlaying;
    private int mLastX;
    private int mLastY;
    private Paint mPaint;

    private void setKeyboardLocal(Keyboard k) {
        if (DEBUG_AUTO_PLAY) {
            findKeys();
            if (mHandler2 == null) {
                mHandler2 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        removeMessages(MSG_TOUCH_DOWN);
                        removeMessages(MSG_TOUCH_UP);
                        if (mPlaying == false) return;

                        switch (msg.what) {
                            case MSG_TOUCH_DOWN:
                                if (mStringIndex >= mStringToPlay.length()) {
                                    mPlaying = false;
                                    return;
                                }
                                char c = mStringToPlay.charAt(mStringIndex);
                                while (c > 255 || mAsciiKeys[c] == null) {
                                    mStringIndex++;
                                    if (mStringIndex >= mStringToPlay.length()) {
                                        mPlaying = false;
                                        return;
                                    }
                                    c = mStringToPlay.charAt(mStringIndex);
                                }
                                int x = mAsciiKeys[c].x + 10;
                                int y = mAsciiKeys[c].y + 26;
                                MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(),
                                        SystemClock.uptimeMillis(),
                                        MotionEvent.ACTION_DOWN, x, y, 0);
                                LatinKeyboardView.this.dispatchTouchEvent(me);
                                me.recycle();
                                sendEmptyMessageDelayed(MSG_TOUCH_UP, 500); // Deliver up in 500ms if nothing else
                                // happens
                                mDownDelivered = true;
                                break;
                            case MSG_TOUCH_UP:
                                char cUp = mStringToPlay.charAt(mStringIndex);
                                int x2 = mAsciiKeys[cUp].x + 10;
                                int y2 = mAsciiKeys[cUp].y + 26;
                                mStringIndex++;

                                MotionEvent me2 = MotionEvent.obtain(SystemClock.uptimeMillis(),
                                        SystemClock.uptimeMillis(),
                                        MotionEvent.ACTION_UP, x2, y2, 0);
                                LatinKeyboardView.this.dispatchTouchEvent(me2);
                                me2.recycle();
                                sendEmptyMessageDelayed(MSG_TOUCH_DOWN, 500); // Deliver up in 500ms if nothing else
                                // happens
                                mDownDelivered = false;
                                break;
                        }
                    }
                };

            }
        }
    }

    private void findKeys() {
        List<Key> keys = getKeyboard().getKeys();
        // Get the keys on this keyboard
        for (int i = 0; i < keys.size(); i++) {
            int code = keys.get(i).codes[0];
            if (code >= 0 && code <= 255) {
                mAsciiKeys[code] = keys.get(i);
            }
        }
    }

    public void startPlaying(String s) {
        if (DEBUG_AUTO_PLAY) {
            if (s == null) return;
            mStringToPlay = s.toLowerCase();
            mPlaying = true;
            mDownDelivered = false;
            mStringIndex = 0;
            mHandler2.sendEmptyMessageDelayed(MSG_TOUCH_DOWN, 10);
        }
    }

    @Override
    public void draw(Canvas c) {
        LatinIMEUtil.GCUtils.getInstance().reset();
        boolean tryGC = true;
        for (int i = 0; i < LatinIMEUtil.GCUtils.GC_TRY_LOOP_MAX && tryGC; ++i) {
            try {
                super.draw(c);
                tryGC = false;
            } catch (OutOfMemoryError e) {
                tryGC = LatinIMEUtil.GCUtils.getInstance().tryGCOrWait("LatinKeyboardView", e);
            }
        }
        if (DEBUG_AUTO_PLAY) {
            if (mPlaying) {
                mHandler2.removeMessages(MSG_TOUCH_DOWN);
                mHandler2.removeMessages(MSG_TOUCH_UP);
                if (mDownDelivered) {
                    mHandler2.sendEmptyMessageDelayed(MSG_TOUCH_UP, 20);
                } else {
                    mHandler2.sendEmptyMessageDelayed(MSG_TOUCH_DOWN, 20);
                }
            }
        }
        if (LatinIME.sKeyboardSettings.showTouchPos || DEBUG_LINE) {
            if (mPaint == null) {
                mPaint = new Paint();
                mPaint.setColor(0x80FFFFFF);
                mPaint.setAntiAlias(false);
            }
            c.drawLine(mLastX, 0, mLastX, getHeight(), mPaint);
            c.drawLine(0, mLastY, getWidth(), mLastY, mPaint);
        }
    }
}
