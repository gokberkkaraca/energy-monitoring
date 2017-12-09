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
        sensorManager.registerListener(this,heartRateSensor,SensorManager.SENSOR_DELAY_UI);

        textViewHeartRate = findViewById(R.id.textViewHeartRate);
        heartRateData = new ArrayList<>();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("HeartRate", "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (textViewHeartRate != null) {
            heartRateData.add(event.values[0]);
            textViewHeartRate.setText(String.valueOf(heartRateData.get(heartRateData.size() -1)));
        }
    }

}
