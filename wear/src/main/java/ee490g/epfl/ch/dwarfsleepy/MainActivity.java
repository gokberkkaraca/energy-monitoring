package ee490g.epfl.ch.dwarfsleepy;

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
import java.util.Date;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;

public class MainActivity extends WearableActivity implements SensorEventListener {

    // Tag for Logcat
    private static final String TAG = "MainActivity";

    private String userId;

    private ArrayList<Float> heartRateData;
    private ArrayList<HeartRateData> averagedHeartRateData;
    private ArrayList<AccelerometerData> accelerometerData;
    private ArrayList<HeartRateData> abnormalHR;

    private TextView textViewHeartRate;
    private TextView textViewHeartRateAverage;
    private TextView textViewHeartRateAverageDate;
    private TextView textViewAccelerometerX;
    private TextView textViewAccelerometerY;
    private TextView textViewAccelerometerZ;

    private TextView textViewAbnormalHRAverage;
    private TextView textViewAbnormalHRBegin;
    private TextView textViewAbnormalHREnd;

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
                    {"android.permission.BODY_SENSORS"}, 0);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        assert sensorManager != null;

        Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_UI);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

        textViewHeartRate = findViewById(R.id.heart_rate);
        textViewAccelerometerX = findViewById(R.id.accelerometerX);
        textViewAccelerometerY = findViewById(R.id.accelerometerY);
        textViewAccelerometerZ = findViewById(R.id.accelerometerZ);
        textViewHeartRateAverage = findViewById(R.id.heart_rate_average);
        textViewHeartRateAverageDate = findViewById(R.id.heart_rate_average_date);
        textViewAbnormalHRAverage=findViewById(R.id.abnormalHRavg);
        textViewAbnormalHRBegin=findViewById(R.id.abnormalHRbegin);
        textViewAbnormalHREnd=findViewById(R.id.abnormalHRend);

        heartRateData = new ArrayList<>();
        averagedHeartRateData = new ArrayList<>();
        abnormalHR = new ArrayList<>();
        accelerometerData = new ArrayList<>();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                Log.d("HeartRateData", "onAccuracyChanged - accuracy: " + accuracy);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                Log.d("HeartRateData", "onAccuracyChanged - accuracy: " + accuracy);
                break;
            default:
                Log.d("UnknownSensor", "onAccuracyChanged - accuracy: " + accuracy);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                if (textViewHeartRate != null) {
                    Float newHeartRate = event.values[0];
                    heartRateData.add(newHeartRate);
                    textViewHeartRate.setText(String.valueOf(newHeartRate));

                    // Get the average of 20 heart rate data and send it to firebase
                    // TODO Change this with sending to tablet
                    if (!heartRateData.isEmpty() && heartRateData.size() % 20 == 0) {
                        Float average = 0f;
                        for (Float heartRateValue : this.heartRateData) {
                            average += heartRateValue;
                        }

                        average = average / 20;
                        HeartRateData heartRateData = new HeartRateData(average, Calendar.getInstance().getTime());
                        averagedHeartRateData.add(heartRateData);
                        textViewHeartRateAverage.setText(String.valueOf(heartRateData.getValue()));
                        textViewHeartRateAverageDate.setText(String.valueOf(heartRateData.getDate()));
                        this.heartRateData.clear();
                        DatabaseHandler.addHeartRateData(averagedHeartRateData);
                    }

                    // Filter the data to see if it is a high heart rate, if it is high start to keep its log
                    if (newHeartRate > 100){
                        HeartRateData instantaneousHR = new HeartRateData(newHeartRate, Calendar.getInstance().getTime());
                        abnormalHR.add(instantaneousHR);
                    }
                    else{
                        if (abnormalHR.size() > 0) {
                            textViewAbnormalHRBegin.setText(abnormalHR.get(0).getDate().toString());
                            textViewAbnormalHREnd.setText(abnormalHR.get(abnormalHR.size()-1).getDate().toString());

                            float sum=0;
                            for (HeartRateData heartRateData : abnormalHR)
                                sum = sum + heartRateData.getValue();

                            float abnormalAverage = sum / abnormalHR.size();
                            textViewAbnormalHRAverage.setText(String.valueOf(abnormalAverage));
                            abnormalHR.clear();
                        }
                    }
                }
                break;
                // TODO Accelerometer and HeartRate doesn't work at the same time
            /*case Sensor.TYPE_ACCELEROMETER:
                if (textViewAccelerometerX != null && textViewAccelerometerY != null && textViewAccelerometerZ != null) {
                    AccelerometerData newAccelerometerData = new AccelerometerData(event.values[0], event.values[1], event.values[2], Calendar.getInstance().getTime());
                    accelerometerData.add(newAccelerometerData);
                    DatabaseHandler.addAccelerometerData(accelerometerData);

                    textViewAccelerometerX.setText(String.valueOf(newAccelerometerData.getXAxisValue()));
                    textViewAccelerometerY.setText(String.valueOf(newAccelerometerData.getYAxisValue()));
                    textViewAccelerometerZ.setText(String.valueOf(newAccelerometerData.getZAxisValue()));
                }
                break;*/
            default:
                break;
        }
    }
}
