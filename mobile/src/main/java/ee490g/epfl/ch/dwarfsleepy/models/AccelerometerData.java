package ee490g.epfl.ch.dwarfsleepy.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Dell on 12/20/2017.
 */

public class AccelerometerData implements Serializable {

    private Date date;
    private Double xaxisValue;
    private Double yaxisValue;
    private Double zaxisValue;

    public AccelerometerData() {}

    public Date getDate() {
        return date;
    }

    public Double getXAxisValue() {
        return xaxisValue;
    }

    public Double getYAxisValue() {
        return yaxisValue;
    }

    public Double getZAxisValue() {
        return zaxisValue;
    }

}
