package ee490g.epfl.ch.dwarfsleepy.data;

import java.util.ArrayList;

import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

public class DataHolder {

    public static ArrayList<HeartRateData> averagedHeartRateDataList = new ArrayList<>();
    public static ArrayList<AbnormalHeartRateEvent> abnormalHeartRateEvents = new ArrayList<>();

    public static ArrayList<AccelerometerData> averagedAccelerometerDataList = new ArrayList<>();
    public static ArrayList<AbnormalAccelerometerEvent> abnormalAccelerometerEvents = new ArrayList<>();
}
