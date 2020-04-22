package com.medisana.intecerptdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 实现一个textview随这手指滑动的动画效果
 */
public class TestButton extends AppCompatTextView {

    private static final String TAG = "TestButton";

    int scaledTouchSlop;
    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    public TestButton(Context context) {
        super(context);
        init();
    }

    public TestButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        Log.d(TAG, "init: " + scaledTouchSlop);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int rawX = (int) event.getRawX();
        int rawy = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = rawX - mLastX;
                int deltaY = rawy - mLastY;
                int transX = (int) (this.getTranslationX() + deltaX);
                int transY = (int) (this.getTranslationY() + deltaY);
                this.setTranslationX(transX);
                this.setTranslationY(transY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX = rawX;
        mLastY = rawy;
        return super.onTouchEvent(event);
    }
}
