package com.showdy.dagger.obj;


import android.content.Context;
import android.util.Log;

/**
 * 拍照类
 */
public class PhotoTaker {

    private static final String TAG = "PhotoTaker";

    Context mContext;

    public PhotoTaker(Context context) {
        mContext = context;
    }

    public void takePhoto() {
        Log.d(TAG, "takePhoto: " + mContext);
    }
}
