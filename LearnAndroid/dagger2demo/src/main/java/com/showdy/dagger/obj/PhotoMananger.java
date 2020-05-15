package com.showdy.dagger.obj;

public class PhotoMananger {

    PhotoTaker mTaker;

    PhotoTailor mTailor;

    public PhotoMananger(PhotoTaker taker, PhotoTailor tailor) {
        mTaker = taker;
        mTailor = tailor;
    }

    public void startMethod() {

        mTaker.takePhoto();

        mTailor.photoTailor();
    }


    public PhotoTaker getTaker() {
        return mTaker;
    }

    public PhotoTailor getTailor() {
        return mTailor;
    }
}
