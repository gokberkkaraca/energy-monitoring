package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;

public class AbnormalAccelerometerEvent {

    private Float xAxisValue;
    private Float yAxisValue;
    private Float zAxisValue;
    private Float accelerometerValue;

    private Date beginTime;
    private Date endTime;
    private int durationMilliseconds;
    private int durationSeconds;
    private int durationMinutes;
    private int durationHours;

    public AbnormalAccelerometerEvent(Float xAxisValue, Float yAxisValue, Float zAxisValue, Date beginTime, Date endTime) {
        this.xAxisValue = xAxisValue;
        this.yAxisValue = yAxisValue;
        this.zAxisValue = zAxisValue;
        this.beginTime = beginTime;
        this.endTime = endTime;

        this.accelerometerValue = (float) Math.sqrt(Math.pow(xAxisValue, 2) + Math.pow(yAxisValue, 2) + Math.pow(zAxisValue, 2));

        long duration = endTime.getTime() - beginTime.getTime() + 1;
        this.durationMilliseconds = (int) (duration) % 1000;
        this.durationSeconds = (int) (duration / 1000) % 60;
        this.durationMinutes = (int) ((duration / (1000 * 60)) % 60);
        this.durationHours = (int) ((duration / (1000 * 60 * 60)) % 24);
    }

    public AbnormalAccelerometerEvent() {

    }

    public AbnormalAccelerometerEvent(DataMap dataMap) {
        this.xAxisValue = dataMap.getFloat("xAxisValue");
        this.yAxisValue = dataMap.getFloat("yAxisValue");
        this.zAxisValue = dataMap.getFloat("zAxisValue");
        this.accelerometerValue = (float) Math.sqrt(Math.pow(xAxisValue, 2) + Math.pow(yAxisValue, 2) + Math.pow(zAxisValue, 2));

        this.beginTime = new Date(dataMap.getLong("beginTime"));
        this.endTime = new Date(dataMap.getLong("endTime"));

        long duration = dataMap.getLong("duration");
        this.durationMilliseconds = (int) (duration) % 1000;
        this.durationSeconds = (int) (duration / 1000) % 60;
        this.durationMinutes = (int) ((duration / (1000 * 60)) % 60);
        this.durationHours = (int) ((duration / (1000 * 60 * 60)) % 24);
    }

    public DataMap putToDataMap(DataMap dataMap) {
        dataMap.putFloat("xAxisValue", xAxisValue);
        dataMap.putFloat("yAxisValue", yAxisValue);
        dataMap.putFloat("zAxisValue", zAxisValue);
        dataMap.putLong("beginTime", beginTime.getTime());
        dataMap.putLong("endTime", endTime.getTime());
        dataMap.putLong("duration", endTime.getTime() - beginTime.getTime() + 1);
        return dataMap;
    }

    public Float getxAxisValue() {
        return xAxisValue;
    }

    public Float getyAxisValue() {
        return yAxisValue;
    }

    public Float getzAxisValue() {
        return zAxisValue;
    }

    public Float getAccelerometerValue() {
        return accelerometerValue;
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
}
