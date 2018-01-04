package ee490g.epfl.ch.dwarfsleepy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class HeartRateEventListener implements SensorEventListener {

    private int numberOfAveragedData;

    private ArrayList<HeartRateData> heartRateDataList;

    public HeartRateEventListener(int numberOfAveragedData, int highHeartRateLimit) {
        this.numberOfAveragedData = numberOfAveragedData;

        heartRateDataList = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                handleHeartRateEvent(sensorEvent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                Log.d("TAG", "onAccuracyChanged - accuracy: " + i);
                break;
            default:
                Log.d("UnknownSensor", "onAccuracyChanged - accuracy: " + i);
                break;
        }
    }

    private void handleHeartRateEvent(SensorEvent event) {
        HeartRateData newHeartRate = new HeartRateData(event.values[0], Calendar.getInstance().getTime());

        heartRateDataList.add(newHeartRate);

        // Get the average heart rate data
        if (!heartRateDataList.isEmpty() && heartRateDataList.size() % numberOfAveragedData == 0) {
            Float average = 0f;
            for (HeartRateData heartRateData : heartRateDataList) {
                average += heartRateData.getValue();
            }

            average = average / numberOfAveragedData;
            Date averageTime = new Date((heartRateDataList.get(0).getDate().getTime() + heartRateDataList.get(numberOfAveragedData - 1).getDate().getTime()) / 2);
            HeartRateData heartRateData = new HeartRateData(average, averageTime);

            averagedHeartRateDataList.add(heartRateData);
            this.heartRateDataList.clear();
        }
    }

    public void setNumberOfAveragedData(int numberOfAveragedData) {
        this.numberOfAveragedData = numberOfAveragedData;
    }
}
