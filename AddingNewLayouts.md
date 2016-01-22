# Adding new keyboard layouts #

_**Warning:** I'm currently in the middle of a refactoring that will replace the current complex XML layout definitions with simple text files. If you're thinking about starting work on a layout based on the instructions below, you may want to wait for the new system - see [issue 13](https://code.google.com/p/hackerskeyboard/issues/detail?id=13)._

It's very difficult for me to create and test keyboard layouts for languages I can't read or write. Help in improving support for additional languages would be very much appreciated.

Please let me know if you can contribute a keyboard layout for a not-yet-supported language. Feel free to contact me before starting work on it to avoid duplicated effort.



## Limitations ##

  * It treats "shift" as equivalent to "caps lock" (some layouts expect different mappings for these two modes).
  * It doesn't directly support an AltGr key for direct access to alternate keys, instead it provides additional characters via long-pressing the corresponding key which displays a pop-up mini keyboard with alternatives. Alternatively, the keyboard supports dead keys which can be used to produce accented characters.
  * There's no support for languages needing more glyphs than can be fit reasonably on a keyboard, such as Chinese. I don't plan to add complex input methods, that's beyond the scope of this project.

## Overview ##

There are three parts needed for fully supporting a new layout:
  * a 4-row layout, used on phones in portrait mode
  * "alternates" definitions used to populate the long-press popup mini-keyboard contents
  * a 5-row layout, used for tablets, or in landscape mode

The 4-row keyboards each have a `kbd_qwerty.xml` file defining the key configuration and characters, for example [kbd\_qwerty.xml](http://code.google.com/p/hackerskeyboard/source/browse/java/res/xml-de/kbd_qwerty.xml) for the German QWERTZ layout. If a language doesn't currently have a 4-row layout, that would need to be adapted and added.

The per-letter alternate characters shown in long-press popups are based on a language-specific `donottranslate-altchars.xml` file. This includes the digits shown in the top row of the 4-row keyboard.

The 5-row keyboard layouts are configured separately from the standard Gingerbread 4-row keyboards.

The core 5-row layout is defined in the [kbd\_full.xml](http://code.google.com/p/hackerskeyboard/source/browse/java/res/xml/kbd_full.xml) file, it defines key sizes and positions but not the characters shown on each key, so it should not need to be modified by language. The character mappings are defined per language in the `donottranslate-keymap.xml` file, assigning characters for each key position.

Examples for the language-specific mappings:
  * English QWERTY [4-row layout](http://code.google.com/p/hackerskeyboard/source/browse/java/res/xml/kbd_qwerty.xml), [5-row keymap](http://code.google.com/p/hackerskeyboard/source/browse/java/res/values/donottranslate-keymap.xml) and [altchars](http://code.google.com/p/hackerskeyboard/source/browse/java/res/values/donottranslate-altchars.xml)
  * German QWERTZ [4-row layout](http://code.google.com/p/hackerskeyboard/source/browse/java/res/xml-de/kbd_qwerty.xml), [keymap](http://code.google.com/p/hackerskeyboard/source/browse/java/res/values-de/donottranslate-keymap.xml) and [altchars](http://code.google.com/p/hackerskeyboard/source/browse/java/res/values-de/donottranslate-altchars.xml)
  * Russian [4-row layout](http://code.google.com/p/hackerskeyboard/source/browse/java/res/xml-ru/kbd_qwerty.xml), [keymap](http://code.google.com/p/hackerskeyboard/source/browse/java/res/values-ru/donottranslate-keymap.xml)

## Editing the keymap.xml file ##

To create a 5-row keyboard layout, assign the main, shifted, and alternate characters for each key location. You can define the alternate characters directly in the file, or (recommended for Latin alphabet languages) indirectly via the altchars file.

Example for a key definition:
```
    <string name="key_ae04_main">4</string>
    <string name="key_ae04_shift">$</string>
    <string name="key_ae04_alt">£€¥</string>
```

The result is as follows:
```
    Main map:  "4", alternates "$£#¥"
    Shift map: "$", alternates "4£#¥"
```

Key "ae04" is the 4th key from the left in the 5th row ("e") of a typical PC layout.

Here's an example for a letter with indirectly specified alternates:

```
    <string name="key_ad03_main">e</string>
    <string name="key_ad03_shift">E</string>
    <string name="key_ad03_alt">@string/alternates_for_e</string>
```

The `alternates_for_e` string reference uses this entry in `donottranslate-altchars.xml`:
```
    <string name="alternates_for_e">3éèêëē€</string>
```

The first alternate character gets drawn as a hint on the key if that's configured in settings. List the remaining alternate characters (if any) in order from most frequent to least frequent. They will be reversed and drawn right to left on keys in the right half of the keyboard. (This is different from the original Gingerbread AOSP behavior.)

Note that most of the current layout definition files repeat the main and shift characters as the first two alt characters, this is no longer needed in current versions since it now does this automatically based on a config option. Redundant characters get removed automatically when initializing the layout, including removing digits from alternates for 5-row layouts which have a separate number row.

For letters, you can use @string/alternates\_for\_X to refer to predefined lists from values/donottranslate-altchars.xml or your localized values-ZZ/donottranslate-altchars.xml as appropriate. This is optional, if it's a non-Latin layout it's easier to just include the alternates directly for each key.

Use backslash escapes and XML entities as follows:
```
     &  ->  &amp;
     <  ->  &lt;
     >  ->  &gt;
     \  ->  \\
     @  ->  \@
     ?  ->  \?
     '  ->  \'
     "  ->  \"
```

## Dead keys and diacritical marks (accents) ##

You can add dead keys intended for accents or other diacritics by using Unicode "Combining Diacritical Marks", for example:

```
    <string name="key_ac10_main">&#x301;</string>
    <string name="key_ac10_shift">&#x308;</string>
    <string name="key_ac10_alt">&#x313;</string>
```

See http://unicode.org/charts/PDF/U0300.pdf for a list. Note that the system's fonts and glyph composition engine may not support all desired combinations, the combination may result in square placeholders instead of accented characters.

By default, the dead keys modify the **following** character. If the appropriate behavior would be to modify the **previous** character instead, this would need to be listed as an exception in the code, see for example revision 06a7a2378fd9 for Thai.