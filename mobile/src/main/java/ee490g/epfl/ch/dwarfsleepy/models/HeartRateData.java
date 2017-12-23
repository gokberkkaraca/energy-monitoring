package ee490g.epfl.ch.dwarfsleepy.models;

import java.util.Date;

public class HeartRateData {

    private Float value;
    private Date date;

    public HeartRateData(Float value, Date date) {
        this.value = value;
        this.date = date;
    }

    // Firebase needs an empty constructor
    public HeartRateData() {

    }

    public Float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
