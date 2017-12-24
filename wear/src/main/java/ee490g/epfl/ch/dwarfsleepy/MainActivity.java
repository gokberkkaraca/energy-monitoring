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

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

public class MainActivity extends WearableActivity implements SensorEventListener {

    // Tag for Logcat
    private static final String TAG = "MainActivity";
    private static final int NUMBER_OF_AVERAGED_DATA = 5;

    private ArrayList<Float> heartRateData;
    private static ArrayList<HeartRateData> averagedHeartRateData;
    private static ArrayList<AccelerometerData> accelerometerData;
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
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, 3);

        textViewHeartRate = findViewById(R.id.heart_rate);
        textViewAccelerometerX = findViewById(R.id.accelerometerX);
        textViewAccelerometerY = findViewById(R.id.accelerometerY);
        textViewAccelerometerZ = findViewById(R.id.accelerometerZ);
        textViewHeartRateAverage = findViewById(R.id.heart_rate_average);
        textViewHeartRateAverageDate = findViewById(R.id.heart_rate_average_date);
        textViewAbnormalHRAverage = findViewById(R.id.abnormalHRavg);
        textViewAbnormalHRBegin = findViewById(R.id.abnormalHRbegin);
        textViewAbnormalHREnd = findViewById(R.id.abnormalHRend);

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

                    // Get the average heart rate data
                    // TODO Change this with sending to tablet
                    if (!heartRateData.isEmpty() && heartRateData.size() % NUMBER_OF_AVERAGED_DATA == 0) {
                        Float average = 0f;
                        for (Float heartRateValue : this.heartRateData) {
                            average += heartRateValue;
                        }

                        average = average / NUMBER_OF_AVERAGED_DATA;
                        HeartRateData heartRateData = new HeartRateData(average, Calendar.getInstance().getTime());
                        averagedHeartRateData.add(heartRateData);
                        textViewHeartRateAverage.setText(String.valueOf(heartRateData.getValue()));
                        textViewHeartRateAverageDate.setText(String.valueOf(heartRateData.getDate()));
                        this.heartRateData.clear();
                    }

                    // Filter the data to see if it is a high heart rate, if it is high start to keep its log
                    if (newHeartRate > 100) {
                        HeartRateData instantaneousHR = new HeartRateData(newHeartRate, Calendar.getInstance().getTime());
                        abnormalHR.add(instantaneousHR);
                    } else {
                        if (abnormalHR.size() > 0) {
                            textViewAbnormalHRBegin.setText(abnormalHR.get(0).getDate().toString());
                            textViewAbnormalHREnd.setText(abnormalHR.get(abnormalHR.size() - 1).getDate().toString());

                            float sum = 0;
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
            case Sensor.TYPE_ACCELEROMETER:
                if (textViewAccelerometerX != null && textViewAccelerometerY != null && textViewAccelerometerZ != null) {
                    AccelerometerData newAccelerometerData = new AccelerometerData(event.values[0], event.values[1], event.values[2], Calendar.getInstance().getTime());
                    accelerometerData.add(newAccelerometerData);

                    textViewAccelerometerX.setText(String.valueOf(newAccelerometerData.getXAxisValue()));
                    textViewAccelerometerY.setText(String.valueOf(newAccelerometerData.getYAxisValue()));
                    textViewAccelerometerZ.setText(String.valueOf(newAccelerometerData.getZAxisValue()));
                }
                break;
            default:
                break;
        }
    }

    public static ArrayList<HeartRateData> getAveragedHeartRateData() {
        return averagedHeartRateData;
    }

    public static ArrayList<AccelerometerData> getAccelerometerData() {
        return accelerometerData;
    }
}
