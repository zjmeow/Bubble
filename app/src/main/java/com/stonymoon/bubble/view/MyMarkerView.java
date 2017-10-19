package com.stonymoon.bubble.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * 拓展TextView，使它显示出气泡
 */
public class MyMarkerView extends View {

    public MyMarkerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = measureWidth(widthMeasureSpec);
        heightMeasureSpec = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureWidth(int widthMeasureSpec) {
        return 50;


    }

    private int measureHeight(int widthMeasureSpec) {
        return 50;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        int length = getWidth();
        int circleXY = length / 2;
        double radius = length / 2;

        super.onDraw(canvas);
    }
}
