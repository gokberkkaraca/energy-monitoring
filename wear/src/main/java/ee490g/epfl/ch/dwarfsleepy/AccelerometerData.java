package ee490g.epfl.ch.dwarfsleepy;

import java.util.Date;

/**
 * Created by Dell on 12/21/2017.
 */

public class AccelerometerData {

    private Float xaxisvalue;
    private Float yaxisvalue;
    private Float zaxisvalue;

    private Date date;

    public AccelerometerData(Float[] axisValues, Date date) {
        this.xaxisvalue = axisValues[0];
        this.yaxisvalue = axisValues[1];
        this.zaxisvalue = axisValues[2];
        this.date = date;
    }

    public Float getXAxisValue() {
        return xaxisvalue;
    }

    public Float getYAxisValue() {
        return yaxisvalue;
    }
    public Float getZAxisValue() {
        return zaxisvalue;
    }

    public Date getDate() {
        return date;
    }
}
