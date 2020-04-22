package com.medisana.intecerptdemo.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

/**
 * 实现滑动的三种方式：
 * 1. scroller
 * <p>
 * 2. view的平移动画
 * <p>
 * 3. LaoutParams使得View重新布局实现滑动
 */
public class ScrollerUtils {


    public static void scrollByView(View targetView) {
        TranslateAnimation animation = new TranslateAnimation(0, 200, 0, 200);
        animation.setDuration(300);
        //动画结束后不要回到起点
        animation.setFillAfter(true);
        targetView.setAnimation(animation);
        animation.start();
    }

    public static void scrollByProperty(View target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationX", 0, 200);
        animator.setDuration(300);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取比例
                float fraction = animation.getAnimatedFraction();
                //...
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }


    public static void scrollByScroller(final View target, final int deltaX) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                //每次滑动一定的比例，实现弹性滑动
                target.scrollBy((int) (deltaX * fraction), 0);
            }
        });
        animator.start();
    }


    public static void scrollByLayoutParams(final View target, final int start, final int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                ViewGroup.LayoutParams params = target.getLayoutParams();
                params.height += (end - start) * fraction;
                target.requestLayout();
                //target.setLayoutParams(params);
            }
        });
        animator.setDuration(2000);
        animator.start();

    }
}
