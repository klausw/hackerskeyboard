/*
 * Copyright (C) 2008-2009 Google Inc.
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class InputLanguageSelection extends PreferenceActivity {
	private static final String TAG = "PCKeyboardILS";
    private String mSelectedLanguages;
    private ArrayList<Loc> mAvailableLanguages = new ArrayList<Loc>();
    private static final String[] BLACKLIST_LANGUAGES = {
        "ko", "ja", "zh"
    };
    
    // Run the GetLanguages.sh script to update the following lists based on
    // the available keyboard resources and dictionaries.
    private static final String[] KBD_LOCALIZATIONS = {
        "ar", "bg", "ca", "cs", "da", "de", "el", "en", "en-rDV", "en-rGB",
        "es", "es-rUS", "fa", "fi", "fr", "fr-rCA", "hr", "hu", "in",
        "it", "iw", "ja", "ka", "ko", "lt", "lv", "nb", "nl", "pl", "pt",
        "pt-rPT", "rm", "ro", "ru", "sk", "sl", "sr", "sv", "th", "tl",
        "tr", "uk", "vi", "zh-rCN", "zh-rTW"
    };

    private static final String[] KBD_5_ROW = {
        "ar", "cs", "da", "de", "el", "en", "en-rDV", "en-rGB", "es", "fi",
	"fr", "fr-rCA", "hr", "iw", "ro", "ru", "si", "sl", "sv", "th",
	"uk"
    };

    private static final String[] KBD_4_ROW = {
        "ar", "cs", "da", "de", "el", "en", "en-rDV", "fr", "fr-rCA", "iw",
	"nb", "ru", "sl", "sr", "sv", "uk"
    };

    private static class Loc implements Comparable<Object> {
        static Collator sCollator = Collator.getInstance();

        String label;
        Locale locale;

        public Loc(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        @Override
        public String toString() {
            return this.label;
        }

        public int compareTo(Object o) {
            return sCollator.compare(this.label, ((Loc) o).label);
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.language_prefs);
        // Get the settings preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSelectedLanguages = sp.getString(LatinIME.PREF_SELECTED_LANGUAGES, "");
        String[] languageList = mSelectedLanguages.split(",");
        mAvailableLanguages = getUniqueLocales();
        PreferenceGroup parent = getPreferenceScreen();
        for (int i = 0; i < mAvailableLanguages.size(); i++) {
            CheckBoxPreference pref = new CheckBoxPreference(this);
            Locale locale = mAvailableLanguages.get(i).locale;
            pref.setTitle(LanguageSwitcher.toTitleCase(locale.getDisplayName(locale)) +
            		" [" + locale.toString() + "]");
            boolean checked = isLocaleIn(locale, languageList);
            pref.setChecked(checked);
            String language = locale.getLanguage();
            boolean has4Row = arrayContains(KBD_4_ROW, language);
            boolean has5Row = arrayContains(KBD_5_ROW, language);
            List<String> summaries = new ArrayList<String>(3);
            if (has5Row) summaries.add("5-row");           
            if (has4Row) summaries.add("4-row");           
            if (hasDictionary(locale)) {
            	summaries.add(getResources().getString(R.string.has_dictionary));
            }
            if (!summaries.isEmpty()) {
            	StringBuilder summary = new StringBuilder();
            	for (int j = 0; j < summaries.size(); ++j) {
            		if (j > 0) summary.append(", ");
            		summary.append(summaries.get(j));
            	}
            	pref.setSummary(summary.toString());
            }
            parent.addPreference(pref);
        }
    }

    private boolean isLocaleIn(Locale locale, String[] list) {
        String lang = get5Code(locale);
        for (int i = 0; i < list.length; i++) {
            if (lang.equalsIgnoreCase(list[i])) return true;
        }
        return false;
    }

    private boolean hasDictionary(Locale locale) {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale saveLocale = conf.locale;
        boolean haveDictionary = false;
        conf.locale = locale;
        res.updateConfiguration(conf, res.getDisplayMetrics());

        int[] dictionaries = LatinIME.getDictionary(res);
        BinaryDictionary bd = new BinaryDictionary(this, dictionaries, Suggest.DIC_MAIN);

        // Is the dictionary larger than a placeholder? Arbitrarily chose a lower limit of
        // 4000-5000 words, whereas the LARGE_DICTIONARY is about 20000+ words.
        if (bd.getSize() > Suggest.LARGE_DICTIONARY_THRESHOLD / 4) {
            haveDictionary = true;
        } else {
        	BinaryDictionary plug = PluginManager.getDictionary(getApplicationContext(), locale.getLanguage());
        	if (plug != null) {
        		bd.close();
        		bd = plug;
        		haveDictionary = true;
        	}
        }
        bd.close();
        conf.locale = saveLocale;
        res.updateConfiguration(conf, res.getDisplayMetrics());
        return haveDictionary;
    }

    private String get5Code(Locale locale) {
        String country = locale.getCountry();
        return locale.getLanguage()
                + (TextUtils.isEmpty(country) ? "" : "_" + country);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the selected languages
        String checkedLanguages = "";
        PreferenceGroup parent = getPreferenceScreen();
        int count = parent.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            CheckBoxPreference pref = (CheckBoxPreference) parent.getPreference(i);
            if (pref.isChecked()) {
                Locale locale = mAvailableLanguages.get(i).locale;
                checkedLanguages += get5Code(locale) + ",";
            }
        }
        if (checkedLanguages.length() < 1) checkedLanguages = null; // Save null
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putString(LatinIME.PREF_SELECTED_LANGUAGES, checkedLanguages);
        SharedPreferencesCompat.apply(editor);
    }

    private static String asString(Set<String> set) {
    	StringBuilder out = new StringBuilder();
    	out.append("set(");
    	String[] parts = new String[set.size()];
    	parts = set.toArray(parts);
        Arrays.sort(parts);
        for (int i = 0; i < parts.length; ++i) {
    		if (i > 0) out.append(", ");
    		out.append(parts[i]);
    	}
    	out.append(")");
    	return out.toString();
    }
    
    ArrayList<Loc> getUniqueLocales() {
        Set<String> localeSet = new HashSet<String>();
        Set<String> langSet = new HashSet<String>();
        String[] sysLocales = getAssets().getLocales();
        
        // First, add zz_ZZ style full language+country locales
        for (int i = 0; i < sysLocales.length; ++i) {
        	String sl = sysLocales[i];
        	if (sl.length() != 5) continue;
        	localeSet.add(sl);
        	langSet.add(sl.substring(0, 2));
        }
        
        // Add entries for system languages without country, but only if there's
        // no full locale for that language yet.
        for (int i = 0; i < sysLocales.length; ++i) {
        	String sl = sysLocales[i];
        	if (sl.length() != 2 || langSet.contains(sl)) continue;
        	localeSet.add(sl);
        }
        
        // Add entries for additional languages supported by the keyboard.
        for (int i = 0; i < KBD_LOCALIZATIONS.length; ++i) {
        	String kl = KBD_LOCALIZATIONS[i];
        	if (kl.length() == 2 && langSet.contains(kl)) continue;
        	localeSet.add(kl);
        }
        Log.i(TAG, "localeSet=" + asString(localeSet));
        Log.i(TAG, "langSet=" + asString(langSet));

        // Now build the locale list for display
        String[] locales = new String[localeSet.size()];
        locales = localeSet.toArray(locales);
        Arrays.sort(locales);
        
        ArrayList<Loc> uniqueLocales = new ArrayList<Loc>();

        final int origSize = locales.length;
        Loc[] preprocess = new Loc[origSize];
        int finalSize = 0;
        for (int i = 0 ; i < origSize; i++ ) {
            String s = locales[i];
            int len = s.length();
            if (len == 2 || len == 5) {
                String language = s.substring(0, 2);
                Locale l;
                if (len == 5) {
                    String country = s.substring(3, 5);
                    l = new Locale(language, country);
                } else {
                    l = new Locale(language);                	
                }
                
                // Exclude languages that are not relevant to LatinIME
                if (arrayContains(BLACKLIST_LANGUAGES, language)) continue;

                if (finalSize == 0) {
                    preprocess[finalSize++] =
                            new Loc(LanguageSwitcher.toTitleCase(l.getDisplayName(l)), l);
                } else {
                    // check previous entry:
                    //  same lang and a country -> upgrade to full name and
                    //    insert ours with full name
                    //  diff lang -> insert ours with lang-only name
                    if (preprocess[finalSize-1].locale.getLanguage().equals(
                            language)) {
                        preprocess[finalSize-1].label = LanguageSwitcher.toTitleCase(
                                preprocess[finalSize-1].locale.getDisplayName());
                        preprocess[finalSize++] =
                                new Loc(LanguageSwitcher.toTitleCase(l.getDisplayName()), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                        } else {
                            displayName = LanguageSwitcher.toTitleCase(l.getDisplayName(l));
                            preprocess[finalSize++] = new Loc(displayName, l);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < finalSize ; i++) {
            uniqueLocales.add(preprocess[i]);
        }
        return uniqueLocales;
    }

    private boolean arrayContains(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
