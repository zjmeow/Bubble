package com.stonymoon.bubble.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.stonymoon.bubble.R;


public class BarView extends LinearLayout {

    public BarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.title_layout, this);
    }
}
