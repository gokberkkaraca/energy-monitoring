package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.Date;

public class AbnormalHeartRateEvent {

    private Float averageHeartRateValue;
    private Date beginTime;
    private Date endTime;
    private int durationMilliseconds;
    private int durationSeconds;
    private int durationMinutes;
    private int durationHours;

    public AbnormalHeartRateEvent(Float averageHeartRateValue, Date beginTime, Date endTime) {
        this.averageHeartRateValue = averageHeartRateValue;
        this.beginTime = beginTime;
        this.endTime = endTime;

        long duration = endTime.getTime() - beginTime.getTime() + 1;
        this.durationMilliseconds = (int) (duration) % 1000;
        this.durationSeconds = (int) (duration / 1000) % 60;
        this.durationMinutes = (int) ((duration / (1000 * 60)) % 60);
        this.durationHours = (int) ((duration / (1000 * 60 * 60)) % 24);
    }

    public AbnormalHeartRateEvent() {

    }

    public AbnormalHeartRateEvent(DataMap dataMap) {
        this.averageHeartRateValue = dataMap.getFloat("averageHeartRateValue");
        this.beginTime = new Date(dataMap.getLong("beginTime"));
        this.endTime = new Date(dataMap.getLong("endTime"));

        long duration = dataMap.getLong("duration");
        this.durationMilliseconds = (int) (duration) % 1000;
        this.durationSeconds = (int) (duration / 1000) % 60;
        this.durationMinutes = (int) ((duration / (1000 * 60)) % 60);
        this.durationHours = (int) ((duration / (1000 * 60 * 60)) % 24);

    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("averageHeartRateValue", averageHeartRateValue);
        dataMap.putLong("beginTime", beginTime.getTime());
        dataMap.putLong("endTime", endTime.getTime());
        dataMap.putLong("duration", endTime.getTime() - beginTime.getTime() + 1);
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

    public int getDurationMilliseconds() {
        return durationMilliseconds;
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

    public boolean isExercise(ArrayList<PhysicalActivity> physicalActivities) {
        if (physicalActivities.isEmpty()){
            return false;
        }

        for (PhysicalActivity physicalActivity: physicalActivities ) {
            boolean isContained = false; // TODO Check if physicalActivity time contains heart rate time

            if (isContained) {
                return true;
            }
        }

        return false;
    }
}
