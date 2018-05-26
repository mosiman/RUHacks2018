package ca.mosiman.ruhacks2018;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "ca.mosiman.ruhacks2018.MESSAGE";
    public static final String EXTRA_HR = "ca.mosiman.ruhacks2018.HR";
    public int targetHR = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textSensors = (TextView) findViewById(R.id.textSensors);

//        SensorManager manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
//
//        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
//
//        StringBuilder message = new StringBuilder();
//        message.append("Sensors on this device are: \n\n\n");
//
//        for(Sensor sensor : sensors){
//            message.append("Name: " + sensor.getName() + "\n");
//        }
//
//        textSensors.setText(message);


    }

    public void startWorkout(View view) {
        // do something when start workout is pressed
        Intent intent = new Intent(this,Workout.class);
        EditText editHR = (EditText) findViewById(R.id.editHR);
        String message = editHR.getText().toString();
        intent.putExtra(EXTRA_HR, targetHR);
        startActivity(intent);
    }

    public void setTargetHR(View view){
        EditText editHR = (EditText) findViewById(R.id.editHR);
        targetHR = Integer.parseInt(editHR.getText().toString());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setMessage(targetHR);
    }
}
