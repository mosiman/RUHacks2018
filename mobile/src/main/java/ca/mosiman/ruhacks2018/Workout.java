package ca.mosiman.ruhacks2018;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Workout extends AppCompatActivity implements SensorEventListener{
    public int numberSteps = 0;
    TextView tv_numSteps;
    TextView tv_runningAvgSteps;
    SensorManager sensorManager;

    final int TIME_WINDOW = 10;
    int runningTotalSteps = 0;
    float runningAvgSteps = 0;

    // stuff for handlers
    Handler h = new Handler();
    int delay = TIME_WINDOW*1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE.toString());
        Integer message = intent.getExtras().getInt(MainActivity.EXTRA_HR);

        // Capture the layout's TextView and set the string as its text
        TextView targetHR = findViewById(R.id.targetHR);
        targetHR.setText(message.toString());

        // Get the TextView corresponding to numsteps
        tv_numSteps = findViewById(R.id.txtNumSteps);
        tv_numSteps.setText(Integer.toString(numberSteps));

        // get the TextView corresponding to runningAvgSteps
        tv_runningAvgSteps = findViewById(R.id.txtRunningAvgSteps);
        tv_runningAvgSteps.setText(Float.toString(runningAvgSteps));

        // Start a sensormanager

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        numberSteps += event.values[0];
        runningTotalSteps += event.values[0];
        tv_numSteps.setText(Integer.toString(numberSteps));


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onResume() {
        super.onResume();
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI);

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                Toast.makeText(Workout.this, "beep boop", Toast.LENGTH_SHORT).show();

                runningAvgSteps = (runningTotalSteps/TIME_WINDOW)*12;
                tv_runningAvgSteps.setText(Float.toString(runningAvgSteps));
                runningTotalSteps = 0;

                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        h.removeCallbacks(runnable); //stop handler when activity not visible
    }
}
