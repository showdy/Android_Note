package com.medisana.vpext;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 主要是争对BEHAVIOR_SET_USER_VISIBLE_HINT的懒加载
 */
public abstract class LazyFragment extends Fragment {
    private boolean mIsViewCreated = false;
    private boolean mIsUiVisible = false;
    private static final String TAG = "LazyFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsViewCreated = true;
        /**
         * 第一次进入，AFragment先创建就显示，走此处
         */
        onLazyLoad("onViewCreate");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsUiVisible = isVisibleToUser;
        if (isVisibleToUser) {
            //后面的Fragment全部走此处
            onLazyLoad("setUserVisibleHint");
        }
    }

    protected void onLazyLoad(String tag) {
        if (mIsViewCreated && mIsUiVisible && !loadCompleted()) {
            Log.d(TAG, "onLazyLoad: " + tag);
            onLoadData();
        }
    }

    protected abstract void onLoadData();

    /**
     * 子类重写改方法根据需要返回
     *
     * @return true 回调{@link LazyFragment#onLoadData()}
     */
    protected boolean loadCompleted() {
        return false;
    }


}
