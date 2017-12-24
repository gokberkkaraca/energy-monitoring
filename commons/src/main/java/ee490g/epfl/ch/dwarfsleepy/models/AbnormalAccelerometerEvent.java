package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;

public class AbnormalAccelerometerEvent {

    // TODO Add more fields
    private Date date;

    public AbnormalAccelerometerEvent(Date date) {
        this.date = date;
    }

    public AbnormalAccelerometerEvent(DataMap dataMap) {
        this.date = new Date(dataMap.getLong("date"));
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putLong("date", date.getTime());
        return dataMap;
    }
}
