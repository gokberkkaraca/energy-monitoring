package ee490g.epfl.ch.dwarfsleepy.data;

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
