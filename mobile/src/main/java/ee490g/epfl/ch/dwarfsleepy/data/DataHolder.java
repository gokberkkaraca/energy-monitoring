package ee490g.epfl.ch.dwarfsleepy.data;

import java.util.ArrayList;

import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.PhysicalActivity;

public class DataHolder {

    public static ArrayList<HeartRateData> averagedHeartRateDataList = new ArrayList<>();
    public static ArrayList<AbnormalHeartRateEvent> abnormalHeartRateEvents = new ArrayList<>();

    public static ArrayList<AccelerometerData> averagedAccelerometerData = new ArrayList<>();
    public static ArrayList<AbnormalAccelerometerEvent> abnormalAccelerometerEvents = new ArrayList<>();

    public static int totalCaloriesBurnedDuringDay;
    public static ArrayList<PhysicalActivity> physicalActivities = new ArrayList<>();
}
