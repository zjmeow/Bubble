package com.stonymoon.bubble.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.stonymoon.bubble.R;


public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    public void initModel() {
        ImageView imageView = (ImageView) findViewById(R.id.iv_bottom_sheet_big_emoji);
        imageView.bringToFront();

    }

}
