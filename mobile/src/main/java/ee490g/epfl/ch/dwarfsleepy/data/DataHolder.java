package ee490g.epfl.ch.dwarfsleepy.data;

import java.util.ArrayList;
import java.util.List;

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

    public static int totalCaloriesBurnedDuringDay = 0;
    public static int userWeight = 0;
    public static ArrayList<PhysicalActivity> physicalActivities = new ArrayList<>();

    public static ArrayList<HeartRateData> todayHeartRates = new ArrayList<>();
    public static ArrayList<List<HeartRateData>> nightHeartRates = new ArrayList<>();
    public static ArrayList<List<AccelerometerData>> nightAccelerometerData = new ArrayList<>();

}
