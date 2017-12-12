package ee490g.epfl.ch.dwarfsleepy;

import java.util.Date;

public class HeartRate {

    private Float value;
    private Date date;

    public HeartRate(Float value, Date date) {
        this.value = value;
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
