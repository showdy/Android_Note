package com.showdy.component;

import android.content.Context;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

public class LocationFragment extends Fragment {

    private static final String TAG = "LocationFragment";

    @Inject
    LocationProvider mLocationProvider;


    private LocationActivity mActivity;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mActivity = (LocationActivity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(mActivity);
        textView.setText("Hello world");
        return textView;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity.fragmentComponent.inject(this);

        Log.d(TAG, "onViewCreated: " + mLocationProvider.hashCode());
    }
}
