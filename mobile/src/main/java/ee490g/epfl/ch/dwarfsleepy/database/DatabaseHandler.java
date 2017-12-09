package ee490g.epfl.ch.dwarfsleepy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ee490g.epfl.ch.dwarfsleepy.user.User;

public class DatabaseHandler {

    private static final String FIREBASE_DATABASE_URL = "https://dwarf-sleepy.firebaseio.com/";
    private static final String DATABASE_USERS_PATH = "users";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference();

    public static void getUser(String userId, ValueEventListener valueEventListener){
        databaseReference.child(DATABASE_USERS_PATH).child(userId).addListenerForSingleValueEvent(valueEventListener);
    }

    public static void addUser(User user){
        databaseReference.child(DATABASE_USERS_PATH).child(user.getUserId()).setValue(user);
    }
}
