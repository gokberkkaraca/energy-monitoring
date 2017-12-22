package ee490g.epfl.ch.dwarfsleepy.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Dell on 12/20/2017.
 */

public class HeartRateData implements Serializable {

    private Date date;
    private Double value;

    public HeartRateData() {}

    public Date getDate() {
        return date;
    }

    public Double getValue() {
        return value;
    }

}
