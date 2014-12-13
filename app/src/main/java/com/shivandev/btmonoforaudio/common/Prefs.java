package com.shivandev.btmonoforaudio.common;

public enum Prefs {
	IS_BT_LISTENER_RUN(SharedPrefHelper.Types.BOOL);

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

	public Object get() { return get(null); }
	public String getStr() { return (String) get(null); }
	public Integer getInt() { return (Integer) get(null); }
	public Boolean getBool() { return (Boolean) get(null); }
}
