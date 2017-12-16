package ee490g.epfl.ch.dwarfsleepy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.data.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.data.HeartRateData;

public class DatabaseHandler {

    private static final String FIREBASE_DATABASE_URL = "https://dwarf-sleepy.firebaseio.com/";
    private static final String DATABASE_HEART_RATE_DATA_PATH = "heartRateData";
    private static final String DATABASE_ACCELEROMETER_DATA_PATH = "accelerometerData";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference();

    public static void addHeartRateData(List<HeartRateData> heartRates){
        databaseReference.child(DATABASE_HEART_RATE_DATA_PATH).setValue(heartRates);
    }

    public static void addAccelerometerData(List<AccelerometerData> accelerometerData) {
        databaseReference.child(DATABASE_ACCELEROMETER_DATA_PATH).setValue(accelerometerData);
    }
}
