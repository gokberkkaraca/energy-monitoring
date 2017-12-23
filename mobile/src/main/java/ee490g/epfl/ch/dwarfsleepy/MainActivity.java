package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        mAuth = FirebaseAuth.getInstance();
        goToNextActivity();
    }

    private void goToNextActivity() {
        int TIMER = 500;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    String userId = mAuth.getUid();
                    DatabaseHandler.getUser(userId, new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            NavigationHandler.goToDashboardActivity(MainActivity.this, user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }, TIMER);
    }
}
