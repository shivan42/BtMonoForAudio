package com.shivandev.btmonoforaudio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import roboguice.activity.RoboActivity;
import roboguice.inject.*;


public class MainActivity extends RoboActivity implements View.OnClickListener {

    @InjectView(R.id.onBtn) private Button onBtn;
    @InjectView(R.id.offBtn) private Button offBtn;
    @InjectView(R.id.startServiceBtn) private Button startServiceBtn;
    @InjectView(R.id.stopServiceBtn) private Button stopServiceBtn;

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
            case R.id.onBtn:
                startService(ScoProcessingSrv.createStartScoIntent(getApplicationContext()));
                break;
            case R.id.offBtn:
                startService(ScoProcessingSrv.createStopScoIntent(getApplicationContext()));
                break;
            case R.id.startServiceBtn:
                startService(new Intent(getApplicationContext(), BtListenerSrv.class));
                break;
            case R.id.stopServiceBtn:
                stopService(new Intent(getApplicationContext(), BtListenerSrv.class));
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
