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
public class Game extends View {

    private final Bitmap mHud;
    private float mThingX;
    private float mThingY;
    private Paint mThingPaint;

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);

        mThingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThingPaint.setColor(Color.RED);
        mThingPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mHud = BitmapFactory.decodeResource(getResources(), R.drawable.hud);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawCircle(mThingX, mThingY, 30, mThingPaint);
        canvas.drawBitmap(mHud, mThingX , mThingY, mThingPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                mThingX = event.getX() - mHud.getWidth() /2;
                mThingY = event.getY() - mHud.getHeight() /2;
                invalidate();
                return false;
        }
        return super.onTouchEvent(event);
    }
}