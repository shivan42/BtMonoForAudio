package com.shivandev.btmonoforaudio.views;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.R;

import java.util.Observable;
import java.util.Observer;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class MainActivity extends RoboActivity implements View.OnClickListener, Observer {

    @InjectView(R.id.am_btn_startSco) private Button onBtn;
    @InjectView(R.id.am_btn_stopSco) private Button offBtn;
    @InjectView(R.id.am_btn_startBtAdapterListener) private Button startServiceBtn;
    @InjectView(R.id.am_btn_stopBtAdapterListener) private Button stopServiceBtn;

    @Inject private MainActivityController mainActivityController;
    @Inject private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onBtn.setOnClickListener(this);
        offBtn.setOnClickListener(this);
        startServiceBtn.setOnClickListener(this);
        stopServiceBtn.setOnClickListener(this);

        findViewById(R.id.command).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshInterfaceBtAdapterButtons();
        refreshInterfaceScoButtons();
        mainActivityController.startScoListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainActivityController.stopScoListener(this);
    }

    private void refreshInterfaceBtAdapterButtons() {
        boolean isBtAdapterListenerServiceRun = mainActivityController.isBtListenerRunning();
        startServiceBtn.setEnabled(!isBtAdapterListenerServiceRun);
        stopServiceBtn.setEnabled(isBtAdapterListenerServiceRun);
    }

    private void refreshInterfaceScoButtons() {
        boolean isScoServiceRun = mAudioManager.isBluetoothScoOn();
        onBtn.setEnabled(!isScoServiceRun);
        offBtn.setEnabled(isScoServiceRun);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.am_btn_startSco:
                mainActivityController.startSco();
                break;
            case R.id.am_btn_stopSco:
                mainActivityController.stopSco();
                break;
            case R.id.am_btn_startBtAdapterListener:
                mainActivityController.startBtAdapterListener();
                refreshInterfaceBtAdapterButtons();
                break;
            case R.id.am_btn_stopBtAdapterListener:
                mainActivityController.stopBtAdapterListener();
                refreshInterfaceBtAdapterButtons();
                break;
            case R.id.command:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
                    }
                }, 1000);
                break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        refreshInterfaceScoButtons();
    }
}
