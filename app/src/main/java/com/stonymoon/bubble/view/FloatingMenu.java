package com.stonymoon.bubble.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.stonymoon.bubble.R;


public class FloatingMenu extends FrameLayout {
    private ImageView ivMenu;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private float moveDistance;

    private boolean isOpen = false;

    public FloatingMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.floating_menu, this);
        ivMenu = (ImageView) findViewById(R.id.iv_floating_menu_menu);
        iv1 = (ImageView) findViewById(R.id.iv_floating_menu_1);
        iv2 = (ImageView) findViewById(R.id.iv_floating_menu_2);
        iv3 = (ImageView) findViewById(R.id.iv_floating_menu_3);
        ivMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    close();
                    isOpen = false;
                } else {
                    open();
                    isOpen = true;
                }
            }
        });


    }

    private void open() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                iv1, "translationY",
                -moveDistance);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                iv2, "translationY",
                -moveDistance * 2);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(
                iv3, "translationY",
                -moveDistance * 3);
        ObjectAnimator animator0 = ObjectAnimator.ofFloat(
                ivMenu, "alpha",
                0.5F);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.setInterpolator(new BounceInterpolator());
        set.playTogether(animator0, animator1, animator2, animator3);
        set.start();

    }

    private void close() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                iv1, "translationY",
                0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                iv2, "translationY",
                0);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(
                iv3, "translationY",
                0);
        ObjectAnimator animator0 = ObjectAnimator.ofFloat(
                ivMenu, "alpha",
                1F);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.setInterpolator(new BounceInterpolator());
        set.playTogether(animator0, animator1, animator2, animator3);
        set.start();

    }

    public void setMoveDistance(float distance) {
        moveDistance = distance;
    }

    public void setOnclickListener(final OnPictureClickListener listener) {
        iv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFirstClick();
            }
        });
        iv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSecondClick();
            }
        });
        iv3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onThirdClick();
            }
        });

    }

    public interface OnPictureClickListener {
        void onFirstClick();

        void onSecondClick();

        void onThirdClick();

    }

}
