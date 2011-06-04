package org.pocketworkstation.pckeyboard;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.Log;

public class PluginManager extends BroadcastReceiver {
	private static String TAG = "PCKeyboard";
	private static String INTENT_DICT = "org.pocketworkstation.DICT";

	private static Map<String, DictPluginSpec> mPluginDicts =
		new HashMap<String, DictPluginSpec>();
	
	static private class DictPluginSpec {
		String packageName;
		int rawId;
		
		public DictPluginSpec(String pkg, int id) {
			packageName = pkg;
			rawId = id;
		}
		
		BinaryDictionary getDict(Context context) {
	        PackageManager packageManager = context.getPackageManager();
	        Resources res;
			try {
				ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
		        res = packageManager.getResourcesForApplication(appInfo);
			} catch (NameNotFoundException e) {
				return null;
			}
    		//Log.i(TAG, "dict raw id=" + rawId);
    		if (rawId == 0) return null;

    		InputStream in = res.openRawResource(rawId);
			InputStream[] dicts = new InputStream[] { in };
			BinaryDictionary dict = new BinaryDictionary(
					context, dicts, Suggest.DIC_MAIN);
			//Log.i(TAG, "dict size=" + dict.getSize());
			return dict;
		}
	}
	
	//private static 

	@Override
	public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Package information changed, updating dictionaries: " + intent);
        getPluginDictionaries(context);
	}

	static void getPluginDictionaries(Context context) {
		mPluginDicts.clear();
        PackageManager packageManager = context.getPackageManager();
        Intent dictIntent = new Intent(INTENT_DICT);
        List<ResolveInfo> dictPacks = packageManager.queryIntentActivities(dictIntent, 0);
        for (ResolveInfo ri : dictPacks) {
    		ApplicationInfo appInfo = ri.activityInfo.applicationInfo;
    		String pkgName = appInfo.packageName;
        	boolean success = false;
        	try {
        		Resources res = packageManager.getResourcesForApplication(appInfo);
        		Log.i(TAG, "Found plugin package: " + pkgName);
        		int langId = res.getIdentifier("dict_language", "string", pkgName);
        		if (langId == 0) continue;
        		String lang = res.getString(langId);
        		int rawId = res.getIdentifier("main", "raw", pkgName);
        		if (rawId == 0) continue;
        		DictPluginSpec spec = new DictPluginSpec(pkgName, rawId);
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
	
	static BinaryDictionary getDictionary(Context context, String lang) {
    	Log.i(TAG, "Looking for plugin dictionary for lang=" + lang);
		DictPluginSpec spec = mPluginDicts.get(lang);
		if (spec == null) spec = mPluginDicts.get(lang.substring(0, 2));
		if (spec == null) {
			Log.i(TAG, "No plugin found.");
			return null;
		}
		BinaryDictionary dict = spec.getDict(context);
		Log.i(TAG, "Found plugin dictionary for " + lang + ", size=" + dict.getSize());
		return dict;
	}
}
