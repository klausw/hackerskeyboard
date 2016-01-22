

# Release Notes #

This page summarizes the changes between the Hacker's Keyboard releases on
Android Market. For non-Market pre-release versions, see the
[Downloads](http://code.google.com/p/hackerskeyboard/downloads/list) page. The
code-level change descriptions are available in the
[Source](http://code.google.com/p/hackerskeyboard/source/list) area.

If you notice problems or regressions in a Market release, please contact me directly by email or file a bug. I don't get notified for problems reported in Market reviews, and I can't contact the comment author or respond to them, which is especially frustrating for cases where a simple configuration change would fix things.

## Version 1.37 ##

Major changes:

  * Switch to modern voice typing, remove RECORD\_AUDIO permission.
  * Disable contacts dictionary, remove READ\_CONTACTS permission.
  * Add Meta/Command/Windows key (❖ symbol), fixed modifier key handling.
  * Add Google Cloud settings backup support.

New settings:
  * Support choice of Shift Lock or Caps Lock, optionally apply Shift Lock to special keys. ([Issue 177](https://code.google.com/p/hackerskeyboard/issues/detail?id=177))

New layouts:

  * "hu" (Hungarian, Magyar) 4-row and 5-row layouts, qwertz + qwerty. Contributed by Bognár András.
  * "ta" (Tamil, தமிழ்) 5-row layout. Contributed by "Stars Soft".
  * "Español (Latinoamérica)" 5-row layout, contributed by Miguel Farah

For more details, please see the [prerelease notes](https://code.google.com/p/hackerskeyboard/downloads/detail?name=hackerskeyboard-v1034rc17.apk).

## Version 1.33 ##

  * Support for devices with MIPS and x86 CPUs.
  * bugfixes for shift handling and suggestion underlining.

New layouts:

  * Persian (فارسی, fa) layouts contributed by امیرمسعود پورموسی
  * Armenian (Հայերեն, hy) keymap contributed by Serop Chamourlian
  * Russian phonetic layout (ru-rPH), contributed by Sergei (zc2).

## Version 1.31 ##

This is primarily a bugfix release, no notable new features.

### Layout modifications ###
  * update Portugese 5-row altchars, [issue 191](https://code.google.com/p/hackerskeyboard/issues/detail?id=191)
  * add Latvian alt chars, [issue 176](https://code.google.com/p/hackerskeyboard/issues/detail?id=176)
  * Add missing ľ and ĺ altchars for Slovak (SK)
  * Remove unwanted letter hints in Hebrew and other non-latin keyboards, [issue 159](https://code.google.com/p/hackerskeyboard/issues/detail?id=159)

### Other changes ###
  * disable auto space after completion for Thai
  * New options for slide key events, see [issue 167](https://code.google.com/p/hackerskeyboard/issues/detail?id=167)
  * make swipe gesture easier to trigger

### Bugfixes ###
  * fix Ctrl modifier that was getting stuck, [issue 165](https://code.google.com/p/hackerskeyboard/issues/detail?id=165)
  * fix landscape keyboard layout preference, [issue 175](https://code.google.com/p/hackerskeyboard/issues/detail?id=175)
  * fix suggestion preference handling, [issue 182](https://code.google.com/p/hackerskeyboard/issues/detail?id=182)
  * disable "slide key hack" for popup keyboards, [issue 187](https://code.google.com/p/hackerskeyboard/issues/detail?id=187)
  * Workarounds for null pointer exceptions in UserDictionary and isConnectbot()

## Version 1.29 ##

### Incompatible changes ###

  * The keyboard height setting was inconsistent in earlier versions, this is now fixed but may result in a diffent size than you're used to. Please adjust the setting if necessary.
  * The keyboard now uses "caps lock", not "shift lock", and you'll see digits instead of punctuation in the 5th row when auto-capitalization is on. If you strongly prefer the old behavior, let me know and I'll add "shift lock" back as an option.
  * Reorganized "Settings" menu. All the old settings should still be there, but many have moved to submenus.

### New features ###

  * New layouts:
    * Turkish (Türkçe, tr) 4-row and 5-row.
    * Italian (Italiano, it) 5-row.
    * Serbian (Српски, sr) layout updated, including new 5-row contributed by fbrcic.
    * Thai (ไทย, th) updates contributed by hmmbug, and fixed accent handling.

  * Basic theming support, including an Ice Cream Sandwich inspired theme which is the new default.

  * Added support for the Ctrl key in Android applications other than ConnectBot. On Android 3.0 (Honeycomb) and newer, you can now use Ctrl-A/X/C/V for cut&paste in compatible applications, including most standard text editing widgets. Use Shift+arrows to select text. Note that some applications don't handle this well, avoid using Ctrl key combinations if they misbehave.

  * The new AutoCaps/Caps Lock handling should make it easier to use the French 5-row AZERTY and similar layouts, you now get uppercase accented characters on the 5th row instead of digits. Use the Shift key to get the normal shifted digits or punctuation.

  * More efficient keyboard redrawing, this should save memory and reduce lag compared to previous versions.

  * New configuration options:
    * Adjust font size for suggestions.
    * Adjust long-press duration for pop-up mini keyboards.
    * Modify the pop-up mini keyboard content to add/exclude shifted or uppercase characters. Or disable the popups and use auto-repeat for all keys instead.
    * Change the punctuation list shown in the suggestion area.
    * Adjust the height of the 5th row, use this if you prefer smaller number keys to get more space for the other keys.
    * Selectable screen drawing method for Android 3.0 (Honeycomb) and newer.

### Bugfixes ###

  * Completion of words beginning with a capital letter was not working correctly in 5-row mode.
  * Fix Hebrew layouts on Samsung Galaxy S2 ([issue 122](https://code.google.com/p/hackerskeyboard/issues/detail?id=122))
  * Add VX ConnectBot support for the compatibility hack ([issue 154](https://code.google.com/p/hackerskeyboard/issues/detail?id=154))
  * New "Sliding key events" option as a workaround for [issue 53](https://code.google.com/p/hackerskeyboard/issues/detail?id=53) "Keyboard drops characters if I type too fast"


### Experimental features ###

The following added features are still experimental and subject to change in
future releases. If they don't work, please let me know, but I'd appreciate it
if you'd just pretend these don't exist when writing Market reviews - if I get
poor reviews due to experimental features not fully working, I'll probably
disable them in the future for official releases instead of keeping them as
experiments.

  * Gesture action support, by default "swipe down" closes the keyboard. The swipe detection isn't very consistent at this point, this needs more tuning. Alternatively, you can bind actions to the Volume Up/Down hardware buttons which is more reliable. These actions are only triggered when the keyboard is visible, so it shouldn't interfere too much with normal volume key use.

  * New compact 5-row layout, US QWERTY only at this point. It's less cramped than the full 5-row layout on phones in portrait mode, but still adds a number row and cursor keys. It's a bit hidden since it's not well integrated at this point, and this will definitely be different in future versions. To activate it:
    * In "Theme and label settings", check "Enable compact 5-row keyboard mode"
    * Exit and re-enter keyboard settings (known bug, the new mode isn't visible unless you do this)
    * In "Keyboard view settings", change the "Keyboard mode" to "5-row compact layout" for portrait and/or landscape.
    * Screenshot, with number row scaling set to 80%: ![http://hackerskeyboard.googlecode.com/files/hk-5row-compact-s.png](http://hackerskeyboard.googlecode.com/files/hk-5row-compact-s.png)

  * Extension keyboards for some layouts, for example adding numbers+punctuation in 4-row mode and F1-F12 in 5-row mode. Toggle the extension with a gesture action, "swipe up" by default.