package ee490g.epfl.ch.dwarfsleepy.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ee490g.epfl.ch.dwarfsleepy.R;
import ee490g.epfl.ch.dwarfsleepy.data.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.data.HeartRateData;

public class MainActivity extends WearableActivity implements SensorEventListener{


    private ArrayList<Float> heartRateData;
    private ArrayList<HeartRateData> averagedHeartRateData;
    private ArrayList<AccelerometerData> accelerometerData;

    private TextView textViewHeartRate;
    private TextView textViewHeartRateAverage;
    private TextView textViewHeartRateAverageDate;
    private TextView textViewAccelerometerX;
    private TextView textViewAccelerometerY;
    private TextView textViewAccelerometerZ;

    public static final String ACTION_SEND_USER = "ACTION_SEND_USER";
    public static final String STRING_USER = "STRING_USER";
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission("android.permission.BODY_SENSORS") ==
                PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]
                    {"android.permission.BODY_SENSORS"},0);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        assert sensorManager != null;

        Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this, heartRateSensor,SensorManager.SENSOR_DELAY_UI);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

        textViewHeartRate = findViewById(R.id.heart_rate);
        textViewAccelerometerX =  findViewById(R.id.accelerometerX);
        textViewAccelerometerY = findViewById(R.id.accelerometerY);
        textViewAccelerometerZ = findViewById(R.id.accelerometerZ);
        textViewHeartRateAverage = findViewById(R.id.heart_rate_average);
        textViewHeartRateAverageDate = findViewById(R.id.heart_rate_average_date);

        heartRateData = new ArrayList<>();
        averagedHeartRateData = new ArrayList<>();

        accelerometerData = new ArrayList<>();

        LocalBroadcastManager.getInstance(this).registerReceiver(mUserReceiver,
                new IntentFilter(ACTION_SEND_USER));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("HeartRateData", "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                if (textViewHeartRate != null) {
                    Float newHeartRate = event.values[0];
                    heartRateData.add(newHeartRate);
                    textViewHeartRate.setText(String.valueOf(newHeartRate));

                    if (!heartRateData.isEmpty() && heartRateData.size() % 20 == 0) {
                        Float average = 0f;
                        for (Float heartRateValue: this.heartRateData) {
                            average += heartRateValue;
                        }

                        average = average / 20;
                        HeartRateData heartRateData = new HeartRateData(average, Calendar.getInstance().getTime());
                        averagedHeartRateData.add(heartRateData);
                        textViewHeartRateAverage.setText(String.valueOf(heartRateData.getValue()));
                        textViewHeartRateAverageDate.setText(String.valueOf(heartRateData.getDate() ));
                        this.heartRateData.clear();
                        DatabaseHandler.addHeartRateData(averagedHeartRateData);
                    }
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (textViewAccelerometerX != null && textViewAccelerometerY != null && textViewAccelerometerZ != null) {
                    AccelerometerData newAccelerometerData = new AccelerometerData(event.values[0], event.values[0], event.values[0], Calendar.getInstance().getTime());
                    accelerometerData.add(newAccelerometerData);
                    DatabaseHandler.addAccelerometerData(accelerometerData);

                    textViewAccelerometerX.setText(String.valueOf(newAccelerometerData.getXAxisValue()));
                    textViewAccelerometerY.setText(String.valueOf(newAccelerometerData.getYAxisValue()));
                    textViewAccelerometerZ.setText(String.valueOf(newAccelerometerData.getZAxisValue()));
                }
                break;
            default:
                break;
        }
    }
    private String userWatch = "";
    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            userWatch = intent.getStringExtra(STRING_USER);
            Log.d(TAG,"Got intent userWatch: " + userWatch);
            //TextView textView = findViewById(R.id.heart_rate);
            String te= String.valueOf(userWatch); // in case it is int
            //textView.setText(te);
            textViewHeartRate.setText(te);
        }
    };
}
