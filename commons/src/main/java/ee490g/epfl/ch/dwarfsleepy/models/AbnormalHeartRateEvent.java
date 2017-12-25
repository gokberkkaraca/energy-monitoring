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

    public AbnormalHeartRateEvent() {

    }

    public AbnormalHeartRateEvent(DataMap dataMap) {
        this.averageHeartRateValue = dataMap.getFloat("averageHeartRateValue");
        this.beginTime = new Date(dataMap.getLong("beginTime"));
        this.endTime = new Date(dataMap.getLong("endTime"));
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("averageHeartRateValue", averageHeartRateValue);
        dataMap.putLong("beginTime", beginTime.getTime());
        dataMap.putLong("endTime", endTime.getTime());
        return dataMap;
    }

    public Float getAverageHeartRateValue() {
        return averageHeartRateValue;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
