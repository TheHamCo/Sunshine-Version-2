package com.example.android.sunshine.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mdd23 on 7/14/2016.
 */
public class MyView extends View {
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY){
            myHeight = hSpecSize;
        } else if (hSpecMode == MeasureSpec.AT_MOST){

        }

        if (wSpecMode == MeasureSpec.EXACTLY){
            myWidth = wSpecSize;
        } else if (wSpecMode == MeasureSpec.AT_MOST){

        }

        setMeasuredDimension(myWidth, myHeight);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
