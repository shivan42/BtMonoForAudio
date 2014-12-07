package com.shivandev.btmonoforaudio;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class MainActivity extends RoboActivity implements View.OnClickListener {

    @InjectView(R.id.am_btn_startSco) private Button onBtn;
    @InjectView(R.id.am_btn_stopSco) private Button offBtn;
    @InjectView(R.id.am_btn_startBtAdapterListener) private Button startServiceBtn;
    @InjectView(R.id.am_btn_stopBtAdapterListener) private Button stopServiceBtn;

    @Inject private Controller controller;
    @Inject private AudioManager mAudioManager;
    private boolean isBtAdapterListenerServiceRun;
    private boolean isScoServiceRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onBtn.setOnClickListener(this);
        offBtn.setOnClickListener(this);
        startServiceBtn.setOnClickListener(this);
        stopServiceBtn.setOnClickListener(this);

        findViewById(R.id.command).setOnClickListener(this);

        refreshInterfaceBtAdapterButtons();
        refreshInterfaceScoButtons();
    }

    private void refreshInterfaceBtAdapterButtons() {
        isBtAdapterListenerServiceRun = controller.isBtListenerRunning();
        startServiceBtn.setEnabled(!isBtAdapterListenerServiceRun);
        stopServiceBtn.setEnabled(isBtAdapterListenerServiceRun);
    }

    private void refreshInterfaceScoButtons() {
        isScoServiceRun = mAudioManager.isBluetoothScoOn();
        onBtn.setEnabled(!isScoServiceRun);
        offBtn.setEnabled(isScoServiceRun);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.am_btn_startSco:
                controller.startSco();
                // todo на момент обновления интерфейса состояние SCO еще не успевает измениться и кнопки не меняют параметра доступности, тогда как сервис срабатывает
                refreshInterfaceScoButtons();
                break;
            case R.id.am_btn_stopSco:
                controller.stopSco();
                refreshInterfaceScoButtons();
                break;
            case R.id.am_btn_startBtAdapterListener:
                controller.startBtAdapterListener();
                refreshInterfaceBtAdapterButtons();
                break;
            case R.id.am_btn_stopBtAdapterListener:
                controller.stopBtAdapterListener();
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
}
