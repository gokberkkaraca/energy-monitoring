package ee490g.epfl.ch.dwarfsleepy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.Calendar;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerDataList;

public class AccelerometerEventListener implements SensorEventListener {

    private int numberOfAveragedAccelerometerData;


    public AccelerometerEventListener(int numberOfAveragedAccelerometerData) {
        this.numberOfAveragedAccelerometerData = numberOfAveragedAccelerometerData;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                handleAccelerometerEvent(sensorEvent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.d("AccelerometerData", "onAccuracyChanged - accuracy: " + i);
                break;
            default:
                Log.d("UnknownSensor", "onAccuracyChanged - accuracy: " + i);
                break;
        }
    }

    private void handleAccelerometerEvent(SensorEvent event) {
        AccelerometerData newAccelerometerData = new AccelerometerData(event.values[0], event.values[1], event.values[2], Calendar.getInstance().getTime());
        averagedAccelerometerDataList.add(newAccelerometerData);

    }
}
