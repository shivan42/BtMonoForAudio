package com.shivandev.btmonoforaudio;

import android.widget.Button;

import com.google.inject.AbstractModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import roboguice.RoboGuice;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class ActivityTest {

    private MainActivity mainActivity;
    private Controller controllerMock = mock(Controller.class);

    @Before
    public  void setUp() {
        RoboGuice.overrideApplicationInjector(Robolectric.application, new MyTestModule());
        when(controllerMock.isServiceRunning(BtListenerSrv.class.getName())).thenReturn(false);
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
    }

    @Test
    public void shouldNotBeNull() {
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
    public void testClickAndEnableButtons() {
        Button btnOn = (Button) mainActivity.findViewById(R.id.am_btn_startSco);
        Button btnOff = (Button) mainActivity.findViewById(R.id.am_btn_stopSco);
        Button btnStartSrv = (Button) mainActivity.findViewById(R.id.am_btn_startBtAdapterListener);
        Button btnStopSrv = (Button) mainActivity.findViewById(R.id.am_btn_stopBtAdapterListener);

        // todo надо изучить как инжектить controller потом как в тесте его подменить на mock и определить через mockito stub выдающий нужные мне ответы в методе проверяющем запущен ли сервис

        assertThat(btnStartSrv).isEnabled();
        assertThat(btnStopSrv).isDisabled();
        when(controllerMock.isServiceRunning(BtListenerSrv.class.getName())).thenReturn(true);
        btnStartSrv.performClick();

        assertThat(btnStartSrv).isDisabled();
        assertThat(btnStopSrv).isEnabled();
        when(controllerMock.isServiceRunning(BtListenerSrv.class.getName())).thenReturn(false);
        btnStopSrv.performClick();
        assertThat(btnStartSrv).isEnabled();
        assertThat(btnStopSrv).isDisabled();
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Controller.class).toInstance(controllerMock);
        }
    }
}
