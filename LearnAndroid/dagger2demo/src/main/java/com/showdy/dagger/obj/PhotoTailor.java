package com.showdy.dagger.obj;


import android.content.Context;
import android.util.Log;

/**
 * 裁剪类
 */
public class PhotoTailor {

    private static final String TAG = "PhotoTailor";

    Photo mPhoto;

    String photoUrl;

    public PhotoTailor() {
    }

    public PhotoTailor(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public PhotoTailor(Photo photo) {
        this.mPhoto = photo;
    }

    public void photoTailor() {
        Log.d(TAG, "photoTailor: " + photoUrl);
    }
}
