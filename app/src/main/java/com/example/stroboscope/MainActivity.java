package com.example.stroboscope;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    /**
     * Stroboscope is an application emulating stroboscopic light from camera flashlight.
     *
     * @author Piotr Dymala p.dymala@gmail.com
     * @version 1.0
     * @since 2020-12-15
     */


    //Code for turning on/off flashlight https://stackoverflow.com/questions/6068803/how-to-turn-on-front-flash-light-programmatically-in-android
    //Fonts: fontjoy.com  Pattern: patternpad.com  Icon: images.google.com + Corel photo-paint

    SeekBar seekBarFrequency;

    Switch aSwitch;
    TextView textViewMilisec;
    Handler handler = new Handler();
    private boolean isRunning = false;
    private int initialTimeDelay = 5;
    private int timeDelay = initialTimeDelay;
    private int timeRunning = 5;
    FlashlightProvider flp = new FlashlightProvider(MainActivity.this);
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = findViewById(R.id.switch1);
        textViewMilisec = findViewById(R.id.textViewMilisec);

        // set a change listener on the SeekBar
        seekBarFrequency = findViewById(R.id.seekBarFrequency);
        seekBarFrequency.setOnSeekBarChangeListener(seekBarFrequencyChangeListener);


    }


    SeekBar.OnSeekBarChangeListener seekBarFrequencyChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            //tvProgressLabel.setText("Progress: " + progress);


            timeDelay = initialTimeDelay + progress * 2;


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class Strobe implements Runnable {

        @Override
        public void run() {


                while (isRunning) {
                    flp.turnFlashlightOn();
                    long start = System.currentTimeMillis();

                    try {
                        Thread.sleep(timeRunning);
                    } catch (InterruptedException e) {
                        //  e.printStackTrace();
                    }
                    flp.turnFlashlightOff();
                    try {
                        Thread.sleep(timeDelay);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }

                    long elapsedTimeMillis = System.currentTimeMillis() - start;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textViewMilisec.setText(String.valueOf(elapsedTimeMillis));

                        }
                    });




            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void turnOnOff(View view) {

        if (!flp.hasFlash()) {

            Toast.makeText(MainActivity.this
                    , "No flashlight detected", Toast.LENGTH_SHORT).show();

        } else {

        if (!isRunning) {
            isRunning = true;
            Strobe strobe = new Strobe();
            thread = new Thread(strobe);
            thread.start();
        } else {

            isRunning = false;
            thread.interrupt();
        }

    }}

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null && thread.isAlive()) {
            isRunning = false;
            thread.interrupt();

        }
        if (aSwitch.isChecked()) {
            aSwitch.setChecked(false);
        }
    }

    public void closeApp(View view) {
        this.finishAffinity();

    }

}