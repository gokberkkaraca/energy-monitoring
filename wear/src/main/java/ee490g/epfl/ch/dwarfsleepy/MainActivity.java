package ee490g.epfl.ch.dwarfsleepy;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.sensor.AccelerometerEventListener;
import ee490g.epfl.ch.dwarfsleepy.sensor.HeartRateEventListener;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerDataList;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class MainActivity extends WearableActivity {

    public static final int HIGH_HR_LIMIT = 100;
    private static final int NUMBER_OF_AVERAGED_HR_DATA = 1;
    private static final int HR_SENSOR_SAMPLING_TIME = 0;

    private static final int HIGH_ACCELEROMETER_LIMIT = 25;
    private static final int NUMBER_OF_AVERAGED_ACCELEROMETER_DATA = 10;
    private static final int ACCELEROMETER_SENSOR_SAMPLING_TIME = 3;

    private ImageView heartImage;
    private TextView heartRateTextView;
    private TextView accelerometerTextView;

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
        AccelerometerEventListener accelerometerEventListener = new AccelerometerEventListener(NUMBER_OF_AVERAGED_ACCELEROMETER_DATA, HIGH_ACCELEROMETER_LIMIT);
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, ACCELEROMETER_SENSOR_SAMPLING_TIME);

        initializeViews();
        updateViews();
    }

    private void initializeViews() {
        heartImage = findViewById(R.id.heartImage);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        accelerometerTextView = findViewById(R.id.accelerometerTextView);
    }

    private void updateViews() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1);
                if (!averagedHeartRateDataList.isEmpty()) {
                    HeartRateData lastHeartRateData = averagedHeartRateDataList.get(averagedHeartRateDataList.size() - 1);
                    int heartRateValue = (int) lastHeartRateData.getValue().floatValue();
                    heartRateTextView.setText(String.valueOf(heartRateValue));

                    if (heartRateValue > 100)
                        heartImage.setImageResource(R.drawable.heart_attack);
                    else
                        heartImage.setImageResource(R.drawable.heart_small);
                }

                if (!averagedAccelerometerDataList.isEmpty()) {
                    AccelerometerData lastAccelerometerData = averagedAccelerometerDataList.get(averagedAccelerometerDataList.size() - 1);
                    float accelerometerValue = lastAccelerometerData.getAccelerometerValue();
                    String accelerometerString = String.valueOf(accelerometerValue).substring(0, 5);
                    accelerometerTextView.setText(accelerometerString);
                }
            }
        };
        handler.postDelayed(r, 0);
    }
}
