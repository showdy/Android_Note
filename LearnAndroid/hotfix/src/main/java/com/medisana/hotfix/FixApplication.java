package com.medisana.hotfix;

import android.app.Application;
import android.content.Context;

import java.io.File;

public class FixApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        HotFix.installPatch(this,new File("/sdcard/patch.jar"));
    }
}
