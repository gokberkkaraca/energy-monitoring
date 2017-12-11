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

public class MainActivity extends WearableActivity implements SensorEventListener{

    private TextView textViewHeartRate;
    private ArrayList<Float> heartRateData;
    private TextView textViewAccelerometer;
    private ArrayList<Float> accelerometerData;

    Sensor heartRateSensor;
    Sensor accelerometerSensor;

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

        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this,heartRateSensor,SensorManager.SENSOR_DELAY_UI);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_UI);

        textViewHeartRate = findViewById(R.id.heart_rate);
        textViewAccelerometer =  findViewById(R.id.accelerometer);
        heartRateData = new ArrayList<>();
        accelerometerData = new ArrayList<>();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("HeartRate", "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                if (textViewHeartRate != null) {
                    heartRateData.add(event.values[0]);
                    textViewHeartRate.setText(String.valueOf(heartRateData.get(heartRateData.size() - 1)));
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (textViewAccelerometer != null) {
                    accelerometerData.add(event.values[0]);
                    textViewAccelerometer.setText(String.valueOf(accelerometerData.get(accelerometerData.size() - 1)));
                }

        }
    }

}
