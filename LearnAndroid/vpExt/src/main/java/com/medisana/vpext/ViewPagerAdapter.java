package com.medisana.vpext;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.medisana.vpext.fragment.AFragment;
import com.medisana.vpext.fragment.BFragment;
import com.medisana.vpext.fragment.CFragment;
import com.medisana.vpext.fragment.DFragment;
import com.medisana.vpext.fragment.EFragment;

class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"A", "B", "C", "D", "E"};

    private Fragment[] mFragments = new Fragment[]{new AFragment(), new BFragment(),
            new CFragment(), new DFragment(), new EFragment()};


    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        //如果此处调用super(fm)，会导致预加载的fragment提前resume，可以回调setUserVisibleHint
        super(fm, FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
        // BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 时使用LifeCycle,预加载的fragment仅仅走到start
        //super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


}
