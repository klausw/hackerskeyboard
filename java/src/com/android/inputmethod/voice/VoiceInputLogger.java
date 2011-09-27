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

package com.android.inputmethod.voice;

import android.content.Context;

/**
 * Provides the logging facility for voice input events. This fires broadcasts back to
 * the voice search app which then logs on our behalf.
 *
 * Note that debug console logging does not occur in this class. If you want to
 * see console output of these logging events, there is a boolean switch to turn
 * on on the VoiceSearch side.
 */
public class VoiceInputLogger {
    private static VoiceInputLogger sVoiceInputLogger;

    // The base intent used to form all broadcast intents to the logger
    // in VoiceSearch.

    // This flag is used to indicate when there are voice events that
    // need to be flushed.

    /**
     * Returns the singleton of the logger.
     *
     * @param contextHint a hint context used when creating the logger instance.
     * Ignored if the singleton instance already exists.
     */
    public static synchronized VoiceInputLogger getLogger(Context contextHint) {
        if (sVoiceInputLogger == null) {
            sVoiceInputLogger = new VoiceInputLogger(contextHint);
        }
        return sVoiceInputLogger;
    }

    public VoiceInputLogger(Context context) {        
   }
    
    public void flush() {
    }
    
    public void keyboardWarningDialogShown() {
    }
    
    public void keyboardWarningDialogDismissed() {
    }

    public void keyboardWarningDialogOk() {
    }

    public void keyboardWarningDialogCancel() {
    }

    public void settingsWarningDialogShown() {
    }
    
    public void settingsWarningDialogDismissed() {
    }

    public void settingsWarningDialogOk() {
    }

    public void settingsWarningDialogCancel() {
    }
    
    public void swipeHintDisplayed() {
    }
    
    public void cancelDuringListening() {
    }

    public void cancelDuringWorking() {
    }

    public void cancelDuringError() {
    }
    
    public void punctuationHintDisplayed() {
    }
    
    public void error(int code) {
    }

    public void start(String locale, boolean swipe) {
    }
    
    public void voiceInputDelivered(int length) {
    }

    public void textModifiedByTypingInsertion(int length) {
    }

    public void textModifiedByTypingInsertionPunctuation(int length) {
    }

    public void textModifiedByTypingDeletion(int length) {
    }

    public void textModifiedByChooseSuggestion(int suggestionLength, int replacedPhraseLength,
                                               int index, String before, String after) {
    }

    public void inputEnded() {
    }
    
    public void voiceInputSettingEnabled() {
    }
    
    public void voiceInputSettingDisabled() {
    }

}
