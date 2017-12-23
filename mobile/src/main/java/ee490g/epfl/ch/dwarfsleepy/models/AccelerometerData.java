package ee490g.epfl.ch.dwarfsleepy.models;

import java.util.Date;

public class AccelerometerData {

    private Float xAxisValue;
    private Float yAxisValue;
    private Float zAxisValue;
    private Date date;

    public AccelerometerData(Float xAxisValue, Float yAxisValue, Float zAxisValue, Date date) {
        this.xAxisValue = xAxisValue;
        this.yAxisValue = yAxisValue;
        this.zAxisValue = zAxisValue;
        this.date = date;
    }

    // Firebase needs an empty constructor
    public AccelerometerData() {

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
}
