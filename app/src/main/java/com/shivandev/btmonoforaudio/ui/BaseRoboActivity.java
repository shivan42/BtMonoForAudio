package com.shivandev.btmonoforaudio.ui;

import com.shivandev.btmonoforaudio.common.App;

import roboguice.activity.RoboActivity;

public class BaseRoboActivity extends RoboActivity {
	@Override
	protected void onStart() {
		super.onStart();
		((App) getApplication()).sendTrackInfo(getClass().getSimpleName());
	}
}
