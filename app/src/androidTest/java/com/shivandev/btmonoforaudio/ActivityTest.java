package com.shivandev.btmonoforaudio;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class ActivityTest {

    private MainActivity mainActivity;

    @Before
    public  void setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
    }

    @Test
    public void shouldNotBeNull() {
        assertThat(mainActivity).isNotNull();
    }
}
