package com.medisana.vpext;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @see {https://blog.csdn.net/qq_36486247/article/details/102531304}
 */
public abstract class LifecycleLazyFragment extends Fragment {


    private boolean isLoaded = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isLoaded) {
            //简单的处理，实际情况可能更复杂，需要在回调中判断是否已经成功加载
            isLoaded = true;
            loadFragmentData();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    // FragmentPageAdpater不会调用OnDestroy(),但会执行该方法，根据需要重置标记
    // 这样重新预加载会重新加载数据
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoaded = false;
    }

    protected abstract void loadFragmentData();

}
