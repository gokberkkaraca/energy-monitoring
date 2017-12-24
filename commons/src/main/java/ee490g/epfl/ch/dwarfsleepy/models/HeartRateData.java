package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

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

    public HeartRateData(DataMap dataMap) {
        this.value = dataMap.getFloat("value");
        this.date = new Date(dataMap.getLong("date"));
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("value", value);
        dataMap.putLong("date", date.getTime());
        return dataMap;
    }

    public Float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
