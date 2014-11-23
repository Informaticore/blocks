package com.dudes.android.blocks;

import android.animation.*;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.plattysoft.leonids.ParticleSystem;

import java.util.Random;


public class MainActivity extends Activity implements Game.OnShootListener {

    private RelativeLayout mContainer;
    private Random mRandom;
    private DisplayMetrics mMetrics;
    private Game mGame;

    private int[] mCubes = {R.drawable.cube001,R.drawable.cube002,R.drawable.cube003};
    private Rect mCubeRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRandom = new Random();
        mMetrics = getResources().getDisplayMetrics();

        initBackground();
        initGame();
        new LeapMotionTask().execute();
    }

    private void initGame() {
        mGame = (Game) findViewById(R.id.game);
        mGame.setOnShootListener(this);
    }

    private void initBackground() {
        mContainer = (RelativeLayout) findViewById(R.id.container);

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
        addAnimatedCube();
    }

    private void addAnimatedCloud(){
        final ImageView cloud = new ImageView(this);
        cloud.setImageResource(R.drawable.cloud_001);
        mContainer.addView(cloud);
        cloud.setTranslationX(mRandom.nextInt(mMetrics.widthPixels));
        cloud.setTranslationY(-(50 * mMetrics.density));
        cloud.setScaleY(0.0f);
        cloud.setScaleX(0.0f);
        cloud.setAlpha(0.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(cloud, "alpha", 0.0f);
        alpha.setDuration(500);
        alpha.setStartDelay(2500);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cloud, "scaleX", 5.0f);
        ObjectAnimator sclaeY = ObjectAnimator.ofFloat(cloud, "scaleY", 5.0f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(cloud, "translationY", mMetrics.heightPixels + (500 * mMetrics.density));
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

    private void addAnimatedCube(){
        mCubeRect = new Rect();
        final ImageView cube = new ImageView(this);
        cube.setImageResource(mCubes[mRandom.nextInt(3)]);
        mContainer.addView(cube, mContainer.getChildCount() - 1);
        cube.setTranslationX(mRandom.nextInt(mMetrics.widthPixels));
        cube.setTranslationY(0);
        cube.setScaleY(0.0f);
        cube.setScaleX(0.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cube, "scaleX", 5.0f);
        ObjectAnimator sclaeY = ObjectAnimator.ofFloat(cube, "scaleY", 5.0f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(cube, "rotation", 100 + mRandom.nextInt(260));
        ObjectAnimator translationY = ObjectAnimator.ofFloat(cube, "translationY", mMetrics.heightPixels + (200 * mMetrics.density));
        translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCubeRect.set((int)(cube.getTranslationX()), (int)(cube.getTop() + cube.getTranslationY()), (int) (cube.getRight() + cube.getTranslationX()), (int)(cube.getBottom() + cube.getTranslationY()));
            }
        });
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(3000);
        animSet.playTogether(scaleX, sclaeY, translationY, rotation);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mContainer.removeView(cube);
                addAnimatedCube();
            }
        });
        animSet.start();
    }

    public void onClickSomething(View view) {

    }

    @Override
    public void onShoot(int x, int y) {
        Log.d("DEBUG", " :: onShoot :: x:" + x + " y:"+ y);
        Log.d("DEBUG", " :: onShoot :: " + mCubeRect);
        if(mCubeRect.contains(x,y)){
            new ParticleSystem(this, 20, R.drawable.smallcubes, 1000)
                    .setSpeedRange(0.5f, 0.8f)
                    .setInitialRotationRange(0, 360)
                    .setRotationSpeed(144)
                    .setRotationSpeedRange(0.5f, 0.8f)
                    .setFadeOut(200)
                    .oneShot(mContainer, 50);
        }
    }
//
//    private boolean checkCollision(Rect asteroidBounds, ImageView asteroid) {
////        asteroidBounds.set((int)(asteroid.getTranslationX()), (int)(asteroid.getTop() + asteroid.getTranslationY()), (int) (asteroid.getRight() + asteroid.getTranslationX()), (int)(asteroid.getBottom() + asteroid.getTranslationY()));
////        mSpaceShipBounds.set((int) mSpaceShip.getTranslationX(), (int) (mSpaceShip.getTranslationY()), (int) (mSpaceShip.getRight() + mSpaceShip.getTranslationX()), (int) ((mSpaceShip.getBottom() + mSpaceShip.getTranslationY()) - (28 * mMetrics.density)));
////        return asteroidBounds.intersect(mSpaceShipBounds);
////        return false;
//    }

}
