package ee490g.epfl.ch.dwarfsleepy.models;


import java.util.Date;

public class PhysicalActivity {

    private static final double WALKING_METS = 4.3;
    private static final double RUNNING_METS = 11;
    private static final double BIKING_METS = 6.8;

    private ActivityType activityType;
    private Date beginTime;
    private Date endTime;
    private long duration;

    public PhysicalActivity(ActivityType activityType, Date beginTime, Date endTime) {
        this.activityType = activityType;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.duration = endTime.getTime() - beginTime.getTime();
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

    public int getDurationMilliseconds() {
        return (int) (duration) % 1000;
    }

    public int getDurationSeconds() {
        return (int) (duration / 1000) % 60;
    }

    public int getDurationMinutes() {
        return (int) ((duration / (1000 * 60)) % 60);
    }

    public int getDurationHours() {
        return (int) ((duration / (1000 * 60 * 60)) % 24);
    }

    @Override
    public String toString() {
        return "Begin Time: " + beginTime.toString() + "\n" +
                "End Time: " + endTime.toString() + "\n" +
                "Activity Type: " + activityType.toString();
    }

    public enum ActivityType {
        BIKING,
        WALKING,
        RUNNING,
        OTHER
    }

    public static String calculateDurationForBiking(int remainingCalories, double BMR) {
        int durationMilliseconds = (int) ((24 * remainingCalories) / (BMR * BIKING_METS) * 3600000);
        int durationSeconds = (durationMilliseconds / 1000) % 60;
        int durationMinutes = ((durationMilliseconds / (1000 * 60)) % 60);
        int durationHours = ((durationMilliseconds / (1000 * 60 * 60)) % 24);

        return durationHours + "h " + durationMinutes + "m " + durationSeconds + "s";
    }

    public static String calculateDurationForRunning(int remainingCalories, double BMR) {
        int durationMilliseconds = (int) ((24 * remainingCalories) / (BMR * RUNNING_METS) * 3600000);
        int durationSeconds = (durationMilliseconds / 1000) % 60;
        int durationMinutes = ((durationMilliseconds / (1000 * 60)) % 60);
        int durationHours = ((durationMilliseconds / (1000 * 60 * 60)) % 24);

        return durationHours + "h " + durationMinutes + "m " + durationSeconds + "s";
    }

    public static String calculateDurationForWalking(int remainingCalories, double BMR) {

        int durationMilliseconds = (int) ((24 * remainingCalories) / (BMR * WALKING_METS) * 3600000);
        int durationSeconds = (durationMilliseconds / 1000) % 60;
        int durationMinutes = ((durationMilliseconds / (1000 * 60)) % 60);
        int durationHours = ((durationMilliseconds / (1000 * 60 * 60)) % 24);

        return durationHours + "h " + durationMinutes + "m " + durationSeconds + "s";
    }
}
