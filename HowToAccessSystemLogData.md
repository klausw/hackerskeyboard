# Introduction #

This page is intended for users who have encountered a crash bug that I can't reproduce, where I need log information from the device it happens on to figure out what's going on. See the [FAQ entry](http://code.google.com/p/hackerskeyboard/wiki/FrequentlyAskedQuestions#It_crashes._Please_fix.) for more detail.

# Getting log information from the phone #

If you want to help fix the bug and are willing to dig up the system logs, you can try the following methods:

**Option 1:** Use third-party Android applications such as the free [aLogcat](https://market.android.com/details?id=org.jtb.alogcat) tool to save and copy logs from your phone.

**Option 2:** You can create a bugreport on the phone:
  * Turn on USB debugging temporarily (you don't need to actually connect anything to the USB port, this just enables bugreports): _Home_ > _Settings_ > _Applications_ > _Development_ > _USB debugging_
  * Press the magic button combination to trigger a bug report. This varies by device, try VolumeDown+Power simultaneously or similar combinations. (I haven't found a list of these, let me know if you know one.) If it worked, it'll vibrate when starting, and then again 10s or so later when done. Starting with Android 4.1.2, the _Development_ menu has a "take bug report" menu entry which is easier.
  * The bugreport is created on the sd card in the /bugreports/ folder. Starting with Ice Cream Sandwich, it'll launch the email app or create a notification when done, I recommend mailing it to yourself to take a look at the content before sending it.
  * Turn off USB debugging again. (It's insecure to leave it on permanently on pre-4.2 devices.)

**Option 3:** If you have the [Android SDK](http://developer.android.com/sdk/installing.html) and your system is already set up for USB debugging, run "[adb logcat](http://developer.android.com/guide/developing/tools/adb.html#logcat)" on your PC to view recent log output. This requires "USB debugging" to be enabled on your phone, see the previous entry for details.

In either case, please review and if necessary edit your logs to make sure they don't contain sensitive information from other applications before sending them to me.