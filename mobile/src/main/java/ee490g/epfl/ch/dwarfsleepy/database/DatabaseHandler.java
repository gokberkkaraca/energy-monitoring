package ee490g.epfl.ch.dwarfsleepy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;

public class DatabaseHandler {

    private static final String FIREBASE_DATABASE_URL = "https://dwarf-sleepy.firebaseio.com/";
    private static final String DATABASE_USERS_PATH = "users";
    private static final String DATABASE_HEART_RATES_PATH = "heartRates";
    private static final String DATABASE_ACCELEROMETER_PATH = "accelerometerData";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference();

    public static void getUser(String userId, ValueEventListener valueEventListener) {
        databaseReference.child(DATABASE_USERS_PATH).child(userId).addListenerForSingleValueEvent(valueEventListener);
    }

    public static void addUser(User user) {
        databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).setValue(user);
    }


    public static void getHeartRateData(User user, ValueEventListener valueEventListener) {
        databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).child(DATABASE_HEART_RATES_PATH).addValueEventListener(valueEventListener);
    }

    public static void addHeartRateData(final User user, final ArrayList<HeartRateData> heartRateDataList) {
        databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).child(DATABASE_HEART_RATES_PATH).setValue(heartRateDataList);
    }

    public static void getAccelerometerData(User user, ValueEventListener valueEventListener) {
        databaseReference.child(DATABASE_ACCELEROMETER_PATH).addListenerForSingleValueEvent(valueEventListener);
    }

    public static void addAccelerometerData(User user, ArrayList<AccelerometerData> accelerometerData) {
        databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).child(DATABASE_ACCELEROMETER_PATH).setValue(accelerometerData);
    }
}
