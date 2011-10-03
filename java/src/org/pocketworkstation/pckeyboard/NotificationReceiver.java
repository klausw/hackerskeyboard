package org.pocketworkstation.pckeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class NotificationReceiver extends BroadcastReceiver {
	static final String TAG = "PCKeyboard/Notification";
    private LatinIME mIME;

	NotificationReceiver(LatinIME ime) {
	 	super();
    	mIME = ime;
		Log.i(TAG, "NotificationReceiver created, ime=" + mIME);
	}
	
    @Override
    public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "NotificationReceiver.onReceive called");

		InputMethodManager imm = (InputMethodManager)
        	context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInputFromInputMethod(mIME.mToken, InputMethodManager.SHOW_FORCED);
		}
	}
}
