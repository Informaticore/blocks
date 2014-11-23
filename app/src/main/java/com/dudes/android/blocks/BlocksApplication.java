package com.dudes.android.blocks;

import android.app.Application;
import android.content.Context;

/**
 * User: tobiasbuchholz @ PressMatrix GmbH
 * Date: 23.11.14 | Time: 16:32
 */
public class BlocksApplication extends Application {
    private static Application      mApplication;

    @Override
    public void onCreate() {
        mApplication = this;
    }

    public static Context getInstance(){
        return mApplication;
    }

}
