package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;

public class AccelerometerData {

    private Float xaxisValue;
    private Float yaxisValue;
    private Float zaxisValue;
    private Float accelerometerValue;
    private Date date;

    public AccelerometerData(Float xaxisValue, Float yaxisValue, Float zaxisValue, Date date) {
        this.xaxisValue = xaxisValue;
        this.yaxisValue = yaxisValue;
        this.zaxisValue = zaxisValue;
        this.accelerometerValue = (float) Math.sqrt(Math.pow(xaxisValue, 2) + Math.pow(yaxisValue, 2) + Math.pow(zaxisValue, 2));
        this.date = date;
    }

    // Firebase needs an empty constructor
    public AccelerometerData() {

    }

    public AccelerometerData(DataMap dataMap) {
        this.xaxisValue = dataMap.getFloat("xaxisValue");
        this.yaxisValue = dataMap.getFloat("yaxisValue");
        this.zaxisValue = dataMap.getFloat("zaxisValue");
        this.accelerometerValue = dataMap.getFloat("accelerometerValue");
        this.date = new Date(dataMap.getLong("date"));
    }

    public Float getXAxisValue() {
        return xaxisValue;
    }

    public Float getYAxisValue() {
        return yaxisValue;
    }

    public Float getZAxisValue() {
        return zaxisValue;
    }

    public Date getDate() {
        return date;
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("xaxisValue", xaxisValue);
        dataMap.putFloat("yaxisValue", yaxisValue);
        dataMap.putFloat("zaxisValue", zaxisValue);
        dataMap.putFloat("accelerometerValue", accelerometerValue);
        dataMap.putLong("date", date.getTime());
        return dataMap;
    }

    public Float getAccelerometerValue() {
        return accelerometerValue;
    }
}
