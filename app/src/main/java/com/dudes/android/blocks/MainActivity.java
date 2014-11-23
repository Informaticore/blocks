package com.dudes.android.blocks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;


public class MainActivity extends Activity {

    private RelativeLayout mContainer;
    private Random mRandom;
    private DisplayMetrics mMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRandom = new Random();
        mMetrics = getResources().getDisplayMetrics();

        initBackground();

        new LeapMotionTask().execute();
    }

    private void initBackground() {
        mContainer = (RelativeLayout) findViewById(R.id.container);
//        LayerDrawable background = (LayerDrawable) mContainer.getBackground();
//        AnimationDrawable drawable = (AnimationDrawable) background.findDrawableByLayerId(R.id.stars);
//        drawable.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(int i = 1; i <= 10; i++){
            mContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addAnimatedCloud();
                }
            }, 600 * i);
        }
    }

    private void addAnimatedCloud(){
        final ImageView cloud = new ImageView(this);
        cloud.setImageResource(R.drawable.cloud_001);
        mContainer.addView(cloud);
        cloud.setTranslationX(mRandom.nextInt(mMetrics.widthPixels));
        cloud.setScaleY(0.0f);
        cloud.setScaleX(0.0f);
        cloud.setAlpha(0.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(cloud, "alpha", 0.0f);
        alpha.setDuration(500);
        alpha.setStartDelay(2500);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cloud, "scaleX", 5.0f);
        ObjectAnimator sclaeY = ObjectAnimator.ofFloat(cloud, "scaleY", 5.0f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(cloud, "translationY", mMetrics.heightPixels);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(3000);
        animSet.playTogether(alpha, scaleX, sclaeY, translationY);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mContainer.removeView(cloud);
                addAnimatedCloud();
            }
        });
        animSet.start();
    }

}
