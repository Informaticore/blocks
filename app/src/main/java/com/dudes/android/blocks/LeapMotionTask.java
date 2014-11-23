package com.dudes.android.blocks;

import android.os.AsyncTask;

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
    private float[] positionOutput = new float[3];
    private float[] rotationOutput = new float[3];

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mSocket = new Socket("192.168.0.25", 1337);

            BufferedReader input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            String line = "";

            while (isCancelled()) {
                line = input.readLine();
                if (line.contains(":")) {
                    String[] rots = line.split(":")[0].split(",");
                    String[] vals = line.split(":")[1].split(",");
                    float[] inputPosition = {Float.parseFloat(vals[0]),
                            Float.parseFloat(vals[1]),
                            Float.parseFloat(vals[2])};
                    float[] smoothVals = applyLPF(inputPosition, positionOutput);

                    inputPosition = new float[]{
                            Float.parseFloat(rots[0]),
                            Float.parseFloat(rots[1]),
                            Float.parseFloat(rots[2])};

                    float[] smoothRots = applyLPF(inputPosition, rotationOutput);
                    float x = smoothVals[0];
                    float y = smoothVals[1];

                    if (y < 200) {
                        y = 200 - y;
                    } else {
                        y = y - 200;
                    }

                    float z = smoothVals[2];

                    float[] obj = {x, y, z, smoothRots[0]};
//                    mMessenger.send(Message.obtain(null, MSG_SET_VALUE, obj));

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
