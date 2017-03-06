package com.olafurtorfi.www.podcastmarket.activity;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.olafurtorfi.www.podcastmarket.PlayerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by olitorfi on 27/02/2017.
 * not working test...
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlayerActivityTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            PlayerActivity.class);

    @Test
    public void getMediaFile(){
//        Context context = InstrumentationRegistry.getTargetContext();
//        PlayerActivity playerActivity = new PlayerActivity();
//        playerActivity.getMediaFile("testpath.mp3");
    }
}
