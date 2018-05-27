package ca.mosiman.ruhacks2018;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity implements SensorEventListener,
        MessageClient.OnMessageReceivedListener{

    Integer stepsTaken = 0;
    TextView tv_heartRate;
    TextView tv_numSteps;
    Button btn_sendMessage;
    SensorManager sensorManager;
    Integer currentHeartRate = -1; // sentinel value

    private final static String TAG = "Wear MainActivity";
    int num = 1;
    String datapath = "/message_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_heartRate = (TextView) findViewById(R.id.heartRate);
        btn_sendMessage = (Button) findViewById(R.id.btnSendMessage);
        tv_numSteps = (TextView) findViewById(R.id.numSteps);
        String message = "heartu ratuh:";
        tv_heartRate.setText(message);
        tv_numSteps.setText(stepsTaken.toString());

        // Enables Always-on
        setAmbientEnabled();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Hello device " + num;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                //Requires a new thread to avoid blocking the UI
                new SendThread(datapath, message).start();
                num++;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        Sensor heartSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this,heartSensor,SensorManager.SENSOR_DELAY_NORMAL);
        Wearable.getMessageClient(this).addListener(this);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        //h.removeCallbacks(runnable); //stop handler when activity not visible
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            currentHeartRate = (int)event.values[0];
            //tv_heartRate.setText(msg);
            // send the message to phone when sensor changed
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    @Override
//    public void onClick(View v) {
//
//    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived() A message from watch was received:"
                + messageEvent.getRequestId() + " " + messageEvent.getPath());
        if (messageEvent.getPath().equals("/message_path")) {  //don't think this if is necessary anymore.
            String message =new String(messageEvent.getData());
            Log.v(TAG, "Wear activity received message: " + message);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            //here, send a message back.
            message = "Hello device " + num;
            //Requires a new thread to avoid blocking the UI
            new SendThread(datapath, message).start();
            num++;
        }
    }
    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
            path = p;
            message = msg;
        }

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, so no problem.
        public void run() {
            //first get all the nodes, ie connected wearable devices.
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                List<Node> nodes = Tasks.await(nodeListTask);

                //Now send the message to each device.
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        Integer result = Tasks.await(sendMessageTask);
                        Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

                    } catch (ExecutionException exception) {
                        Log.e(TAG, "Task failed: " + exception);

                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Interrupt occurred: " + exception);
                    }

                }

            } catch (ExecutionException exception) {
                Log.e(TAG, "Task failed: " + exception);

            } catch (InterruptedException exception) {
                Log.e(TAG, "Interrupt occurred: " + exception);
            }
        }
    }
}
