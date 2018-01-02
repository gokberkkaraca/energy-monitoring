package ee490g.epfl.ch.dwarfsleepy.models;


import java.util.Date;

public class PhysicalActivity {

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

    public static String calculateDurationForBiking(int remainingCalories) {
        int durationMilliseconds = (int) (((double) remainingCalories / 10) * (60 * 1000)); // Minutes to milliseconds conversion
        int durationSeconds = (durationMilliseconds / 1000) % 60;
        int durationMinutes = ((durationMilliseconds / (1000 * 60)) % 60);
        int durationHours = ((durationMilliseconds / (1000 * 60 * 60)) % 24);

        return durationHours + "h " + durationMinutes + "m " + durationSeconds + "s";
    }

    public static String calculateDurationForRunning(int remainingCalories, double weight, int height) {
        int durationMilliseconds = (int) (remainingCalories / ((0.035 * weight) + (6.25 / ((double) height) / 100))) * (60 * 1000);
        int durationSeconds = (durationMilliseconds / 1000) % 60;
        int durationMinutes = ((durationMilliseconds / (1000 * 60)) % 60);
        int durationHours = ((durationMilliseconds / (1000 * 60 * 60)) % 24);

        return durationHours + "h " + durationMinutes + "m " + durationSeconds + "s";
    }

    public static String calculateDurationForWalking(int remainingCalories, double weight, int height) {
        int durationMilliseconds = (int) (remainingCalories / ((0.035 * weight) + (1.96 / ((double) height) / 100))) * (60 * 1000);
        int durationSeconds = (durationMilliseconds / 1000) % 60;
        int durationMinutes = ((durationMilliseconds / (1000 * 60)) % 60);
        int durationHours = ((durationMilliseconds / (1000 * 60 * 60)) % 24);

        return durationHours + "h " + durationMinutes + "m " + durationSeconds + "s";
    }
}
