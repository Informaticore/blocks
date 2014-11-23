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
public class LeapMotionTask extends AsyncTask<Void, Void, Void> {


    private Socket mSocket;
    private static final float alpha = 0.04f;
    private float[] thumbPositionOutput = new float[3];
    private float[] fingerPositionOutput = new float[3];

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

                    final float[] smoothThumbPositions = applyLPF(thumbPositions, thumbPositionOutput);
                    final float[] smoothFingerPositions = applyLPF(fingerPositions, fingerPositionOutput);

                    final float thumbX = smoothThumbPositions[0];
                    final float fingerX = smoothFingerPositions[0];
                    final float deltaX = thumbX - fingerX;
                    if(Math.abs(deltaX) < 50) {
                        Log.d("DEBUG", "BAM!!!");
                    }
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
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }
}
