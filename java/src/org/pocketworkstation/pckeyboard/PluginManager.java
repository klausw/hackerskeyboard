package org.pocketworkstation.pckeyboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class PluginManager extends BroadcastReceiver {
    private static String TAG = "PCKeyboard";
    private static String HK_INTENT_DICT = "org.pocketworkstation.DICT";
    private static String SOFTKEYBOARD_INTENT_DICT = "com.menny.android.anysoftkeyboard.DICTIONARY";

    private static Map<String, DictPluginSpec> mPluginDicts =
        new HashMap<String, DictPluginSpec>();
    
    static interface DictPluginSpec {
        BinaryDictionary getDict(Context context);
    }

    static private abstract class DictPluginSpecBase
            implements DictPluginSpec {
        String mPackageName;
        
        Resources getResources(Context context) {
            PackageManager packageManager = context.getPackageManager();
            Resources res = null;
            try {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(mPackageName, 0);
                res = packageManager.getResourcesForApplication(appInfo);
            } catch (NameNotFoundException e) {
                Log.i(TAG, "couldn't get resources");
            }
            return res;
        }

        abstract InputStream getStream(Resources res);

        public BinaryDictionary getDict(Context context) {
            Resources res = getResources(context);
            if (res == null) return null;

            InputStream in = getStream(res);
            if (in == null) return null;

            InputStream[] dicts = new InputStream[] { in };
            BinaryDictionary dict = new BinaryDictionary(
                    context, dicts, Suggest.DIC_MAIN);
            //Log.i(TAG, "dict size=" + dict.getSize());
            return dict;
        }
    }

    static private class DictPluginSpecHK
            extends DictPluginSpecBase {
        
        int mRawId;

        public DictPluginSpecHK(String pkg, int id) {
            mPackageName = pkg;
            mRawId = id;
        }

        @Override
        InputStream getStream(Resources res) {
            if (mRawId == 0) return null;
            return res.openRawResource(mRawId);
        }
    }
    
    static private class DictPluginSpecSoftKeyboard
            extends DictPluginSpecBase {
        
        String mAssetName;

        public DictPluginSpecSoftKeyboard(String pkg, String asset) {
            mPackageName = pkg;
            mAssetName = asset;
        }

        @Override
        InputStream getStream(Resources res) {
            if (mAssetName == null) return null;
            try {
                return res.getAssets().open(mAssetName);
            } catch (IOException e) {
                Log.e(TAG, "Dictionary asset loading failure");
                return null;
            }
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Package information changed, updating dictionaries.");
        getPluginDictionaries(context);
    }

    static void getSoftKeyboardDictionaries(PackageManager packageManager) {
        Intent dictIntent = new Intent(SOFTKEYBOARD_INTENT_DICT);
        List<ResolveInfo> dictPacks = packageManager.queryBroadcastReceivers(
        		dictIntent, PackageManager.GET_RECEIVERS);
        for (ResolveInfo ri : dictPacks) {
            ApplicationInfo appInfo = ri.activityInfo.applicationInfo;
            String pkgName = appInfo.packageName;
            boolean success = false;
            try {
                Resources res = packageManager.getResourcesForApplication(appInfo);
                Log.i(TAG, "Found dictionary plugin package: " + pkgName);
                int dictId = res.getIdentifier("dictionaries", "xml", pkgName);
                if (dictId == 0) continue;
                XmlResourceParser xrp = res.getXml(dictId);

                String assetName = null;
                String lang = null;
                try {
                    int current = xrp.getEventType();
                    while (current != XmlResourceParser.END_DOCUMENT) {
                        if (current == XmlResourceParser.START_TAG) {
                            String tag = xrp.getName();
                            if (tag != null) {
                                if (tag.equals("Dictionary")) {
                                    assetName = xrp.getAttributeValue(null, "dictionaryAssertName"); // sic
                                    lang = xrp.getAttributeValue(null, "locale");
                                    //Log.i(TAG, "asset=" + assetName + " lang=" + lang);
                                }
                            }
                        }
                        xrp.next();
                        current = xrp.getEventType();
                    }
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "Dictionary XML parsing failure");
                } catch (IOException e) {
                    Log.e(TAG, "Dictionary XML IOException");
                }

                if (assetName == null || lang == null) continue;
                DictPluginSpec spec = new DictPluginSpecSoftKeyboard(pkgName, assetName);
                mPluginDicts.put(lang, spec);
                Log.i(TAG, "Loaded plugin dictionary: lang=" + lang);
                success = true;
            } catch (NameNotFoundException e) {
                Log.i(TAG, "bad");
            } finally {
                if (!success) {
                    Log.i(TAG, "failed to load plugin dictionary from " + pkgName);
                }
            }
        }
    }

    static void getHKDictionaries(PackageManager packageManager) {
        Intent dictIntent = new Intent(HK_INTENT_DICT);
        List<ResolveInfo> dictPacks = packageManager.queryIntentActivities(dictIntent, 0);
        for (ResolveInfo ri : dictPacks) {
            ApplicationInfo appInfo = ri.activityInfo.applicationInfo;
            String pkgName = appInfo.packageName;
            boolean success = false;
            try {
                Resources res = packageManager.getResourcesForApplication(appInfo);
                Log.i(TAG, "Found dictionary plugin package: " + pkgName);
                int langId = res.getIdentifier("dict_language", "string", pkgName);
                if (langId == 0) continue;
                String lang = res.getString(langId);
                int rawId = res.getIdentifier("main", "raw", pkgName);
                if (rawId == 0) continue;
                DictPluginSpec spec = new DictPluginSpecHK(pkgName, rawId);
                mPluginDicts.put(lang, spec);
                Log.i(TAG, "Loaded plugin dictionary: lang=" + lang);
                success = true;
            } catch (NameNotFoundException e) {
                Log.i(TAG, "bad");
            } finally {
                if (!success) {
                    Log.i(TAG, "failed to load plugin dictionary from " + pkgName);
                }
            }
        }
    }

    static void getPluginDictionaries(Context context) {
        mPluginDicts.clear();
        PackageManager packageManager = context.getPackageManager();
        getSoftKeyboardDictionaries(packageManager);
        getHKDictionaries(packageManager);
    }
    
    static BinaryDictionary getDictionary(Context context, String lang) {
        //Log.i(TAG, "Looking for plugin dictionary for lang=" + lang);
        DictPluginSpec spec = mPluginDicts.get(lang);
        if (spec == null) spec = mPluginDicts.get(lang.substring(0, 2));
        if (spec == null) {
            //Log.i(TAG, "No plugin found.");
            return null;
        }
        BinaryDictionary dict = spec.getDict(context);
        Log.i(TAG, "Found plugin dictionary for " + lang + ", size=" + dict.getSize());
        return dict;
    }
}
