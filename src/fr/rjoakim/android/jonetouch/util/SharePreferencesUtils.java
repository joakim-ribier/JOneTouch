package fr.rjoakim.android.jonetouch.util;

import com.google.common.base.Strings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * Copyright 2013 Joakim Ribier
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class SharePreferencesUtils {
	
	private static final String PREFS_FILE = "jonetouch_prefs";
	private static final String KEY = "key";
	
	public static boolean isAppLocked(Activity activity) {
		return Strings.isNullOrEmpty(getKey(activity));
	}
	
	public static SharedPreferences getSharePreferences(Context context) {
		return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
	}
	
	public static void setAppLocked(Activity activity) {
		SharedPreferences sharedPreferences = getSharePreferences(activity);
		Editor edit = sharedPreferences.edit();
		edit.remove(KEY);
		edit.commit();
	}

	public static String getKey(Activity activity) {
		SharedPreferences sharedPreferences = getSharePreferences(activity);
		return sharedPreferences.getString(KEY, null);
	}

	public static void setKey(Activity activity, String key) {
		SharedPreferences sharedPreferences = getSharePreferences(activity);
		Editor edit = sharedPreferences.edit();
		edit.putString(KEY, key);
		edit.commit();
	}
}
