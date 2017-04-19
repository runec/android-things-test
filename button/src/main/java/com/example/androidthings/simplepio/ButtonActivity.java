/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.simplepio;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sample usage of the Gpio API that logs when a button is pressed.
 *
 */
public class ButtonActivity extends Activity {
    private static final String TAG = ButtonActivity.class.getSimpleName();

    private Gpio mButtonGpio;

    private List<Date> presses;

    public ButtonActivity(){
        this.presses= new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting ButtonActivity");
        Log.i(TAG, TAG);
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String baseUrl = "http://requestb.in/zqgdobzq";

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            String pinName = BoardDefaults.getGPIOForButton();
            mButtonGpio = service.openGpio(pinName);
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mButtonGpio.registerGpioCallback(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    Log.i(TAG, "GPIO changed, button pressed");
                    presses.add(new Date());
                    System.out.println(presses);
                    System.out.println(presses.size());
                    System.out.println("Jesper");
                    // Request a string response from the provided URL.
                    String url = baseUrl + "?time=" + new Date().toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    Log.i(TAG, "Response is: "+ response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "That didn't work!");
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    // Return true to continue listening to events
                    return true;
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mButtonGpio != null) {
            // Close the Gpio pin
            Log.i(TAG, "Closing Button GPIO pin");
            try {
                mButtonGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            } finally {
                mButtonGpio = null;
            }
        }
    }
}
