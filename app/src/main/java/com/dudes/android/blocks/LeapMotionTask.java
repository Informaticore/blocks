package com.dudes.android.blocks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * User: mirko @ PressMatrix GmbH
 * Date: 23.11.2014 | Time: 12:28
 */
public class LeapMotionTask extends AsyncTask<Void, Float, Void> {
    private static final float  ALPHA                           = 0.04f;
    public static final float   PUBLISH_SHOT_DETECTED           = 100f;
    public static final float   PUBLISH_FINGER_MOTION_DETECTED  = 101f;

    private Socket mSocket;
    private float[] mThumbPositionOutput = new float[3];
    private float[] mFingerPositionOutput = new float[3];

    private MotionListener mListener;
    private boolean mIsShooting;

    public LeapMotionTask(final MotionListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mSocket = new Socket("10.100.220.57", 1337);
            Log.d("DEBUG", "### CONNECTED ###");

            BufferedReader input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            String line;

            while (!isCancelled()) {
                line = input.readLine();
                if (line != null) {
                    final String[] positionStrings = line.split(",");
                    final float[] thumbPositions = {Float.parseFloat(positionStrings[0]), Float.parseFloat(positionStrings[1]), Float.parseFloat(positionStrings[2])};
                    final float[] fingerPositions = {Float.parseFloat(positionStrings[3]), Float.parseFloat(positionStrings[4]), Float.parseFloat(positionStrings[5])};

                    final float[] smoothThumbPositions = applyLPF(thumbPositions, mThumbPositionOutput);
                    final float[] smoothFingerPositions = applyLPF(fingerPositions, mFingerPositionOutput);

                    final float thumbX = smoothThumbPositions[0];
                    final float fingerX = smoothFingerPositions[0];
                    final float fingerY = smoothFingerPositions[1];
                    final float deltaX = thumbX - fingerX;

                    if(Math.abs(deltaX) < 50) {
                        if(!mIsShooting) {
                            publishProgress(PUBLISH_SHOT_DETECTED, fingerX, fingerY);
                            mIsShooting = true;
                        }
                    } else {
                        mIsShooting = false;
                    }
                    publishProgress(PUBLISH_FINGER_MOTION_DETECTED, fingerX, fingerY);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private float[] applyLPF(float[] input, float[] output) {
        if (output == null)
            return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    protected void onProgressUpdate(final Float... values) {
        super.onProgressUpdate(values);
        if(values[0] == PUBLISH_SHOT_DETECTED) {
            invokeShotDetected(values[1], values[2]);
        } else {
            invokeFingerMotionDetected(values[1], values[2]);
        }
    }

    private void invokeShotDetected(final float fingerX, final float fingerY) {
        if(mListener != null) {
            mListener.onShotDetected(fingerX, fingerY);
        }
    }

    private void invokeFingerMotionDetected(final float fingerX, final float fingerY) {
        if(mListener != null) {
            mListener.onFingerMotionDetected(fingerX, fingerY);
        }
    }

    public interface MotionListener {
        public void onFingerMotionDetected(final float fingerX, final float fingerY);
        public void onShotDetected(final float fingerX, final float fingerY);
    }
}
