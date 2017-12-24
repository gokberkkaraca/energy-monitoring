package ee490g.epfl.ch.dwarfsleepy.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

        final ArrayList<HeartRateData> mergeHeartRateData = new ArrayList<>();
        DatabaseHandler.getHeartRateData(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    HeartRateData heartRateData = postSnapshot.getValue(HeartRateData.class);
                    mergeHeartRateData.add(heartRateData);
                }

                mergeHeartRateData.addAll(heartRateDataList);
                Log.v("DatabaseHandler", "Add Heart rate with size : " + mergeHeartRateData.size());
                //databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).child(DATABASE_HEART_RATES_PATH).setValue(mergeHeartRateData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getAccelerometerData(User user, ValueEventListener valueEventListener) {
        databaseReference.child(DATABASE_ACCELEROMETER_PATH).addListenerForSingleValueEvent(valueEventListener);
    }

    public static void addAccelerometerData(User user, ArrayList<AccelerometerData> accelerometerData) {
        databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).child(DATABASE_ACCELEROMETER_PATH).setValue(accelerometerData);
    }
}
