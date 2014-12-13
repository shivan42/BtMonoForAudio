package com.shivandev.btmonoforaudio.common;

import com.shivandev.btmonoforaudio.utils.SharedPrefHelper;

public enum Prefs {
	IS_MUSIC_PLAYER_CONTROL_NEEDED(SharedPrefHelper.Types.BOOL),
	IS_BT_SERVICE_START_AFTER_REBOOT(SharedPrefHelper.Types.BOOL);

	private final SharedPrefHelper.Types type;

	Prefs(SharedPrefHelper.Types type) {
		this.type = type;
	}

	public SharedPrefHelper.Types getType() {
		return type;
	}

	public void set(final Object value) {
		SharedPrefHelper.saveToSharedPref(name(), getType(), value);
	}

	public Object get(final Object defValue) {
		return SharedPrefHelper.getFromSharedPref(name(), getType(), defValue);
	}

    // не типизированные getters без конкретных знакчений по умолчанию, эта значения заданы в хелпере SharedPrefHelper
	public Object get() { return get(null); }
    // типизированные getters без конкретных знакчений по умолчанию
	public String getStr() { return (String) get(); }
	public Integer getInt() { return (Integer) get(); }
	public Boolean getBool() { return (Boolean) get(); }
}
