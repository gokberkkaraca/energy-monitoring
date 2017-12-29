package ee490g.epfl.ch.dwarfsleepy.models;


import java.util.Date;

public class PhysicalActivity {

    private ActivityType activityType;
    private Date beginTime;
    private Date endTime;

    public PhysicalActivity(ActivityType activityType, Date beginTime, Date endTime) {
        this.activityType = activityType;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public enum ActivityType {
        BIKING,
        WALKING,
        RUNNING,
        OTHER
    }

    @Override
    public String toString() {
        return "Begin Time: " + beginTime.toString() + "\n" +
                "End Time: " + endTime.toString() + "\n" +
                "Activity Type: " + activityType.toString();
    }
}
