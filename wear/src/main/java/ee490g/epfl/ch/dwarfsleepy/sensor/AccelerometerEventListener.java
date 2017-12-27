package ee490g.epfl.ch.dwarfsleepy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ee490g.epfl.ch.dwarfsleepy.MainActivity;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;

import static android.content.Context.VIBRATOR_SERVICE;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerDataList;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalAccelerometerEvents;

public class AccelerometerEventListener implements SensorEventListener {

    private int numberOfAveragedData;
    private int highAccelerometerLimit;

    private ArrayList<AccelerometerData> accelerometerDataList;
    private ArrayList<AccelerometerData> abrubtAccelerometerList;

    public AccelerometerEventListener(int numberOfAveragedData, int highAccelerometerLimit) {
        this.numberOfAveragedData = numberOfAveragedData;
        this.highAccelerometerLimit = highAccelerometerLimit;

        accelerometerDataList = new ArrayList<>();
        abrubtAccelerometerList = new ArrayList<>();
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
        accelerometerDataList.add(newAccelerometerData);

        if (!accelerometerDataList.isEmpty() && accelerometerDataList.size() % numberOfAveragedData == 0) {
            Float xAverage = 0f;
            Float yAverage = 0f;
            Float zAverage = 0f;
            for (AccelerometerData accelerometerData : accelerometerDataList) {
                xAverage += accelerometerData.getXAxisValue();
                yAverage += accelerometerData.getYAxisValue();
                zAverage += accelerometerData.getZAxisValue();
            }

            xAverage = xAverage / numberOfAveragedData;
            yAverage = yAverage / numberOfAveragedData;
            zAverage = zAverage / numberOfAveragedData;

            Date averageTime = new Date((accelerometerDataList.get(0).getDate().getTime() + accelerometerDataList.get(numberOfAveragedData - 1).getDate().getTime()) / 2);
            AccelerometerData accelerometerData = new AccelerometerData(xAverage, yAverage, zAverage, averageTime);

            averagedAccelerometerDataList.add(accelerometerData);
            Log.v("AccelerometerListener", String.valueOf(accelerometerData.getAccelerometerValue()));
            this.accelerometerDataList.clear();
        }

        // Filter the data to see if it is a high heart rate, if it is high start to keep its log
        if (newAccelerometerData.getAccelerometerValue() > highAccelerometerLimit) {
            abrubtAccelerometerList.add(newAccelerometerData);
        } else {
            if (!abrubtAccelerometerList.isEmpty()) {

                Float xAverage = 0f;
                Float yAverage = 0f;
                Float zAverage = 0f;
                for (AccelerometerData accelerometerData : abrubtAccelerometerList) {
                    xAverage += accelerometerData.getXAxisValue();
                    yAverage += accelerometerData.getYAxisValue();
                    zAverage += accelerometerData.getZAxisValue();
                }

                xAverage = xAverage / abrubtAccelerometerList.size();
                yAverage = yAverage / abrubtAccelerometerList.size();
                zAverage = zAverage / abrubtAccelerometerList.size();


                Date beginTime = abrubtAccelerometerList.get(0).getDate();
                Date endTime = abrubtAccelerometerList.get(abrubtAccelerometerList.size() - 1).getDate();

                AbnormalAccelerometerEvent abnormalAccelerometerEvent = new AbnormalAccelerometerEvent(xAverage, yAverage, zAverage, beginTime, endTime);
                abnormalAccelerometerEvents.add(abnormalAccelerometerEvent);
                abrubtAccelerometerList.clear();
            }
        }
    }
}
