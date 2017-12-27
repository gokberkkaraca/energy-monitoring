package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;

public class AccelerometerData {

    private Float xAxisValue;
    private Float yAxisValue;
    private Float zAxisValue;
    private Float accelerometerValue;
    private Date date;

    public AccelerometerData(Float xAxisValue, Float yAxisValue, Float zAxisValue, Date date) {
        this.xAxisValue = xAxisValue;
        this.yAxisValue = yAxisValue;
        this.zAxisValue = zAxisValue;
        this.accelerometerValue = (float) Math.sqrt(Math.pow(xAxisValue, 2) + Math.pow(yAxisValue, 2) + Math.pow(zAxisValue, 2));
        this.date = date;
    }

    // Firebase needs an empty constructor
    public AccelerometerData() {

    }

    public AccelerometerData(DataMap dataMap) {
        this.xAxisValue = dataMap.getFloat("xAxisValue");
        this.yAxisValue = dataMap.getFloat("yAxisValue");
        this.zAxisValue = dataMap.getFloat("zAxisValue");
        this.accelerometerValue = dataMap.getFloat("accelerometerValue");
        this.date = new Date(dataMap.getLong("date"));
    }

    public Float getXAxisValue() {
        return xAxisValue;
    }

    public Float getYAxisValue() {
        return yAxisValue;
    }

    public Float getZAxisValue() {
        return zAxisValue;
    }

    public Date getDate() {
        return date;
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("xAxisValue", xAxisValue);
        dataMap.putFloat("yAxisValue", yAxisValue);
        dataMap.putFloat("zAxisValue", zAxisValue);
        dataMap.putFloat("accelerometerValue", accelerometerValue);
        dataMap.putLong("date", date.getTime());
        return dataMap;
    }

    public Float getAccelerometerValue() {
        return accelerometerValue;
    }
}
