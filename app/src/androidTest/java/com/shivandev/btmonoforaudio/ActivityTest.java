package com.shivandev.btmonoforaudio;

import android.media.AudioManager;
import android.widget.Button;

import com.google.inject.AbstractModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import roboguice.RoboGuice;

//import static org.fest.assertions.api.ANDROID.assertThat;
import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class ActivityTest {

    private MainActivity mainActivity;
    private Controller controllerMock = mock(Controller.class);
    private AudioManager mAudioManagerMock = mock(AudioManager.class);

    @Before
    public  void setUp() {
        RoboGuice.overrideApplicationInjector(Robolectric.application, new MyTestModule());
        when(controllerMock.isBtListenerRunning()).thenReturn(false);
        when(mAudioManagerMock.isBluetoothScoOn()).thenReturn(false);
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
    }

    @Test
    public void viewsShouldNotBeNull() {
        assertThat(mainActivity).isNotNull();

        Button btnOn = (Button) mainActivity.findViewById(R.id.am_btn_startSco);
        Button btnOff = (Button) mainActivity.findViewById(R.id.am_btn_stopSco);
        Button btnStartSrv = (Button) mainActivity.findViewById(R.id.am_btn_startBtAdapterListener);
        Button btnStopSrv = (Button) mainActivity.findViewById(R.id.am_btn_stopBtAdapterListener);

        assertThat(btnOn).isNotNull();
        assertThat(btnOff).isNotNull();
        assertThat(btnStartSrv).isNotNull();
        assertThat(btnStopSrv).isNotNull();
    }

    @Test
    public void startBtAdapterListener_checkEnabledButtons_stopService_checkEnabledButtons() {
        Button btnStartSrv = (Button) mainActivity.findViewById(R.id.am_btn_startBtAdapterListener);
        Button btnStopSrv = (Button) mainActivity.findViewById(R.id.am_btn_stopBtAdapterListener);

        // проверяем состояние доступности кнопок управления сервисом слежения за состоянием БТ подключения
        assertThat(btnStartSrv).isEnabled();
        assertThat(btnStopSrv).isDisabled();
        // перед вызовом клика кнопки, подключаем мок с нужным ответом
        when(controllerMock.isBtListenerRunning()).thenReturn(true);
        // вызываем клик
        btnStartSrv.performClick();
        // проверяем был ли вызван определенный метод
        verify(controllerMock).startBtAdapterListener();
        // далее повторяем цикл для разных кнопок
        assertThat(btnStartSrv).isDisabled();
        assertThat(btnStopSrv).isEnabled();

        when(controllerMock.isBtListenerRunning()).thenReturn(false);
        btnStopSrv.performClick();
        verify(controllerMock).stopBtAdapterListener();

        assertThat(btnStartSrv).isEnabled();
        assertThat(btnStopSrv).isDisabled();
    }

    @Test
    public void startSco_checkEnabledButtons_stopSco_checkEnabledButtons() {
        // все аналогично методу startBtAdapterListener_checkEnabledButtons_stopService_checkEnabledButtons()
        Button btnOn = (Button) mainActivity.findViewById(R.id.am_btn_startSco);
        Button btnOff = (Button) mainActivity.findViewById(R.id.am_btn_stopSco);

        assertThat(btnOn).isEnabled();
        assertThat(btnOff).isDisabled();

        when(mAudioManagerMock.isBluetoothScoOn()).thenReturn(true);
        btnOn.performClick();
        verify(controllerMock).startSco();

        assertThat(btnOn).isDisabled();
        assertThat(btnOff).isEnabled();

        when(mAudioManagerMock.isBluetoothScoOn()).thenReturn(false);
        btnOff.performClick();
        verify(controllerMock).stopSco();

        assertThat(btnOn).isEnabled();
        assertThat(btnOff).isDisabled();
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Controller.class).toInstance(controllerMock);
            bind(AudioManager.class).toInstance(mAudioManagerMock);
        }
    }
}
