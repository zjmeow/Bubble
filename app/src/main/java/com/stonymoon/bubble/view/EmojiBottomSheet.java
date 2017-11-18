package com.stonymoon.bubble.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.vondear.rxtools.RxImageTool;

public class EmojiBottomSheet extends FrameLayout {
    public EmojiBottomSheet(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }


    private void initModule() {


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


}


