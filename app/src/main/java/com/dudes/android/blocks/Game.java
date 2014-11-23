package com.dudes.android.blocks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * User: mirko @ PressMatrix GmbH
 * Date: 23.11.2014 | Time: 11:18
 */
public class Game extends View {

    private float mThingX;
    private float mThingY;
    private Paint mThingPaint;

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);

        mThingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThingPaint.setColor(Color.RED);
        mThingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mThingX, mThingY, 30, mThingPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                mThingX = event.getX();
                mThingY = event.getY();
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
