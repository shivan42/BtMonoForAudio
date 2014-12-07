package com.shivandev.btmonoforaudio;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
	static SharedPreferences sharedPref = App.getContext().getSharedPreferences("common_pref", Context.MODE_PRIVATE);
	/**
	 * classic types: String, Int, Boolean
	 */
	static enum Types {
		STR, INT, BOOL
	}

	public static synchronized void saveToSharedPref(final String key, final Types type, final Object value) {
		final SharedPreferences.Editor editor = sharedPref.edit();

		switch (type) {

			case STR:
				editor.putString(key, (String) value);
				break;
			case INT:
				editor.putInt(key, (Integer) value);
				break;
			case BOOL:
				editor.putBoolean(key, (Boolean) value);
				break;
			default:
				break;
		}
		editor.apply();
	}

	public static Object getFromSharedPref(final String key, final Types type, final Object defValue) {
		switch (type) {
			case STR:
				return sharedPref.getString(key, defValue != null ? ((String) defValue) : "");
			case INT:
				return sharedPref.getInt(key, defValue != null ? ((Integer) defValue) : 0);
			case BOOL:
				return sharedPref.getBoolean(key, defValue != null ? ((Boolean) defValue) : false);
		}
		return null;
	}
}
