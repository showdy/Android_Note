package com.medisana.vpext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 解决ViewPager wrap_content无效的问题，另外构建Fragment时请使用inflate三个参数的方法
 */
public class ViewPagerExt extends ViewPager {


    public ViewPagerExt(@NonNull Context context) {
        super(context);
    }

    public ViewPagerExt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 模仿 {@link android.widget.FrameLayout#onMeasure(int, int)}测量子View
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //所有子View高度的最大值
        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            //网络上的错误做法
            //child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = child.getLayoutParams();
            int childWidthMeaSpec = getChildMeasureSpec(widthMeasureSpec,
                    getPaddingLeft() + getPaddingRight(), params.width);
            int childHeightMeaSpec = getChildMeasureSpec(heightMeasureSpec,
                    getPaddingTop() + getPaddingBottom(), params.height);
            child.measure(childWidthMeaSpec, childHeightMeaSpec);
            int h = child.getMeasuredHeight();
            if (h > height) height = h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
