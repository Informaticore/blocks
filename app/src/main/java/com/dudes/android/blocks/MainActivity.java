package com.dudes.android.blocks;

import android.animation.*;
import android.app.Activity;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.plattysoft.leonids.ParticleSystem;

import java.util.Random;


public class MainActivity extends Activity implements HudView.OnShootListener, LeapMotionTask.MotionListener {

    private RelativeLayout mContainer;
    private Random mRandom;
    private DisplayMetrics mMetrics;
    private HudView mHudView;

    private int[] mCubes = {R.drawable.cube001,R.drawable.cube002,R.drawable.cube003};
    private Rect mCubeRect;
    private ImageView mCube;
    private View mHitAnimationView;
    private AnimatorSet mCubeAnimSet;
    private boolean mIsCubeShot;
    private MediaPlayer mMediaPlayer;

    private SoundEffectHelper mSoundEffectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRandom = new Random();
        mMetrics = getResources().getDisplayMetrics();
        mSoundEffectHelper = new SoundEffectHelper();

        initBackground();
        initGame();
        executeLeapMotionTask();

        mMediaPlayer = MediaPlayer.create(this, R.raw.music2);
        mMediaPlayer.setLooping(true);
    }


    private void initGame() {
        mHudView = (HudView) findViewById(R.id.hudview);
        mHudView.setOnShootListener(this);
    }

    private void initBackground() {
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mHitAnimationView = new View(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(50, 50);
        mHitAnimationView.setLayoutParams(layoutParams);
        mContainer.addView(mHitAnimationView);
    }

    private void executeLeapMotionTask() {
        new LeapMotionTask(this).execute();
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
        mMediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
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
        mCube = new ImageView(this);
        mCube.setImageResource(mCubes[mRandom.nextInt(3)]);
        mContainer.addView(mCube, mContainer.getChildCount() - 1);
        mCube.setTranslationX(mRandom.nextInt(mMetrics.widthPixels));
        mCube.setTranslationY(0);
        mCube.setScaleY(0.0f);
        mCube.setScaleX(0.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCube, "scaleX", 5.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCube, "scaleY", 5.0f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mCube, "rotation", 100 + mRandom.nextInt(260));
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mCube, "translationY", mMetrics.heightPixels + (200 * mMetrics.density));
        translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCubeRect.set((int)(mCube.getTranslationX()), (int)(mCube.getTop() + mCube.getTranslationY()), (int) ((mCube.getRight() * mCube.getScaleX()) + mCube.getTranslationX()), (int)((mCube.getBottom()* mCube.getScaleX()) + mCube.getTranslationY()));
            }
        });
        mCubeAnimSet = new AnimatorSet();
        mCubeAnimSet.setDuration(6000);
        mCubeAnimSet.playTogether(scaleX, scaleY, translationY, rotation);
        mCubeAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mContainer.removeView(mCube);
                addAnimatedCube();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!mIsCubeShot) {
                    mSoundEffectHelper.playWushSound();
                }
            }
        }, 4000);

        mCubeAnimSet.start();
    }

    @Override
    public void onShoot(int x, int y) {
        Log.d("DEBUG", " :: onShoot :: x:" + x + " y:"+ y);
        Log.d("DEBUG", " :: onShoot :: " + mCubeRect);
        if(mCubeRect.contains(x,y)){
            mHitAnimationView.setTranslationX(x);
            mHitAnimationView.setTranslationY(y);
            new ParticleSystem(this, 20, R.drawable.smallcubes, 1000)
                    .setSpeedRange(0.5f, 0.8f)
                    .setInitialRotationRange(0, 360)
                    .setRotationSpeed(144)
                    .setRotationSpeedRange(0.5f, 0.8f)
                    .setFadeOut(200)
                    .oneShot(mHitAnimationView, 50);
            mCubeAnimSet.cancel();
        }
    }

    @Override
    public void onFingerMotionDetected(final float fingerX, final float fingerY) {
        final float x = convertFingerXToScreenX(fingerX);
        final float y = convertFingerYToScreenY(fingerY);
        mHudView.setPosition(x, y);
    }

    private float convertFingerXToScreenX(final float fingerX) {
        final int screenWidth = mMetrics.widthPixels;
        final int factorX = screenWidth / 200;
        return screenWidth / 2 + fingerX * factorX;
    }

    private float convertFingerYToScreenY(final float fingerY) {
        final int screenHeight = mMetrics.heightPixels;
        final int factorY = screenHeight / 300;
        return screenHeight - fingerY * factorY;
    }

    @Override
    public void onShotDetected(final float fingerX, final float fingerY) {
        final int x = (int) convertFingerXToScreenX(fingerX);
        final int y = (int) convertFingerYToScreenY(fingerY);
        mSoundEffectHelper.playShootSound();

        if(mCubeRect.contains(x,y)){
            mHitAnimationView.setTranslationX(x);
            mHitAnimationView.setTranslationY(y);
            new ParticleSystem(this, 20, R.drawable.smallcubes, 1000)
                    .setSpeedRange(0.5f, 0.8f)
                    .setInitialRotationRange(0, 360)
                    .setRotationSpeed(144)
                    .setRotationSpeedRange(0.5f, 0.8f)
                    .setFadeOut(200)
                    .oneShot(mHitAnimationView, 50);
            mCubeAnimSet.cancel();
            mSoundEffectHelper.playYaySound();
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
