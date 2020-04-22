package com.medisana.intecerptdemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";
    private int mHorizontalSpacing = dp2px(16); //每个item横向间距
    private int mVerticalSpacing = dp2px(8); //每个item横向间距

    private List<List<View>> allLines; // 记录所有的行，一行一行的存储
    List<Integer> lineHeights = new ArrayList<>(); // 记录每一行的行高

    public FlowLayout(Context context) {
        super(context);
//        initMeasureParams();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        initMeasureParams();
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initMeasureParams();
    }

    private void initMeasureParams() {
        allLines = new ArrayList<>();
        lineHeights = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initMeasureParams();
         //度量子view的大小
        //度量所有的子View
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);  //ViewGroup解析的宽度
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec); // ViewGroup解析的高度

        List<View> lineViews = new ArrayList<>(); //保存一行中的所有的view
        int lineWidthUsed = 0; //记录这行已经使用了多宽的size
        int lineHeight = 0; // 一行的行高

        int parentNeededWidth = 0;  // measure过程中，子View要求的父ViewGroup的宽
        int parentNeededHeight = 0; // measure过程中，子View要求的父ViewGroup的高

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            LayoutParams childLP = childView.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                    childLP.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                    childLP.height);

            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            //获取子view的宽高
            int childMesauredWidth = childView.getMeasuredWidth();
            int childMeasuredHeight = childView.getMeasuredHeight();

            //通过宽度来判断是否需要换行，通过换行后的每行的行高来获取整个viewGroup的行高
            //如果需要换行
            if (childMesauredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                allLines.add(lineViews);
                lineHeights.add(lineHeight);

                //一旦换行，我们就可以判断当前行需要的宽和高了，所以此时要记录下来
                parentNeededHeight = parentNeededHeight + lineHeight + mVerticalSpacing;
                parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);

                lineViews = new ArrayList<>();
                lineWidthUsed = 0;
                lineHeight = 0;
            }
            // view 是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
            lineViews.add(childView);
            //每行都会有自己的宽和高
            lineWidthUsed = lineWidthUsed + childMesauredWidth + mHorizontalSpacing;
            lineHeight = Math.max(lineHeight, childMeasuredHeight);

            //如果当前childView是最后一行的最后一个
            if (i == childCount - 1) {//最后一行
                lineHeights.add(lineHeight);
                allLines.add(lineViews);
                parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed);
                parentNeededHeight += lineHeight;
            }

        }



        //根据子View的度量结果，来重新度量自己ViewGroup
        // 作为一个ViewGroup，它自己也是一个View,它的大小也需要根据它的父亲给它提供的宽高来度量
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth: parentNeededWidth;
        int realHeight = (heightMode == MeasureSpec.EXACTLY) ?selfHeight: parentNeededHeight;

        setMeasuredDimension(realWidth,realHeight);

    }

    /*
    将所有的子View布局到屏幕上，同时，由于FlowLayout自己没有特殊要求所以不需要对自己布局
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

//        int viewCount = getChildCount();
//        for (int i = 0; i < viewCount; i++){
//            View view = getChildAt(i);
//            view.layout(view.getLeft(),view.getTop(),view.getRight(),view.getBottom());
//        }
        int lineCount = allLines.size();
        int curL = 0;
        Log.d(TAG, "onLayout: getLeft(): " + getLeft());
        int curT = 0;
        Log.d(TAG, "onLayout: getTop(): " + getTop());
        for (int i = 0; i < lineCount; i++) {
            List<View> lineViews = allLines.get(i);
            int lineHeight = lineHeights.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View view = lineViews.get(j);
                int left = curL;
                int top = curT;
//                int bottom = top + view.getHeight();
//                int right = left + view.getWidth();

                int bottom = top + view.getMeasuredHeight();
                int right = left + view.getMeasuredWidth();
                view.layout(left,top, right,bottom);
                curL = right + mHorizontalSpacing;
            }
            curL = 0;
            curT = curT + lineHeight + mVerticalSpacing;
        }
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
