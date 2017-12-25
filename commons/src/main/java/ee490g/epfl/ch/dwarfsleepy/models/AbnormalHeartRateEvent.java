package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;

public class AbnormalHeartRateEvent {

    private Float averageHeartRateValue;
    private Date beginTime;
    private Date endTime;
    private int durationSeconds;
    private int durationMinutes;
    private int durationHours;

    public AbnormalHeartRateEvent(Float averageHeartRateValue, Date beginTime, Date endTime) {
        this.averageHeartRateValue = averageHeartRateValue;
        this.beginTime = beginTime;
        this.endTime = endTime;

        long duration = endTime.getTime() - beginTime.getTime();
        this.durationSeconds = (int) (duration / 1000) % 60 ;
        this.durationMinutes = (int) ((duration / (1000*60)) % 60);
        this.durationHours = (int) ((duration / (1000*60*60)) % 24);
    }

    public AbnormalHeartRateEvent() {

    }

    public AbnormalHeartRateEvent(DataMap dataMap) {
        this.averageHeartRateValue = dataMap.getFloat("averageHeartRateValue");
        this.beginTime = new Date(dataMap.getLong("beginTime"));
        this.endTime = new Date(dataMap.getLong("endTime"));

        long duration = dataMap.getLong("duration");
        this.durationSeconds = (int) (duration / 1000) % 60 ;
        this.durationMinutes = (int) ((duration / (1000*60)) % 60);
        this.durationHours = (int) ((duration / (1000*60*60)) % 24);

    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("averageHeartRateValue", averageHeartRateValue);
        dataMap.putLong("beginTime", beginTime.getTime());
        dataMap.putLong("endTime", endTime.getTime());
        dataMap.putLong("duration", endTime.getTime() - beginTime.getTime());
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

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getDurationHours() {
        return durationHours;
    }
}
