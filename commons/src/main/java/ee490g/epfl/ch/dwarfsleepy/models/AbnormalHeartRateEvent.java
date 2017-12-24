package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;

public class AbnormalHeartRateEvent {

    private Float averageHeartRateValue;
    private Date beginTime;
    private Date endTime;

    public AbnormalHeartRateEvent(Float averageHeartRateValue, Date beginTime, Date endTime) {
        this.averageHeartRateValue = averageHeartRateValue;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public AbnormalHeartRateEvent(DataMap dataMap) {
        this.averageHeartRateValue = dataMap.getFloat("averageHeartRateValue");
        this.beginTime = new Date(dataMap.getLong("beginTime"));
        this.endTime = new Date(dataMap.getLong("endTime"));
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("averageHeartRateValue", averageHeartRateValue);
        dataMap.putLong("averageHeartRateValue", beginTime.getTime());
        dataMap.putLong("averageHeartRateValue", endTime.getTime());
        return dataMap;
    }
}
