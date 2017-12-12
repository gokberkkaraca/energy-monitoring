package ee490g.epfl.ch.dwarfsleepy;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DatabaseHandler {

    private static final String FIREBASE_DATABASE_URL = "https://dwarf-sleepy.firebaseio.com/";
    private static final String DATABASE_HEART_RATE_PATH = "heartRates";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference();

    public static void addHeartRateData(List<HeartRate> heartRates){
        databaseReference.child(DATABASE_HEART_RATE_PATH).setValue(heartRates);
    }
}
