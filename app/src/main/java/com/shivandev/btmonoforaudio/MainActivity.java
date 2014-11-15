package com.shivandev.btmonoforaudio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.onBtn).setOnClickListener(this);
        findViewById(R.id.offBtn).setOnClickListener(this);
        findViewById(R.id.onServiceBtn).setOnClickListener(this);
        findViewById(R.id.offServiceBtn).setOnClickListener(this);
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
//                stopService(ScoProcessingSrv.createStopScoIntent(getApplicationContext()));
                startService(ScoProcessingSrv.createStopScoIntent(getApplicationContext()));
                break;
            case R.id.onServiceBtn:
//                getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.pause"));
                startService(new Intent(getApplicationContext(), BtListenerSrv.class));
                break;
            case R.id.offServiceBtn:
                stopService(new Intent(getApplicationContext(), BtListenerSrv.class));
                break;
            case R.id.command:
//                getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
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
