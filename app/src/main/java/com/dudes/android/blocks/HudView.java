package com.dudes.android.blocks;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * User: mirko @ PressMatrix GmbH
 * Date: 23.11.2014 | Time: 11:18
 */
public class HudView extends View {

    private final Bitmap mHud;
    private float mX;
    private float mY;
    private Paint mThingPaint;
    private OnShootListener mListener;

    public HudView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mThingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThingPaint.setColor(Color.RED);
        mThingPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mHud = BitmapFactory.decodeResource(getResources(), R.drawable.hud);
    }

    public void setOnShootListener(final OnShootListener listener){
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mHud, mX, mY, mThingPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                mX = event.getX() - mHud.getWidth() /2;
                mY = event.getY() - mHud.getHeight() /2;
                if(mListener != null){
                    mListener.onShoot((int)event.getX(), (int)event.getY());
                }
                invalidate();
                return false;
        }
        return super.onTouchEvent(event);
    }

    public void setPosition(final float x, final float y) {
        mX = x - mHud.getWidth() /2;
        mY = y - mHud.getHeight() /2;
        invalidate();
    }

    public interface OnShootListener{
        public void onShoot(int x, int y);
    }
}
