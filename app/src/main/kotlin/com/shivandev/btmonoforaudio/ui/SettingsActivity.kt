package com.shivandev.btmonoforaudio.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.shivandev.btmonoforaudio.R
import android.widget.CheckBox
import com.shivandev.btmonoforaudio.common.Prefs
import android.widget.CompoundButton
import roboguice.activity.RoboActivity
import com.google.inject.Inject


public class SettingsActivity : BaseRoboActivity(), CompoundButton.OnCheckedChangeListener {
//        Inject private var controller: Controller? = null
//        InjectView(R.id.as_chb_startBtServiceAfterReboot) private var startBtServiceAfterRebootOptionChB: CheckBox? = null
    //    InjectView(R.id.as_chb_notifyBtServiceIfBtAdapterIsOn) private val notifyBtServiceIfBtAdapterIsOnOptionChB: CheckBox? = null

    var startBtServiceAfterRebootOptionChB: CheckBox? = null
    var notifyBtServiceIfBtAdapterIsOnOptionChB: CheckBox? = null
    var checkBtAdapterIsOnChB: CheckBox? = null

    val controller = Controller()

//    var controller: Controller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BaseRoboActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

//        controller = Controller()

        startBtServiceAfterRebootOptionChB = findViewById(R.id.as_chb_startBtServiceAfterReboot) as CheckBox
        startBtServiceAfterRebootOptionChB!!.setOnCheckedChangeListener(this)
        notifyBtServiceIfBtAdapterIsOnOptionChB = findViewById(R.id.as_chb_notifyBtServiceIfBtAdapterIsOn) as CheckBox
        notifyBtServiceIfBtAdapterIsOnOptionChB!!.setOnCheckedChangeListener(this)
        checkBtAdapterIsOnChB = findViewById(R.id.as_chb_checkBtAdapterIsOn) as CheckBox
        checkBtAdapterIsOnChB!!.setOnCheckedChangeListener(this)

        refreshInterfaceDependedOnPrefs()
    }

    private fun refreshInterfaceDependedOnPrefs() {
        startBtServiceAfterRebootOptionChB!!.setChecked(Prefs.IS_BT_SERVICE_START_AFTER_REBOOT.getBool())
        notifyBtServiceIfBtAdapterIsOnOptionChB!!.setChecked(Prefs.IS_NOTIFY_BT_SERVICE_IF_BT_ADAPTER_IS_ON.getBool())
        checkBtAdapterIsOnChB!!.setChecked(Prefs.IS_CHECK_BT_ADAPTER_IS_ON_OPTION.getBool())
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.getId()) {
            R.id.as_chb_startBtServiceAfterReboot -> controller.setStartServiceAfterRebootOption(isChecked)
            R.id.as_chb_notifyBtServiceIfBtAdapterIsOn -> controller.setNotifyAboutBtServiceIfBtAdapterIsOnOption(isChecked)
            R.id.as_chb_checkBtAdapterIsOn -> controller.setCheckBtAdapterIsOnOption(isChecked)
            else -> {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true
//        }

        return super<BaseRoboActivity>.onOptionsItemSelected(item)
    }
}
