package ee490g.epfl.ch.dwarfsleepy;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import java.util.ArrayList;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.sensor.AccelerometerEventListener;
import ee490g.epfl.ch.dwarfsleepy.sensor.HeartRateEventListener;

public class MainActivity extends WearableActivity {

    public static final int HIGH_HR_LIMIT = 100;
    private static final int NUMBER_OF_AVERAGED_HR_DATA = 1;
    private static final int HR_SENSOR_SAMPLING_TIME = 0;

    private static final int NUMBER_OF_AVERAGED_ACCELEROMETER_DATA = 100;
    private static final int ACCELEROMETER_SENSOR_SAMPLING_TIME = 3;

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
        HeartRateEventListener heartRateEventListener = new HeartRateEventListener(NUMBER_OF_AVERAGED_HR_DATA, HIGH_HR_LIMIT);
        sensorManager.registerListener(heartRateEventListener, heartRateSensor, HR_SENSOR_SAMPLING_TIME);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        AccelerometerEventListener accelerometerEventListener = new AccelerometerEventListener(NUMBER_OF_AVERAGED_ACCELEROMETER_DATA);
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, ACCELEROMETER_SENSOR_SAMPLING_TIME);

        initializeViews();
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
}
