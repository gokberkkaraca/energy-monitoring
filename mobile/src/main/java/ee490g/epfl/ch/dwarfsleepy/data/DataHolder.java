package ee490g.epfl.ch.dwarfsleepy.data;

import java.util.ArrayList;
import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

public class DataHolder {

    public static ArrayList<HeartRateData> averagedHeartRateDataList = new ArrayList<>();
    public static ArrayList<AccelerometerData> averagedAccelerometerData = new ArrayList<>();
    public static int userWeight = 0;
    public static ArrayList<HeartRateData> todayHeartRates = new ArrayList<>();
    public static ArrayList<List<HeartRateData>> nightHeartRates = new ArrayList<>();
    public static ArrayList<List<AccelerometerData>> nightAccelerometerData = new ArrayList<>();

}
