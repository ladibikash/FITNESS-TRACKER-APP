package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements  SensorEventListener{
    private TextView stepCountTextView;
    private  TextView distanceTextView;
    private  TextView timeTextView;
    private Button pauseButton;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private ProgressBar progressBar;
    private boolean isPaused=false;
    private long timePaused;
    private  float stepLengthInMeter=0.762f;
    private  long  startTime;
    private int stepCountTarget=5000;
    private  TextView  stepCountTargetTextView;
    private Handler timerHandler=new Handler();
    private  Runnable timerRunnable=new Runnable() {
        private Object Locale;

        @Override
        public void run() {
            long milis=System.currentTimeMillis() - startTime;
            int seconds=(int)(milis/1000);
            int min =seconds/60;
            seconds=seconds%0;

            timeTextView.setText(String.format(String.valueOf(Locale.getClass()),"Time: %02d:%02d", min, seconds));
            timerHandler.postDelayed(this,1000);



        }
    };

    @Override
    protected void onStop(){

        super.onStop();
        if(stepCounterSensor!=null){
            sensorManager.unregisterListener(this);
            timerHandler.removeCallbacks(timerRunnable);
        }
    }




    @Override
    protected  void onResume(){
        super.onResume();

        if (stepCounterSensor !=null){
            sensorManager.registerListener((SensorEventListener) this,stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            timerHandler.postDelayed(timerRunnable,0);
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        stepCountTextView=findViewById(R.id.stepCounterTargetTextView);
        distanceTextView=findViewById(R.id.distanceTextView);
        timeTextView=findViewById(R.id.timeTextView);
        pauseButton=findViewById(R.id.pauseButton);
        stepCountTargetTextView=findViewById(R.id.stepCounterTargetTextView);
        progressBar=findViewById(R.id.progressBar);

        startTime=System.currentTimeMillis();


        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        stepCounterSensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(stepCountTarget);
        stepCountTargetTextView.setText("Step Goal" + stepCountTarget);
        if(stepCounterSensor==null){
            stepCountTextView.setText("Step Counter not available") ;

        }



    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorAdditionalInfo sensorEvent = null;
        if(sensorEvent.sensor.getType()==Sensor.TYPE_STEP_COUNTER){
            stepCount=(int) sensorEvent.intValues[0];
            stepCountTextView.setText("Step Count:"  +stepCount);
            progressBar.setProgress(stepCount);

            if(stepCount>= stepCountTarget){
                stepCountTargetTextView.setText("Stop Goal Achived");
            }
            float distanceInkm=stepCount * stepLengthInMeter/1000;
            distanceTextView.setText(String.format(Locale.getDefault(),"Distance:%.2f km",distanceInkm));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public  void setPauseButtononClicked() {
        setPauseButtononClicked(null);
    }

    public  void setPauseButtononClicked(View view){
        if(isPaused){
            isPaused=false;
            pauseButton.setText("pause");
            startTime=System.currentTimeMillis()- timePaused;
            timerHandler.postDelayed(timerRunnable,0);

        }else {

            isPaused=true;
            pauseButton.setText("Resume");
            timerHandler.removeCallbacks(timerRunnable);
            timePaused=System.currentTimeMillis()-startTime;
        }
    }
}