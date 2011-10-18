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

import java.text.Normalizer;

import android.util.Log;

public class DeadAccentSequence extends ComposeBase {
    private static final String TAG = "HK/DeadAccent";

    public DeadAccentSequence(ComposeSequencing user) {
        init(user);
    }
    
    private static void putAccent(String nonSpacing, String spacing, String ascii) {
        if (ascii == null) ascii = spacing;
        put("" + nonSpacing + " ", ascii);
        put(nonSpacing + nonSpacing, spacing);
        put(Keyboard.DEAD_KEY_PLACEHOLDER + nonSpacing, spacing);
    }
    
    public static String getSpacing(char nonSpacing) {
        String spacing = get("" + Keyboard.DEAD_KEY_PLACEHOLDER + nonSpacing);
        if (spacing == null) spacing = DeadAccentSequence.normalize(" " + nonSpacing);
        if (spacing == null) return "" + nonSpacing;
        return spacing;
    }
    
    static {
        // space + combining diacritical
        // cf. http://unicode.org/charts/PDF/U0300.pdf
        putAccent("\u0300", "\u02cb", "`");  // grave
        putAccent("\u0301", "\u02ca", "´");  // acute
        putAccent("\u0302", "\u02c6", "^");  // circumflex
        putAccent("\u0303", "\u02dc", "~");  // small tilde
        putAccent("\u0304", "\u02c9", "¯");  // macron
        putAccent("\u0305", "\u00af", "¯");  // overline
        putAccent("\u0306", "\u02d8", null);  // breve
        putAccent("\u0307", "\u02d9", null);  // dot above
        putAccent("\u0308", "\u00a8", "¨");  // diaeresis
        putAccent("\u0309", "\u02c0", null);  // hook above
        putAccent("\u030a", "\u02da", "°");  // ring above
        putAccent("\u030b", "\u02dd", "\"");  // double acute 
        putAccent("\u030c", "\u02c7", null);  // caron
        putAccent("\u030d", "\u02c8", null);  // vertical line above
        putAccent("\u030e", "\"", "\"");  // double vertical line above
        putAccent("\u0313", "\u02bc", null);  // comma above
        putAccent("\u0314", "\u02bd", null);  // reversed comma above
/*
// include?
        put("̃ ", "~");
        put("̃̃", "~");
        put("́ ", "'");
        put("́́", "´");
        put("̀ ", "`");
        put("̀̀", "`");
        put("̂ ", "^");
        put("̂̂", "^");
        put("̊ ", "°");
        put("̊̊", "°");
        put("̄ ", "¯");
        put("̄̄", "¯");
        put("̆ ", "˘");
        put("̆̆", "˘");
        put("̇ ", "˙");
        put("̇̇", "˙");
        put("̈̈", "¨");
        put("̈ ", "\"");
        put("̋ ", "˝");
        put("̋̋", "˝");
        put("̌ ", "ˇ");
        put("̌̌", "ˇ");
        put("̧ ", "¸");
        put("̧̧", "¸");
        put("̨ ", "˛");
        put("̨̨", "˛");
        put("̂2", "²");
        put("̂3", "³");
        put("̂1", "¹");
// include end?
        put("̀A", "À");
        put("́A", "Á");
        put("̂A", "Â");
        put("̃A", "Ã");
        put("̈A", "Ä");
        put("̊A", "Å");
        put("̧C", "Ç");
        put("̀E", "È");
        put("́E", "É");
        put("̂E", "Ê");
        put("̈E", "Ë");
        put("̀I", "Ì");
        put("́I", "Í");
        put("̂I", "Î");
        put("̈I", "Ï");
        put("̃N", "Ñ");
        put("̀O", "Ò");
        put("́O", "Ó");
        put("̂O", "Ô");
        put("̃O", "Õ");
        put("̈O", "Ö");
        put("̀U", "Ù");
        put("́U", "Ú");
        put("̂U", "Û");
        put("̈U", "Ü");
        put("́Y", "Ý");
        put("̀a", "à");
        put("́a", "á");
        put("̂a", "â");
        put("̃a", "ã");
        put("̈a", "ä");
        put("̊a", "å");
        put("̧c", "ç");
        put("̀e", "è");
        put("́e", "é");
        put("̂e", "ê");
        put("̈e", "ë");
        put("̀i", "ì");
        put("́i", "í");
        put("̂i", "î");
        put("̈i", "ï");
        put("̃n", "ñ");
        put("̀o", "ò");
        put("́o", "ó");
        put("̂o", "ô");
        put("̃o", "õ");
        put("̈o", "ö");
        put("̀u", "ù");
        put("́u", "ú");
        put("̂u", "û");
        put("̈u", "ü");
        put("́y", "ý");
        put("̈y", "ÿ");
        put("̄A", "Ā");
        put("̄a", "ā");
        put("̆A", "Ă");
        put("̆a", "ă");
        put("̨A", "Ą");
        put("̨a", "ą");
        put("́C", "Ć");
        put("́c", "ć");
        put("̂C", "Ĉ");
        put("̂c", "ĉ");
        put("̇C", "Ċ");
        put("̇c", "ċ");
        put("̌C", "Č");
        put("̌c", "č");
        put("̌D", "Ď");
        put("̌d", "ď");
        put("̄E", "Ē");
        put("̄e", "ē");
        put("̆E", "Ĕ");
        put("̆e", "ĕ");
        put("̇E", "Ė");
        put("̇e", "ė");
        put("̨E", "Ę");
        put("̨e", "ę");
        put("̌E", "Ě");
        put("̌e", "ě");
        put("̂G", "Ĝ");
        put("̂g", "ĝ");
        put("̆G", "Ğ");
        put("̆g", "ğ");
        put("̇G", "Ġ");
        put("̇g", "ġ");
        put("̧G", "Ģ");
        put("̧g", "ģ");
        put("̂H", "Ĥ");
        put("̂h", "ĥ");
        put("̃I", "Ĩ");
        put("̃i", "ĩ");
        put("̄I", "Ī");
        put("̄i", "ī");
        put("̆I", "Ĭ");
        put("̆i", "ĭ");
        put("̨I", "Į");
        put("̨i", "į");
        put("̇I", "İ");
        put("̇i", "ı");
        put("̂J", "Ĵ");
        put("̂j", "ĵ");
        put("̧K", "Ķ");
        put("̧k", "ķ");
        put("́L", "Ĺ");
        put("́l", "ĺ");
        put("̧L", "Ļ");
        put("̧l", "ļ");
        put("̌L", "Ľ");
        put("̌l", "ľ");
        put("́N", "Ń");
        put("́n", "ń");
        put("̧N", "Ņ");
        put("̧n", "ņ");
        put("̌N", "Ň");
        put("̌n", "ň");
        put("̄O", "Ō");
        put("̄o", "ō");
        put("̆O", "Ŏ");
        put("̆o", "ŏ");
        put("̋O", "Ő");
        put("̋o", "ő");
        put("́R", "Ŕ");
        put("́r", "ŕ");
        put("̧R", "Ŗ");
        put("̧r", "ŗ");
        put("̌R", "Ř");
        put("̌r", "ř");
        put("́S", "Ś");
        put("́s", "ś");
        put("̂S", "Ŝ");
        put("̂s", "ŝ");
        put("̧S", "Ş");
        put("̧s", "ş");
        put("̌S", "Š");
        put("̌s", "š");
        put("̧T", "Ţ");
        put("̧t", "ţ");
        put("̌T", "Ť");
        put("̌t", "ť");
        put("̃U", "Ũ");
        put("̃u", "ũ");
        put("̄U", "Ū");
        put("̄u", "ū");
        put("̆U", "Ŭ");
        put("̆u", "ŭ");
        put("̊U", "Ů");
        put("̊u", "ů");
        put("̋U", "Ű");
        put("̋u", "ű");
        put("̨U", "Ų");
        put("̨u", "ų");
        put("̂W", "Ŵ");
        put("̂w", "ŵ");
        put("̂Y", "Ŷ");
        put("̂y", "ŷ");
        put("̈Y", "Ÿ");
        put("́Z", "Ź");
        put("́z", "ź");
        put("̇Z", "Ż");
        put("̇z", "ż");
        put("̌Z", "Ž");
        put("̌z", "ž");
        put("̛O", "Ơ");
        put("̛o", "ơ");
        put("̛U", "Ư");
        put("̛u", "ư");
        put("̌A", "Ǎ");
        put("̌a", "ǎ");
        put("̌I", "Ǐ");
        put("̌i", "ǐ");
        put("̌O", "Ǒ");
        put("̌o", "ǒ");
        put("̌U", "Ǔ");
        put("̌u", "ǔ");
        put("̄Ü", "Ǖ");
        put("̄̈U", "Ǖ");
        put("̄ü", "ǖ");
        put("̄̈u", "ǖ");
        put("́Ü", "Ǘ");
        put("́̈U", "Ǘ");
        put("́ü", "ǘ");
        put("́̈u", "ǘ");
        put("̌Ü", "Ǚ");
        put("̌̈U", "Ǚ");
        put("̌ü", "ǚ");
        put("̌̈u", "ǚ");
        put("̀Ü", "Ǜ");
        put("̀̈U", "Ǜ");
        put("̀ü", "ǜ");
        put("̀̈u", "ǜ");
        put("̄Ä", "Ǟ");
        put("̄̈A", "Ǟ");
        put("̄ä", "ǟ");
        put("̄̈a", "ǟ");
        put("̄Ȧ", "Ǡ");
        put("̄̇A", "Ǡ");
        put("̄ȧ", "ǡ");
        put("̄̇a", "ǡ");
        put("̄Æ", "Ǣ");
        put("̄æ", "ǣ");
        put("̌G", "Ǧ");
        put("̌g", "ǧ");
        put("̌K", "Ǩ");
        put("̌k", "ǩ");
        put("̨O", "Ǫ");
        put("̨o", "ǫ");
        put("̄Ǫ", "Ǭ");
        put("̨̄O", "Ǭ");
        put("̄ǫ", "ǭ");
        put("̨̄o", "ǭ");
        put("̌Ʒ", "Ǯ");
        put("̌ʒ", "ǯ");
        put("̌j", "ǰ");
        put("́G", "Ǵ");
        put("́g", "ǵ");
        put("̀N", "Ǹ");
        put("̀n", "ǹ");
        put("́Å", "Ǻ");
        put("́̊A", "Ǻ");
        put("́å", "ǻ");
        put("́̊a", "ǻ");
        put("́Æ", "Ǽ");
        put("́æ", "ǽ");
        put("́Ø", "Ǿ");
        put("́ø", "ǿ");
        put("̏A", "Ȁ");
        put("̏a", "ȁ");
        put("̑A", "Ȃ");
        put("̑a", "ȃ");
        put("̏E", "Ȅ");
        put("̏e", "ȅ");
        put("̑E", "Ȇ");
        put("̑e", "ȇ");
        put("̏I", "Ȉ");
        put("̏i", "ȉ");
        put("̑I", "Ȋ");
        put("̑i", "ȋ");
        put("̏O", "Ȍ");
        put("̏o", "ȍ");
        put("̑O", "Ȏ");
        put("̑o", "ȏ");
        put("̏R", "Ȑ");
        put("̏r", "ȑ");
        put("̑R", "Ȓ");
        put("̑r", "ȓ");
        put("̏U", "Ȕ");
        put("̏u", "ȕ");
        put("̑U", "Ȗ");
        put("̑u", "ȗ");
        put("̌H", "Ȟ");
        put("̌h", "ȟ");
        put("̇A", "Ȧ");
        put("̇a", "ȧ");
        put("̧E", "Ȩ");
        put("̧e", "ȩ");
        put("̄Ö", "Ȫ");
        put("̄̈O", "Ȫ");
        put("̄ö", "ȫ");
        put("̄̈o", "ȫ");
        put("̄Õ", "Ȭ");
        put("̄ ̃O", "Ȭ");
        put("̄õ", "ȭ");
        put("̄ ̃o", "ȭ");
        put("̇O", "Ȯ");
        put("̇o", "ȯ");
        put("̄Ȯ", "Ȱ");
        put("̄̇O", "Ȱ");
        put("̄ȯ", "ȱ");
        put("̄̇o", "ȱ");
        put("̄Y", "Ȳ");
        put("̄y", "ȳ");
        put("̥A", "Ḁ");
        put("̥a", "ḁ");
        put("̇B", "Ḃ");
        put("̇b", "ḃ");
        put("̣B", "Ḅ");
        put("̣b", "ḅ");
        put("̱B", "Ḇ");
        put("̱b", "ḇ");
        put("́Ç", "Ḉ");
        put("̧́C", "Ḉ");
        put("́ç", "ḉ");
        put("̧́c", "ḉ");
        put("̇D", "Ḋ");
        put("̇d", "ḋ");
        put("̣D", "Ḍ");
        put("̣d", "ḍ");
        put("̱D", "Ḏ");
        put("̱d", "ḏ");
        put("̧D", "Ḑ");
        put("̧d", "ḑ");
        put("̭D", "Ḓ");
        put("̭d", "ḓ");
        put("̀Ē", "Ḕ");
        put("̀ ̄E", "Ḕ");
        put("̀ē", "ḕ");
        put("̀ ̄e", "ḕ");
        put("́Ē", "Ḗ");
        put("́ ̄E", "Ḗ");
        put("́ē", "ḗ");
        put("́ ̄e", "ḗ");
        put("̭E", "Ḙ");
        put("̭e", "ḙ");
        put("̰E", "Ḛ");
        put("̰e", "ḛ");
        put("̆Ȩ", "Ḝ");
        put("̧̆E", "Ḝ");
        put("̆ȩ", "ḝ");
        put("̧̆e", "ḝ");
        put("̇F", "Ḟ");
        put("̇f", "ḟ");
        put("̄G", "Ḡ");
        put("̄g", "ḡ");
        put("̇H", "Ḣ");
        put("̇h", "ḣ");
        put("̣H", "Ḥ");
        put("̣h", "ḥ");
        put("̈H", "Ḧ");
        put("̈h", "ḧ");
        put("̧H", "Ḩ");
        put("̧h", "ḩ");
        put("̮H", "Ḫ");
        put("̮h", "ḫ");
        put("̰I", "Ḭ");
        put("̰i", "ḭ");
        put("́Ï", "Ḯ");
        put("́̈I", "Ḯ");
        put("́ï", "ḯ");
        put("́̈i", "ḯ");
        put("́K", "Ḱ");
        put("́k", "ḱ");
        put("̣K", "Ḳ");
        put("̣k", "ḳ");
        put("̱K", "Ḵ");
        put("̱k", "ḵ");
        put("̣L", "Ḷ");
        put("̣l", "ḷ");
        put("̄Ḷ", "Ḹ");
        put("̣̄L", "Ḹ");
        put("̄ḷ", "ḹ");
        put("̣̄l", "ḹ");
        put("̱L", "Ḻ");
        put("̱l", "ḻ");
        put("̭L", "Ḽ");
        put("̭l", "ḽ");
        put("́M", "Ḿ");
        put("́m", "ḿ");
        put("̇M", "Ṁ");
        put("̇m", "ṁ");
        put("̣M", "Ṃ");
        put("̣m", "ṃ");
        put("̇N", "Ṅ");
        put("̇n", "ṅ");
        put("̣N", "Ṇ");
        put("̣n", "ṇ");
        put("̱N", "Ṉ");
        put("̱n", "ṉ");
        put("̭N", "Ṋ");
        put("̭n", "ṋ");
        put("́Õ", "Ṍ");
        put("́ ̃O", "Ṍ");
        put("́õ", "ṍ");
        put("́ ̃o", "ṍ");
        put("̈Õ", "Ṏ");
        put("̈ ̃O", "Ṏ");
        put("̈õ", "ṏ");
        put("̈ ̃o", "ṏ");
        put("̀Ō", "Ṑ");
        put("̀ ̄O", "Ṑ");
        put("̀ō", "ṑ");
        put("̀ ̄o", "ṑ");
        put("́Ō", "Ṓ");
        put("́ ̄O", "Ṓ");
        put("́ō", "ṓ");
        put("́ ̄o", "ṓ");
        put("́P", "Ṕ");
        put("́p", "ṕ");
        put("̇P", "Ṗ");
        put("̇p", "ṗ");
        put("̇R", "Ṙ");
        put("̇r", "ṙ");
        put("̣R", "Ṛ");
        put("̣r", "ṛ");
        put("̄Ṛ", "Ṝ");
        put("̣̄R", "Ṝ");
        put("̄ṛ", "ṝ");
        put("̣̄r", "ṝ");
        put("̱R", "Ṟ");
        put("̱r", "ṟ");
        put("̇S", "Ṡ");
        put("̇s", "ṡ");
        put("̣S", "Ṣ");
        put("̣s", "ṣ");
        put("̇Ś", "Ṥ");
        put("̇ ́S", "Ṥ");
        put("̇ś", "ṥ");
        put("̇ ́s", "ṥ");
        put("̇Š", "Ṧ");
        put("̇̌S", "Ṧ");
        put("̇š", "ṧ");
        put("̇̌s", "ṧ");
        put("̇Ṣ", "Ṩ");
        put("̣̇S", "Ṩ");
        put("̇ṣ", "ṩ");
        put("̣̇s", "ṩ");
        put("̇T", "Ṫ");
        put("̇t", "ṫ");
        put("̣T", "Ṭ");
        put("̣t", "ṭ");
        put("̱T", "Ṯ");
        put("̱t", "ṯ");
        put("̭T", "Ṱ");
        put("̭t", "ṱ");
        put("̤U", "Ṳ");
        put("̤u", "ṳ");
        put("̰U", "Ṵ");
        put("̰u", "ṵ");
        put("̭U", "Ṷ");
        put("̭u", "ṷ");
        put("́Ũ", "Ṹ");
        put("́ ̃U", "Ṹ");
        put("́ũ", "ṹ");
        put("́ ̃u", "ṹ");
        put("̈Ū", "Ṻ");
        put("̈ ̄U", "Ṻ");
        put("̈ū", "ṻ");
        put("̈ ̄u", "ṻ");
        put("̃V", "Ṽ");
        put("̃v", "ṽ");
        put("̣V", "Ṿ");
        put("̣v", "ṿ");
        put("̀W", "Ẁ");
        put("̀w", "ẁ");
        put("́W", "Ẃ");
        put("́w", "ẃ");
        put("̈W", "Ẅ");
        put("̈w", "ẅ");
        put("̇W", "Ẇ");
        put("̇w", "ẇ");
        put("̣W", "Ẉ");
        put("̣w", "ẉ");
        put("̇X", "Ẋ");
        put("̇x", "ẋ");
        put("̈X", "Ẍ");
        put("̈x", "ẍ");
        put("̇Y", "Ẏ");
        put("̇y", "ẏ");
        put("̂Z", "Ẑ");
        put("̂z", "ẑ");
        put("̣Z", "Ẓ");
        put("̣z", "ẓ");
        put("̱Z", "Ẕ");
        put("̱z", "ẕ");
        put("̱h", "ẖ");
        put("̈t", "ẗ");
        put("̊w", "ẘ");
        put("̊y", "ẙ");
        put("̇ſ", "ẛ");
        put("̣A", "Ạ");
        put("̣a", "ạ");
        put("̉A", "Ả");
        put("̉a", "ả");
        put("́Â", "Ấ");
        put("́ ̂A", "Ấ");
        put("́â", "ấ");
        put("́ ̂a", "ấ");
        put("̀Â", "Ầ");
        put("̀ ̂A", "Ầ");
        put("̀â", "ầ");
        put("̀ ̂a", "ầ");
        put("̉Â", "Ẩ");
        put("̉ ̂A", "Ẩ");
        put("̉â", "ẩ");
        put("̉ ̂a", "ẩ");
        put("̃Â", "Ẫ");
        put("̃ ̂A", "Ẫ");
        put("̃â", "ẫ");
        put("̃ ̂a", "ẫ");
        put("̂Ạ", "Ậ");
        put("̣̂A", "Ậ");
        put("̣Â", "Ậ");
        put("̂ạ", "ậ");
        put("̣̂a", "ậ");
        put("̣â", "ậ");
        put("́Ă", "Ắ");
        put("́̆A", "Ắ");
        put("́ă", "ắ");
        put("́̆a", "ắ");
        put("̀Ă", "Ằ");
        put("̀̆A", "Ằ");
        put("̀ă", "ằ");
        put("̀̆a", "ằ");
        put("̉Ă", "Ẳ");
        put("̉̆A", "Ẳ");
        put("̉ă", "ẳ");
        put("̉̆a", "ẳ");
        put("̃Ă", "Ẵ");
        put("̃̆A", "Ẵ");
        put("̃ă", "ẵ");
        put("̃̆a", "ẵ");
        put("̆Ạ", "Ặ");
        put("̣̆A", "Ặ");
        put("̣Ă", "Ặ");
        put("̆ạ", "ặ");
        put("̣̆a", "ặ");
        put("̣ă", "ặ");
        put("̣E", "Ẹ");
        put("̣e", "ẹ");
        put("̉E", "Ẻ");
        put("̉e", "ẻ");
        put("̃E", "Ẽ");
        put("̃e", "ẽ");
        put("́Ê", "Ế");
        put("́ ̂E", "Ế");
        put("́ê", "ế");
        put("́ ̂e", "ế");
        put("̀Ê", "Ề");
        put("̀ ̂E", "Ề");
        put("̀ê", "ề");
        put("̀ ̂e", "ề");
        put("̉Ê", "Ể");
        put("̉ ̂E", "Ể");
        put("̉ê", "ể");
        put("̉ ̂e", "ể");
        put("̃Ê", "Ễ");
        put("̃ ̂E", "Ễ");
        put("̃ê", "ễ");
        put("̃ ̂e", "ễ");
        put("̂Ẹ", "Ệ");
        put("̣̂E", "Ệ");
        put("̣Ê", "Ệ");
        put("̂ẹ", "ệ");
        put("̣̂e", "ệ");
        put("̣ê", "ệ");
        put("̉I", "Ỉ");
        put("̉i", "ỉ");
        put("̣I", "Ị");
        put("̣i", "ị");
        put("̣O", "Ọ");
        put("̣o", "ọ");
        put("̉O", "Ỏ");
        put("̉o", "ỏ");
        put("́Ô", "Ố");
        put("́ ̂O", "Ố");
        put("́ô", "ố");
        put("́ ̂o", "ố");
        put("̀Ô", "Ồ");
        put("̀ ̂O", "Ồ");
        put("̀ô", "ồ");
        put("̀ ̂o", "ồ");
        put("̉Ô", "Ổ");
        put("̉ ̂O", "Ổ");
        put("̉ô", "ổ");
        put("̉ ̂o", "ổ");
        put("̃Ô", "Ỗ");
        put("̃ ̂O", "Ỗ");
        put("̃ô", "ỗ");
        put("̃ ̂o", "ỗ");
        put("̂Ọ", "Ộ");
        put("̣̂O", "Ộ");
        put("̣Ô", "Ộ");
        put("̂ọ", "ộ");
        put("̣̂o", "ộ");
        put("̣ô", "ộ");
        put("́Ơ", "Ớ");
        put("̛́O", "Ớ");
        put("́ơ", "ớ");
        put("̛́o", "ớ");
        put("̀Ơ", "Ờ");
        put("̛̀O", "Ờ");
        put("̀ơ", "ờ");
        put("̛̀o", "ờ");
        put("̉Ơ", "Ở");
        put("̛̉O", "Ở");
        put("̉ơ", "ở");
        put("̛̉o", "ở");
        put("̃Ơ", "Ỡ");
        put("̛̃O", "Ỡ");
        put("̃ơ", "ỡ");
        put("̛̃o", "ỡ");
        put("̣Ơ", "Ợ");
        put("̛̣O", "Ợ");
        put("̣ơ", "ợ");
        put("̛̣o", "ợ");
        put("̣U", "Ụ");
        put("̣u", "ụ");
        put("̉U", "Ủ");
        put("̉u", "ủ");
        put("́Ư", "Ứ");
        put("̛́U", "Ứ");
        put("́ư", "ứ");
        put("̛́u", "ứ");
        put("̀Ư", "Ừ");
        put("̛̀U", "Ừ");
        put("̀ư", "ừ");
        put("̛̀u", "ừ");
        put("̉Ư", "Ử");
        put("̛̉U", "Ử");
        put("̉ư", "ử");
        put("̛̉u", "ử");
        put("̃Ư", "Ữ");
        put("̛̃U", "Ữ");
        put("̃ư", "ữ");
        put("̛̃u", "ữ");
        put("̣Ư", "Ự");
        put("̛̣U", "Ự");
        put("̣ư", "ự");
        put("̛̣u", "ự");
        put("̀Y", "Ỳ");
        put("̀y", "ỳ");
        put("̣Y", "Ỵ");
        put("̣y", "ỵ");
        put("̉Y", "Ỷ");
        put("̉y", "ỷ");
        put("̃Y", "Ỹ");
        put("̃y", "ỹ");
// include?
        put("̂0", "⁰");
        put("̂4", "⁴");
        put("̂5", "⁵");
        put("̂6", "⁶");
        put("̂7", "⁷");
        put("̂8", "⁸");
        put("̂9", "⁹");
        put("̂+", "⁺");
        put("̂−", "⁻");
        put("̂=", "⁼");
        put("̂(", "⁽");
        put("̂)", "⁾");
        put("̣+", "⨥");
        put("̰+", "⨦");
        put("̣-", "⨪");
        put("̣=", "⩦");
        put("̤̈=", "⩷");
        put("̤̈=", "⩷");
// include end?
        put("̥|", "⫰");
        put("̇Ā", "Ǡ");
        put("̇ā", "ǡ");
        put("̇j", "ȷ");
        put("̇L", "Ŀ");
        put("̇l", "ŀ");
        put("̇Ō", "Ȱ");
        put("̇ō", "ȱ");
        put("́Ṡ", "Ṥ");
        put("́ṡ", "ṥ");
        put("́V", "Ǘ");
        put("́v", "ǘ");
        put("̣Ṡ", "Ṩ");
        put("̣ṡ", "ṩ");
        put("̣̣", "̣");
        put("̣ ", "̣");
        put("̆Á", "Ắ");
        put("̆À", "Ằ");
        put("̆Ả", "Ẳ");
        put("̆Ã", "Ẵ");
        put("̆a", "ắ");
        put("̆à", "ằ");
        put("̆ả", "ẳ");
        put("̆ã", "ẵ");
// include?
        put("̌(", "₍");
        put("̌)", "₎");
        put("̌+", "₊");
        put("̌-", "₋");
        put("̌0", "₀");
        put("̌1", "₁");
        put("̌2", "₂");
        put("̌3", "₃");
        put("̌4", "₄");
        put("̌5", "₅");
        put("̌6", "₆");
        put("̌7", "₇");
        put("̌8", "₈");
        put("̌9", "₉");
        put("̌=", "₌");
// include end?
        put("̌ǲ", "ǅ");
        put("̌Ṡ", "Ṧ");
        put("̌ṡ", "ṧ");
        put("̌V", "Ǚ");
        put("̌v", "ǚ");
        put("̧C", "Ḉ");
        put("̧c", "ḉ");
        put("̧¢", "₵");
        put("̧Ĕ", "Ḝ");
        put("̧ĕ", "ḝ");
        put("̂-", "⁻");
        put("̂Á", "Ấ");
        put("̂À", "Ầ");
        put("̂Ả", "Ẩ");
        put("̂Ã", "Ẫ");
        put("̂á", "ấ");
        put("̂à", "ầ");
        put("̂ả", "ẩ");
        put("̂ã", "ẫ");
        put("̂É", "Ế");
        put("̂È", "Ề");
        put("̂Ẻ", "Ể");
        put("̂Ẽ", "Ễ");
        put("̂é", "ế");
        put("̂è", "ề");
        put("̂ẻ", "ể");
        put("̂ẽ", "ễ");
        put("̂Ó", "Ố");
        put("̂Ò", "Ồ");
        put("̂Ỏ", "Ổ");
        put("̂Õ", "Ỗ");
        put("̂ó", "ố");
        put("̂ò", "ồ");
        put("̂ỏ", "ổ");
        put("̂õ", "ỗ");
        put("̦S", "Ș");
        put("̦s", "ș");
        put("̦T", "Ț");
        put("̦t", "ț");
        put("̦̦", ",");
        put("̦ ", ",");
        put("̈Ā", "Ǟ");
        put("̈ā", "ǟ");
        put("̈Í", "Ḯ");
        put("̈í", "ḯ");
        put("̈Ō", "Ȫ");
        put("̈ō", "ȫ");
        put("̈Ú", "Ǘ");
        put("̈Ǔ", "Ǚ");
        put("̈Ù", "Ǜ");
        put("̈ú", "ǘ");
        put("̈ǔ", "ǚ");
        put("̈ù", "ǜ");
        put("̀V", "Ǜ");
        put("̀v", "ǜ");
        put("̉B", "Ɓ");
        put("̉b", "ɓ");
        put("̉C", "Ƈ");
        put("̉c", "ƈ");
        put("̉D", "Ɗ");
        put("̉d", "ɗ");
        put("̉ɖ", "ᶑ");
        put("̉F", "Ƒ");
        put("̉f", "ƒ");
        put("̉G", "Ɠ");
        put("̉g", "ɠ");
        put("̉h", "ɦ");
        put("̉ɟ", "ʄ");
        put("̉K", "Ƙ");
        put("̉k", "ƙ");
        put("̉M", "Ɱ");
        put("̉m", "ɱ");
        put("̉N", "Ɲ");
        put("̉n", "ɲ");
        put("̉P", "Ƥ");
        put("̉p", "ƥ");
        put("̉q", "ʠ");
        put("̉ɜ", "ɝ");
        put("̉s", "ʂ");
        put("̉ə", "ɚ");
        put("̉T", "Ƭ");
        put("̉t", "ƭ");
        put("̉ɹ", "ɻ");
        put("̉V", "Ʋ");
        put("̉v", "ʋ");
        put("̉W", "Ⱳ");
        put("̉w", "ⱳ");
        put("̉Z", "Ȥ");
        put("̉z", "ȥ");
        put("̉̉", "̉");
        put("̉ ", "̉");
        put("̛Ó", "Ớ");
        put("̛O", "Ợ");
        put("̛Ò", "Ờ");
        put("̛Ỏ", "Ở");
        put("̛Õ", "Ỡ");
        put("̛ó", "ớ");
        put("̛ọ", "ợ");
        put("̛ò", "ờ");
        put("̛ỏ", "ở");
        put("̛õ", "ỡ");
        put("̛Ú", "Ứ");
        put("̛Ụ", "Ự");
        put("̛Ù", "Ừ");
        put("̛Ủ", "Ử");
        put("̛Ũ", "Ữ");
        put("̛ú", "ứ");
        put("̛ụ", "ự");
        put("̛ù", "ừ");
        put("̛ủ", "ử");
        put("̛ũ", "ữ");
        put("̛̛", "̛");
        put("̛ ", "̛");
        put("̄É", "Ḗ");
        put("̄È", "Ḕ");
        put("̄é", "ḗ");
        put("̄è", "ḕ");
        put("̄Ó", "Ṓ");
        put("̄Ò", "Ṑ");
        put("̄ó", "ṓ");
        put("̄ò", "ṑ");
        put("̄V", "Ǖ");
        put("̄v", "ǖ");
        put("̨Ō", "Ǭ");
        put("̨ō", "ǭ");
        put("̊Á", "Ǻ");
        put("̊á", "ǻ");
        put("̃Ó", "Ṍ");
        put("̃Ö", "Ṏ");
        put("̃Ō", "Ȭ");
        put("̃ó", "ṍ");
        put("̃ö", "ṏ");
        put("̃ō", "ȭ");
        put("̃Ú", "Ṹ");
        put("̃ú", "ṹ");
        put("̃=", "≃");
        put("̃<", "≲");
        put("̃>", "≳");
        put("́̇S", "Ṥ");
        put("́̇s", "ṥ");
        put("̣̇S", "Ṩ");
        put("̣̇s", "ṩ");
        put("̌̇S", "Ṧ");
        put("̌̇s", "ṧ");
        put("̇ ̄A", "Ǡ");
        put("̇ ̄a", "ǡ");
        put("̇ ̄O", "Ȱ");
        put("̇ ̄o", "ȱ");
        put("̆ ́A", "Ắ");
        put("̆ ́a", "ắ");
        put("̧ ́C", "Ḉ");
        put("̧ ́c", "ḉ");
        put("̂ ́A", "Ấ");
        put("̂ ́a", "ấ");
        put("̂ ́E", "Ế");
        put("̂ ́e", "ế");
        put("̂ ́O", "Ố");
        put("̂ ́o", "ố");
        put("̈ ́I", "Ḯ");
        put("̈ ́i", "ḯ");
        put("̈ ́U", "Ǘ");
        put("̈ ́u", "ǘ");
        put("̛ ́O", "Ớ");
        put("̛ ́o", "ớ");
        put("̛ ́U", "Ứ");
        put("̛ ́u", "ứ");
        put("̄ ́E", "Ḗ");
        put("̄ ́e", "ḗ");
        put("̄ ́O", "Ṓ");
        put("̄ ́o", "ṓ");
        put("̊ ́A", "Ǻ");
        put("̊ ́a", "ǻ");
        put("̃ ́O", "Ṍ");
        put("̃ ́o", "ṍ");
        put("̃ ́U", "Ṹ");
        put("̃ ́u", "ṹ");
        put("̣̆A", "Ặ");
        put("̣̆a", "ặ");
        put("̣ ̂A", "Ậ");
        put("̣ ̂a", "ậ");
        put("̣ ̂E", "Ệ");
        put("̣ ̂e", "ệ");
        put("̣ ̂O", "Ộ");
        put("̣ ̂o", "ộ");
        put("̛̣O", "Ợ");
        put("̛̣o", "ợ");
        put("̛̣U", "Ự");
        put("̛̣u", "ự");
        put("̣ ̄L", "Ḹ");
        put("̣ ̄l", "ḹ");
        put("̣ ̄R", "Ṝ");
        put("̣ ̄r", "ṝ");
        put("̧̆E", "Ḝ");
        put("̧̆e", "ḝ");
        put("̆ ̀A", "Ằ");
        put("̆ ̀a", "ằ");
        put("̆̉A", "Ẳ");
        put("̆̉a", "ẳ");
        put("̆ ̃A", "Ẵ");
        put("̆ ̃a", "ẵ");
        put("̈̌U", "Ǚ");
        put("̈̌u", "ǚ");
        put("̂ ̀A", "Ầ");
        put("̂ ̀a", "ầ");
        put("̂ ̀E", "Ề");
        put("̂ ̀e", "ề");
        put("̂ ̀O", "Ồ");
        put("̂ ̀o", "ồ");
        put("̂̉A", "Ẩ");
        put("̂̉a", "ẩ");
        put("̂̉E", "Ể");
        put("̂̉e", "ể");
        put("̂̉O", "Ổ");
        put("̂̉o", "ổ");
        put("̂ ̃A", "Ẫ");
        put("̂ ̃a", "ẫ");
        put("̂ ̃E", "Ễ");
        put("̂ ̃e", "ễ");
        put("̂ ̃O", "Ỗ");
        put("̂ ̃o", "ỗ");
        put("̈ ̀U", "Ǜ");
        put("̈ ̀u", "ǜ");
        put("̈ ̄A", "Ǟ");
        put("̈ ̄a", "ǟ");
        put("̈ ̄O", "Ȫ");
        put("̈ ̄o", "ȫ");
        put("̃̈O", "Ṏ");
        put("̃̈o", "ṏ");
        put("̛ ̀O", "Ờ");
        put("̛ ̀o", "ờ");
        put("̛ ̀U", "Ừ");
        put("̛ ̀u", "ừ");
        put("̄ ̀E", "Ḕ");
        put("̄ ̀e", "ḕ");
        put("̄ ̀O", "Ṑ");
        put("̄ ̀o", "ṑ");
        put("̛̉O", "Ở");
        put("̛̉o", "ở");
        put("̛̉U", "Ử");
        put("̛̉u", "ử");
        put("̛ ̃O", "Ỡ");
        put("̛ ̃o", "ỡ");
        put("̛ ̃U", "Ữ");
        put("̛ ̃u", "ữ");
        put("̨ ̄O", "Ǭ");
        put("̨ ̄o", "ǭ");
        put("̃ ̄O", "Ȭ");
        put("̃ ̄o", "ȭ");
*/
   }
	
    public static String normalize(String input) {
    	String lookup = mMap.get(input);
    	if (lookup != null) return lookup;
    	return Normalizer.normalize(input, Normalizer.Form.NFC);
    }
    
    public boolean execute(int code) {
    	String composed = executeToString(code);
    	if (composed != null) {
    		if (composed.equals("")) {
    			// Unrecognised - try to use the built-in Java text normalisation
    			int c = composeBuffer.codePointAt(composeBuffer.length() - 1);
    			if (Character.getType(c) != Character.NON_SPACING_MARK) {
    				// Put the combining character(s) at the end, else this won't work
    				composeBuffer.reverse();
    				composed = Normalizer.normalize(composeBuffer.toString(), Normalizer.Form.NFC);
    				if (composed.equals("")) {
    					return true; // incomplete :-)
    				}
    			} else {
    				return true; // there may be multiple combining accents
    			}
    		}

    		clear();
    		composeUser.onText(composed);
    		return false;
    	}
    	return true;
    }
}
