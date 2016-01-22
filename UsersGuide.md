# User's Guide #

Please see the FrequentlyAskedQuestions page for additional details, including hints, tips, and known issues.

## Enabling the keyboard ##

_Hacker's Keyboard_ provides a new Android input method. It is disabled by default, and for security reasons Android doesn't allow programs to change input method settings. It will display a help screen when you first install it, please follow the following steps to activate it:

  1. Add the keyboard to the system's input method list:
    1. Open the system input settings configuration by pressing the **Configure input methods** button
    1. Find _Hacker's Keyboard_ in the input method list, tap the checkmark to switch it on.
    1. Acknowledge the warning shown by the system. The system always shows this message when enabling a third-party input method, see FrequentlyAskedQuestions.
    1. Press the _Back_ button to return to the previous screen
  1. Choose the currently active input method:
    1. Press the **Set current input method** button.
    1. Select <i>Hacker's Keyboard</i> from the list.

## Keyboard layout ##

The keyboard mostly works like the standard Android  keyboard, including multitouch support. You can get a shifted character in two ways:
  * tap shift and let go, tap the character.
  * press and hold shift, tap the character, let go of shift.

This works the same way for the Ctrl and Alt modifiers. You can lock shift mode by tapping it twice, indicated by a glowing green dot on the left shift key. The other modifiers don't lock, they only affect the next key. You can cancel a modifier by pressing the modifier key again.

Long-pressing keys shows a popup mini-keyboard including shifted keys and (for some keys) additional special characters.

Modern Android versions (3.0/Honeycomb or newer) support selecting text with shift + arrow keys, and cut&paste with Ctrl-X/C/V.

The Alt and Meta/Command/Windows (❖) keys are mainly intended for use with terminal emulators or remote desktops. Alt + arrow keys should work as start/end of line or document in most applications.

The "Fn" key shows an alternate keymap with additional special keys including Home, Page up/down, F1-F12, and a number block. Note that many Android applications aren't programmed to handle these keys, so they may be ignored.

The arrow keys in the bottom right corner generate directional pad (trackball) key events.

The "◯" (circle) key next to the arrows corresponds to pressing the directional pad center button. Long-pressing "◯" acts as a "compose" key, for example the sequence "◯" "+" "-" produces "±". See the [source](https://code.google.com/p/hackerskeyboard/source/browse/java/src/org/pocketworkstation/pckeyboard/ComposeSequence.java) for a list of supported combinations.

## Configuration options ##

In addition to the standard Gingerbread configuration options, Hacker's Keyboard adds the following settings:

**Keyboard height** - two settings, separately for portrait and landscape mode, for the overall keyboard height as a percentage of screen size. Change these settings if you prefer larger or smaller keys.

**Full keyboard in portrait mode** - If this setting is off, use the original Gingerbread keyboard layout (4 rows, 10 across) in portrait mode. This is intended for phones since the keys get unreasonably small in full mode. It is off by default for phones, on for tablets.

**Show suggestions in landscape mode** - Turn this setting off if you want to hide the suggestions bar above the keyboard in landscape mode. The overall suggestions behavior is controlled by the Gingerbread settings below.

**Always use standard view in landscape mode** - By default, Android uses a special mini-editor when editing text fields in landscape mode that shows only one field at a time. You can change this setting to keep the standard view active in landscape mode instead. Doing so can cause compatibility issues with some applications that can't cope with the small screen height.

**Key hint label visibility** - Controls the additional key labels shown on keys to indicate shift/long-press additional characters.

**Tab key and Ctrl-I send ConnectBot-compatible Tab** - This is a hack to support the current Market versions of ConnectBot that don't handle the Tab key correctly. You shouldn't need to change this, but it's configurable in case future ConnectBot version behave differently.

## Credits and Acknowledgements ##

Based on the LatinIME package from the Gingerbread AOSP distribution: http://android.git.kernel.org/?p=platform/packages/inputmethods/LatinIME.git

Includes the Cyanogen "en" dictionary as distributed under the Apache 2.0 licence here: https://gist.github.com/CyanogenMod/android_vendor_cyanogen/tree/gingerbread/overlay/common/packages/inputmethods/LatinIME/java/res

Arabic 4-row keyboard based on the AnySoftKeyboard language pack as distributed under the Apache 2.0 license here: http://code.google.com/p/softkeyboard/source/browse/trunk/LanguagePacks/Arabic/