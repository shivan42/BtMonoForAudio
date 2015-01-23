package com.shivandev.btmonoforaudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.google.inject.Inject;
import com.shivandev.btmonoforaudio.R;
import com.shivandev.btmonoforaudio.common.Prefs;
import com.shivandev.btmonoforaudio.model.ScoStateObserve;

import java.util.Observable;
import java.util.Observer;

import roboguice.inject.InjectView;


public class MainActivity extends BaseRoboActivity implements View.OnClickListener, Observer, CompoundButton.OnCheckedChangeListener {

	private static final int REQUEST_ENABLE_BT = 2015011901;

    @InjectView(R.id.am_btn_startSco) private Button onBtn;
    @InjectView(R.id.am_btn_stopSco) private Button offBtn;
    @InjectView(R.id.am_btn_startBtAdapterListener) private Button startServiceBtn;
    @InjectView(R.id.am_btn_stopBtAdapterListener) private Button stopServiceBtn;
    @InjectView(R.id.am_btn_menu) private ImageButton settingsActivityBtn;
    @InjectView(R.id.am_chb_controlMusicPlayer) private CheckBox controlMusicPlayerOptionChB;

    @Inject private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onBtn.setOnClickListener(this);
        offBtn.setOnClickListener(this);
        startServiceBtn.setOnClickListener(this);
        stopServiceBtn.setOnClickListener(this);
        settingsActivityBtn.setOnClickListener(this);
        controlMusicPlayerOptionChB.setOnCheckedChangeListener(this);

        findViewById(R.id.command).setOnClickListener(this);
        refreshInterfaceDependedOnPrefs();
    }

    private void refreshInterfaceDependedOnPrefs() {
        controlMusicPlayerOptionChB.setChecked(Prefs.IS_MUSIC_PLAYER_CONTROL_NEEDED.getBool());
    }

	@Override
    protected void onResume() {
        super.onResume();
        refreshInterfaceBtAdapterButtons();
        refreshInterfaceScoButtons();
        Controller.startScoListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Controller.stopScoListener(this);
    }

    private void refreshInterfaceBtAdapterButtons() {
        boolean isBtAdapterListenerServiceRun = Controller.isBtListenerRunning();
        startServiceBtn.setEnabled(!isBtAdapterListenerServiceRun);
        stopServiceBtn.setEnabled(isBtAdapterListenerServiceRun);
    }

    private void refreshInterfaceScoButtons() {
        boolean isScoServiceRun = Controller.isScoProcessingRunning();
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
            case R.id.am_btn_stopSco:
                controller.switchSco(this);
                break;
            case R.id.am_btn_startBtAdapterListener:
            case R.id.am_btn_stopBtAdapterListener:
                Controller.switchBtListener(getApplicationContext());
                break;
            case R.id.am_btn_menu:
                controller.menuCall();
                break;
            case R.id.command:
                new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent player = new Intent(Intent.ACTION_MEDIA_BUTTON);
						player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
						sendOrderedBroadcast(player, null);
						player.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
						sendOrderedBroadcast(player, null);
//                        getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.play"));
//                        getApplicationContext().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
					}
				}, 1000);
            break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null && data instanceof ScoStateObserve.ScoState) {
            switch ((ScoStateObserve.ScoState) data) {
                case SCO:
                    refreshInterfaceScoButtons();
                    break;
                case BT_LISTENER:
                    refreshInterfaceBtAdapterButtons();
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.am_chb_controlMusicPlayer:
                controller.setControlMusicPlayerOption(isChecked);
                break;
        }
//        refreshInterfaceDependedOnPrefs();
    }

//	public void requestEnableBt() {
//		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//		startActivity(enableBtIntent);
////		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			if (requestCode == REQUEST_ENABLE_BT) {
//				try {
//					Thread.sleep(2000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				controller.switchSco(this);
//			}
//		}
//	}
}
