package com.androhi.lchikaandroidthings;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class BlinkingActivity extends AppCompatActivity {

    private static final int INTERVAL_BETWEEN_BLINK_MS = 500;

    private Handler blinkingLedHandler = new Handler();
    private Gpio ledGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinking);

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            String gpioPinName = BoardDefaults.getGPIOForLED();
            ledGpio = service.openGpio(gpioPinName);
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            throw new RuntimeException("Problem connecting to IO Port", e);
        }

        blinkingLedHandler.post(blinkingRunnable);
    }

    private Runnable blinkingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                ledGpio.setValue(!ledGpio.getValue());
                blinkingLedHandler.postDelayed(blinkingRunnable, INTERVAL_BETWEEN_BLINK_MS);
            } catch (IOException e) {
                Log.e("BlinkActivity", "Error on PeripheralIO API", e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ledGpio.close();
        } catch (IOException e) {
            Log.e("BlinkActivity", "Error on PeripheralIO API", e);
        }
    }
}
