package ee490g.epfl.ch.dwarfsleepy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalHeartRateEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class HeartRateEventListener implements SensorEventListener {

    private static String TAG = "HeartRateEventListener";

    private int numberOfAveragedData;
    private int highHeartRateLimit;

    private ArrayList<HeartRateData> heartRateDataList;
    private ArrayList<HeartRateData> abnormalHeartRateList;

    public HeartRateEventListener(int numberOfAveragedData, int highHeartRateLimit) {
        this.numberOfAveragedData = numberOfAveragedData;
        this.highHeartRateLimit = highHeartRateLimit;

        heartRateDataList = new ArrayList<>();
        abnormalHeartRateList = new ArrayList<>();
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

        // Filter the data to see if it is a high heart rate, if it is high start to keep its log
        if (newHeartRate.getValue() > highHeartRateLimit) {
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
                abnormalHeartRateList.clear();
            }
        }

    }
}
