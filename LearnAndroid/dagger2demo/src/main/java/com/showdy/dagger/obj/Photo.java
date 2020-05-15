package com.showdy.dagger.obj;

public class Photo {


    private static final String TAG = "Photo";

    String photoType;

    public Photo(String photoType) {
        this.photoType = photoType;
    }

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photoType='" + photoType + '\'' +
                '}';
    }
}
