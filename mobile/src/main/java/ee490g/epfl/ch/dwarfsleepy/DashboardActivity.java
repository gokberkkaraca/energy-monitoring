package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton profileButton;
    private Button refreshButton;
    private Button polarBeltButton;
    private Button accelerometerButton;
    private Button dayMonitoringButton;
    private Button nightMonitoringButton;
    private TextView heartRateTextView;

    private List<HeartRateData> heartRateData;
    private List<AccelerometerData> accelerometerData;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        profileButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
        polarBeltButton.setOnClickListener(this);
        accelerometerButton.setOnClickListener(this);
        dayMonitoringButton.setOnClickListener(this);
        nightMonitoringButton.setOnClickListener(this);
    }

    private void initializeViews() {
        profileButton = findViewById(R.id.profileButton);
        refreshButton = findViewById(R.id.refreshButton);
        polarBeltButton = findViewById(R.id.polarBeltButton);
        accelerometerButton = findViewById(R.id.accelerometerButton);
        dayMonitoringButton = findViewById(R.id.dayMonitoringButton);
        nightMonitoringButton = findViewById(R.id.nightMonitoringButton);
        heartRateTextView = findViewById(R.id.heartRateTextView);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.profileButton:
                NavigationHandler.goToUserProfileActivity(this, user);
                break;
            default:
                break;
        }
    }

    private void fetchHeartRates() {
        DatabaseHandler.getHeartRatesData(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                heartRateData = new ArrayList<>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    heartRateData.add(snapshot.getValue(HeartRateData.class));
                }

                heartRateTextView.setText(String.valueOf(heartRateData.get(heartRateData.size() - 1).getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, "Failed to fetch heart rate data", Toast.LENGTH_LONG).show();
            }
        });
    }
}
