

# Security and privacy #

## Why is there a security warning when I activate it? ##

This warning is a general Android system security feature that alerts users when activating a third-party input method. It's shown for all user-installed keyboards as a precaution. The warning does not mean that this specific keyboard is known to be stealing data, just that it **could** potentially do so.

A keyboard knows which keys you are currently typing (that's part of its job description), and a malicious application could abuse that, especially if it were to ask for Internet access permission.

The source code for this project is available at the [Source](http://code.google.com/p/hackerskeyboard/source/checkout) link above, you are welcome to review it and/or build your own binary.

In case you're curious about the technical details related to showing this security warning, see the Android OS source code, specifically _ime\_security\_warning_ in the official Android source [LanguageSettings.java](https://android.googlesource.com/platform/packages/apps/Settings/+/935b504a05f328cf90db3415b82ff4a7f51e628f/src/com/android/settings/LanguageSettings.java) and [message catalog](https://android.googlesource.com/platform/packages/apps/Settings/+/935b504a05f328cf90db3415b82ff4a7f51e628f/res/values/strings.xml).

## Where's the source code? ##

See "Source" link above: http://code.google.com/p/hackerskeyboard/source/browse/

See BuildingFromSource for instructions how to build it yourself.

# General questions #

## I'm still seeing the system keyboard. How do I activate it? ##

The recommended method is to launch the "Hacker's Keyboard" application and use the two buttons shown by the application to enable it. The help text shown below the buttons explains this in more detail.

If using a phone, turn the phone sideways (landscape mode) to see the full layout. By default, small devices use a simple layout in portrait mode since the full 5-row layout would be too crowded, but you can change that in options if you prefer.

For manual activation, use these steps:

  * Go to Home / Menu / Settings / Language & Keyboard
  * Scroll down to "Hacker's Keyboard", activate the check mark, and acknowledge the system warning shown for third-party input methods. (See [this FAQ entry](http://code.google.com/p/hackerskeyboard/w/edit.do#Why_is_there_a_security_warning_when_I_activate_it?) for more information about the warning.)
  * Exit settings and open a text entry box, for example the Search bar.
  * Long-press the input field, and select the _Input method_ menu entry.
  * Select "Hacker's Keyboard" from the list.

See also the following entry that explains how to activate specific layouts.

## How do I activate Dvorak / Arabic / ... layouts? ##

  * go to Hacker's Keyboard's settings menu (the "gear" symbol at the bottom left)

  * go to "Input languages" setting

  * add a checkmark to the languages you want to use. Dvorak is "English (en\_DV)".

  * swipe the space bar horizontally to switch languages

## Can I use it with my Nook Color / Kindle Fire? ##

These devices use a heavily modified operating system that isn't fully compatible with standard Android versions. I consider these as unsupported, but users have been successful in activating third-party keyboards.

The application [NookColor Tools](http://forum.xda-developers.com/showthread.php?t=868366) allegedly works for both the Nook Color and Kindle Fire. See [this xda developers thread](http://forum.xda-developers.com/showthread.php?t=1364543) for Kindle Fire specific information. I have not tried this myself, please be careful with steps involving rooting or OS modification.

## How do I hide the keyboard? ##

On standard Android devices, pressing the "Back" button while the keyboard is visible will close it instead of doing the usual "Back" action.

If this is not working for you, or if you have a nonstandard device that doesn't have a Back key, try a vertical "swipe down" gesture (across multiple keyboard rows) to close it.

You can configure the gesture used for this in the keyboard's "Gesture and key actions" settings menu. If the swipe action is too hard to trigger, try binding the "close keyboard" action to the "Volume down" hardware key. This won't interfere with normal use of the volume button while the keyboard is closed.

For the future, I do plan to add support for a dedicated "close keyboard" key, but this needs to be part of the general user-customizable layout support ([issue 13](https://code.google.com/p/hackerskeyboard/issues/detail?id=13)) - there just isn't enough room in the current layouts to add this by default for everyone.

## Where can I get a completion dictionary for my language? ##

Several languages are available as add-on dictionary packages on the Play Store: https://play.google.com/store/apps/developer?id=Klaus+Weidner

Additional dictionary packages are on the project download page: https://code.google.com/p/hackerskeyboard/downloads/list?q=label%3ADictionary

Hacker's Keyboard is also able to use dictionaries from AnySoftKeyboard language packs, including [Arabic](https://play.google.com/store/apps/details?id=com.anysoftkeyboard.languagepack.arabic) and [Greek](https://play.google.com/store/apps/details?id=com.anysoftkeyboard.languagepack.greek): https://play.google.com/store/search?q=anysoftkeyboard+language&c=apps

The dictionary should be available for use immediately after installing the package. The "Input language selection" setting menu shows "Dictionary available" for each languages where it found a usable dictionary.


## Where is the "|" (pipe) or "@" key? ##

Many non-English layouts have more than two characters assigned to each key, typically accessed by using the AltGr key. On this keyboard, the additional characters are accessible by long-pressing keys in the usual locations. For example, on a German keyboard, long-press "<" to show a pop-up mini keyboard which includes "|". On a French AZERTY keyboard, long-press "à" or "é" to get "@".

I'm planning to add support for an AltGr key in a future version.

## What does the Circle key do? ##

The Circle key to the left of the arrow keys corresponds to pushing a trackball to make a menu selection, or pressing the middle of a directional keypad. The corresponding Android keycode is DPAD\_CENTER. This is usually equivalent to pressing RETURN, but some applications treat it differently. For example, when navigating a dropdown suggestion list such as the browser's address bar, you can use it to copy the current selection for further editing instead of immediately selecting it.

As a secondary function, you can long-press the Circle key to activate a "compose" function, for example Compose (long-press Circle) followed by the "o" and "c" keys generates the "©" Copyright symbol. See the [source file](http://code.google.com/p/hackerskeyboard/source/browse/java/src/org/pocketworkstation/pckeyboard/ComposeSequence.java) for the list of supported combinations.

# Bugs and known issues #

## Unwanted spaces before punctuation ##

Version 1.29 changed the way that the automatic punctuation swap works. Earlier versions were quite aggressive about automatically removing spaces before punctuation, but this could lead to problems when you actually want a space character.

The new version tries to make this more consistent. Short version: if you want the old behavior, turn on the "Auto-Complete" setting, or tap the suggested word instead of pressing the space bar.

  * If the "Auto-complete" setting is off, the space bar inserts a "hard space", and this doesn't get removed when adding punctuation from the suggestions list. If you want to accept a suggestion, tap the suggested word in the suggestion bar. Then it'll insert it with a "soft space", and you'll get the punctuation behavior you expect.

  * If "Auto-complete" is on, the spacebar accepts the currently highlighted suggestion and adds it with a "soft space", and punctuation typed after that will appear next to the word without a space.

The reason for the change was that the old behavior made it very difficult to type punctuation consistently, especially after adding ":" and ";" to the list of characters that should be space-swapped. The keyboard needs to be able to distinguish "Test: " from "Test :-)", and this was difficult in the old system.

If this isn't working for you as described here, please let me know.

## "Touch to correct words" inserts the correction instead of replacing ##

See [issue 17](https://code.google.com/p/hackerskeyboard/issues/detail?id=17), this feature depends on [operating system support](http://developer.android.com/reference/android/view/inputmethod/InputConnection.html#setComposingRegion(int,%20int)) that's only available starting with Gingerbread (v2.3). In the meantime I recommend disabling the option in the keyboard settings if it's not working correctly.

## It doesn't work right after upgrading ##

There's a known problem if the current input method gets replaced while it's active, the system can get confused. The symptom is that the keyboard appears unresponsive, or only shows the typed output after switching screen orientation. Switching applications should fix it, or alternatively try briefly switching to a different input method and back.

The log files show "IME died" and "android.os.DeadObjectException" when this happens. This is outside my code, as far as I know the system should restart the IME but this sometimes doesn't appear to happen.

## The keys are too small. ##

This keyboard layout was originally designed for tablets, so it's unavoidable that the keys will be rather small on phones. By default it'll revert to the standard 4-row (10 keys across) Gingerbread layout on phones when in portrait mode. You can change that in settings but I don't recommend it.

Note that the key height is adjustable, you can configure the overall keyboard height as a percentage of screen size in settings, with two separate settings for portrait and landscape mode.

There's also a separate config setting for the key label sizes, this is useful if the key symbols are too small or too big due to your device's pixel density settings.

## Some keys don't work, for example Ctrl-C/Ctrl-V for cut&paste. ##

Recent Android versions (starting from Honeycomb / 3.0) do include support for the Ctrl-X/C/V/A shortcuts in EditText widgets and other common dialogs, and this generally works with Hacker's Keyboard.

Support for the extra keys varies by application. The keyboard just sends key events, and the application receiving the key events is responsible for acting on them, and typically applications ignore unknown or unexpected key events, or (in unfortunate cases) misinterpret the keystrokes to launch menu shortcuts or similar.

If possible, try attaching a USB or Bluetooth keyboard to your Android device to see if the corresponding keys work with a real hardware keyboard. If that also doesn't work, it'll most likely require a change to the application before a soft keyboard such as this one can support them. If it works with a real keyboard but not with the Hacker's Keyboard application, please [file a bug](http://code.google.com/p/hackerskeyboard/issues/list).

Developers: see KeyboardSupportInApplications for more information.

## It crashes. Please fix. ##

You can send a crash report through the Android crash reporting tool, this will give me some basic anonymized data about the crash location and an indication how often a problem occurs, but isn't always sufficient to figure out what's going on. If possible, please include details how to reproduce it, and information about your phone and Android version, those aren't included in the automatic report. If you want me to respond, please make sure to include your email address in the text.

It's easiest for me to handle if you [file a bug](http://code.google.com/p/hackerskeyboard/issues/list), that helps avoid losing track of things and lets me easily send progress updates. You can also just send me email if you don't want to use the issue tracker.

For crashes and similar issues, I'll most likely need system log information to figure out what's going on if I can't reproduce the bug.
See HowToAccessSystemLogData for more information how to get this data from your phone.

# Feature requests #

## Please add swipe / glide / gesture typing ##

(Note that "Swype" is a trademark referring to Nuance Communication's keyboard application which implements this input style.)

I do not intend to support this. It would be a huge change since the Google Keyboard has diverged a lot since the time I forked the project, and backporting the new logic would pretty much be equivalent to a full rewrite.

Also, Hacker's Keyboard currently supports swipe gestures to trigger actions, and this would conflict with using gestures to write words.

Personally, I switch between keyboards fairly frequently, using the standard Android keyboard for plain text entry and Hacker's Keyboard for more complex input.

## Can I use a different keyboard app (input method) in portrait mode? ##

Unfortunately, this isn't generally possible. Hacker's Keyboard could give up control and activate a different input method in portrait mode, but this would be irreversible. The user would need to re-activate Hacker's Keyboard manually to use it again, or the other keyboard would need to cooperate by implementing its own logic to switch keyboards back.

The reason for this is that the Android operating system enforces security restrictions for input methods that are intended to prevent malicious applications from intercepting user input. See the "Security" section in the [InputMethodManager](http://developer.android.com/reference/android/view/inputmethod/InputMethodManager.html) documentation for details.

Keyboard switching would be possible with modifications at the operating system level, for example on a phone running custom firmware, but that's beyond the scope of this application.

Some users have reported success using the application "[Keyboard Manager](https://market.android.com/details?id=com.ne0fhykLabs.android.utility.kmLauncher)" which requires a rooted phone. I haven't tried this myself.

## Can you add support for _Chinese/Japanese/Korean/(other specific language)_ layouts? ##

The current code is strongly oriented to alphabetic languages and is based on a fairly simple mapping from keypresses to output characters. Ideographic languages with complex input methods would need very different approaches, and I do not plan to support Chinese/Japanese/Korean and similar languages due to their complexity and lack of language knowledge on my part. Note that you can easily keep multiple keyboards installed and manually switch between them, for example using Hacker's Keyboard just for SSH while using the system keyboard for other applications.

For other languages, AddingNewLayouts wiki page has more details.

# Specific application notes #

## ConnectBot doesn't work in landscape mode ##

The ConnectBot application disables landscape mode by default on phones. Go to its main menu settings and set "Rotation mode" to "Automatic" to enable it.

## Android VNC Viewer doesn't recognize the extra keys ##

I've proposed a patch for that project to add support, at this time it has not been applied yet so you'd need to build your own modified VNC viewer binary: http://code.google.com/p/android-vnc-viewer/issues/detail?id=238

# Obsolete questions #

The information in this section is no longer applicable for current versions of the keyboard. I'm temporarily keeping the information in this file so that outside links to this page continue working.

## Why does it ask for "record audio" and "read contacts" permission? ##

Current Play Store releases (>= 1.37) no longer use these permissions.

Obsolete text following:
> The permissions requested by the application are those needed by the underlying Gingerbread keyboard, see the https://android.googlesource.com/platform/packages/inputmethods/LatinIME/+/979f8690967ff5409fe18f5085858ccdb8e0ccf1/java/AndroidManifest.xml AndroidManifest.xml] file from the original AOSP source code.

> It uses the contacts information for completion of names and email addresses, and audio recording for the voice input feature.

> I'm considering making a minimal-permissions version with this code disabled if there's sufficient demand, see http://code.google.com/p/hackerskeyboard/issues/detail?id=2 for more details.

## Why does it ask for "call log" permission? ##

Current versions (1.37) should no longer be showing this spurious permission request.

Obsolete text following:
> This is a temporary side effect of a recent change in Android permissions that appears on 4.1 (Jellybean) devices. I'll need to build and publish a new version to fix this, if you want this earlier you can download one of the release candidates from https://code.google.com/p/hackerskeyboard/downloads/list which includes a fix for this.

> The application is not actively asking for the READ\_CALL\_LOG permission. The equivalent rights were granted automatically on older Android versions when asking for READ\_CONTACTS, and recent versions split this up into two separate permissions. To avoid breaking backwards compatibility, apps requesting READ\_CONTACTS are automatically treated as also asking for READ\_CALL\_LOG unless they explicitly declare that they are aware of this distinction by setting the API level. I'll need to rebuild it with a current SDK to remove this permission, thanks for pointing that out.

> More info here: http://developer.android.com/reference/android/Manifest.permission.html#READ_CALL_LOG, note that the permission was added for API level 16 (Android 4.1 Jelly Bean), and the current build isn't yet targeting that level.