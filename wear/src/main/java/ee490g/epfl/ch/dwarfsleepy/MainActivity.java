package ee490g.epfl.ch.dwarfsleepy;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalHeartRateEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.accelerometerDataList;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class MainActivity extends WearableActivity implements SensorEventListener {

    public static final int HIGH_HR_LIMIT = 100;
    // Tag for Logcat
    private static final String TAG = "MainActivity";
    private static final int NUMBER_OF_AVERAGED_DATA = 1;

    private ArrayList<HeartRateData> heartRateDataList;
    private ArrayList<HeartRateData> abnormalHeartRateList;
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
        sensorManager.registerListener(this, heartRateSensor, 0);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, 3);

        initializeViews();

        initializeArraysLists();
    }

    private void initializeArraysLists() {
        heartRateDataList = new ArrayList<>();
        abnormalHeartRateList = new ArrayList<>();
    }

    private void initializeViews() {
        textViewHeartRate = findViewById(R.id.heart_rate);
        textViewAccelerometerX = findViewById(R.id.accelerometerX);
        textViewAccelerometerY = findViewById(R.id.accelerometerY);
        textViewAccelerometerZ = findViewById(R.id.accelerometerZ);
        textViewHeartRateAverage = findViewById(R.id.heart_rate_average);
        textViewHeartRateAverageDate = findViewById(R.id.heart_rate_average_date);
        textViewAbnormalHRAverage = findViewById(R.id.abnormalHRavg);
        textViewAbnormalHRBegin = findViewById(R.id.abnormalHRbegin);
        textViewAbnormalHREnd = findViewById(R.id.abnormalHRend);
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
                handleHeartRateEvent(event);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                handleAccelerometerEvent(event);
                break;
            default:
                break;
        }
    }

    private void handleAccelerometerEvent(SensorEvent event) {
        if (textViewAccelerometerX != null && textViewAccelerometerY != null && textViewAccelerometerZ != null) {

            AccelerometerData newAccelerometerData = new AccelerometerData(event.values[0], event.values[1], event.values[2], Calendar.getInstance().getTime());
            accelerometerDataList.add(newAccelerometerData);

            textViewAccelerometerX.setText(String.valueOf(newAccelerometerData.getXAxisValue()));
            textViewAccelerometerY.setText(String.valueOf(newAccelerometerData.getYAxisValue()));
            textViewAccelerometerZ.setText(String.valueOf(newAccelerometerData.getZAxisValue()));
        }
    }

    private void handleHeartRateEvent(SensorEvent event) {
        if (textViewHeartRate != null) {
            HeartRateData newHeartRate = new HeartRateData(event.values[0], Calendar.getInstance().getTime());

            heartRateDataList.add(newHeartRate);
            textViewHeartRate.setText(String.valueOf(newHeartRate.getValue()));

            // Get the average heart rate data
            if (!heartRateDataList.isEmpty() && heartRateDataList.size() % NUMBER_OF_AVERAGED_DATA == 0) {
                Float average = 0f;
                for (HeartRateData heartRateData : heartRateDataList) {
                    average += heartRateData.getValue();
                }

                average = average / NUMBER_OF_AVERAGED_DATA;
                Date averageTime = new Date((heartRateDataList.get(0).getDate().getTime() + heartRateDataList.get(NUMBER_OF_AVERAGED_DATA - 1).getDate().getTime()) / 2);
                HeartRateData heartRateData = new HeartRateData(average, averageTime);

                averagedHeartRateDataList.add(heartRateData);
                textViewHeartRateAverage.setText(String.valueOf(heartRateData.getValue()));
                textViewHeartRateAverageDate.setText(String.valueOf(heartRateData.getDate()));
                this.heartRateDataList.clear();
            }

            // Filter the data to see if it is a high heart rate, if it is high start to keep its log
            if (newHeartRate.getValue() > HIGH_HR_LIMIT) {
                abnormalHeartRateList.add(newHeartRate);
            } else {
                if (!abnormalHeartRateList.isEmpty()) {

                    float sum = 0;
                    for (HeartRateData heartRateData : abnormalHeartRateList)
                        sum = sum + heartRateData.getValue();

                    float abnormalAverage = sum / abnormalHeartRateList.size();
                    Date beginTime = abnormalHeartRateList.get(0).getDate();
                    Date endTime = abnormalHeartRateList.get(abnormalHeartRateList.size() - 1).getDate();

                    AbnormalHeartRateEvent abnormalHeartRateEvent = new AbnormalHeartRateEvent(abnormalAverage, beginTime, endTime);
                    abnormalHeartRateEvents.add(abnormalHeartRateEvent);

                    textViewAbnormalHRBegin.setText(abnormalHeartRateEvent.getBeginTime().toString());
                    textViewAbnormalHREnd.setText(abnormalHeartRateEvent.getEndTime().toString());
                    textViewAbnormalHRAverage.setText(String.valueOf(abnormalAverage));

                    abnormalHeartRateList.clear();
                }
            }
        }
    }
}
