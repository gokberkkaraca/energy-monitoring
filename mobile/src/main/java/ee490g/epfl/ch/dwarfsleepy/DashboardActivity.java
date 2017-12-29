package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.service.DataLayerListenerService;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalAccelerometerEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalHeartRateEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    // Tag for Logcat
    public static User user;
    boolean doubleBackToExitPressedOnce = false;
    private Button dayMonitoringButton;
    private Button nightMonitoringButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        dayMonitoringButton.setOnClickListener(this);
        nightMonitoringButton.setOnClickListener(this);

        fetchPreviousData();
        setMessageScheduler();
    }

    private void fetchPreviousData() {
        DatabaseHandler.getHeartRateData(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                averagedHeartRateDataList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    HeartRateData heartRateData = postSnapshot.getValue(HeartRateData.class);
                    averagedHeartRateDataList.add(heartRateData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseHandler.getAbnormalHeartRateEvents(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                abnormalHeartRateEvents = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AbnormalHeartRateEvent abnormalHeartRateEvent = postSnapshot.getValue(AbnormalHeartRateEvent.class);
                    abnormalHeartRateEvents.add(abnormalHeartRateEvent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseHandler.getAbnormalAccelerometerEvents(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                abnormalAccelerometerEvents = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AbnormalAccelerometerEvent abnormalAccelerometerEvent = postSnapshot.getValue(AbnormalAccelerometerEvent.class);
                    abnormalAccelerometerEvents.add(abnormalAccelerometerEvent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseHandler.getAccelerometerData(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                averagedAccelerometerData = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AccelerometerData accelerometerData = postSnapshot.getValue(AccelerometerData.class);
                    averagedAccelerometerData.add(accelerometerData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMessageScheduler() {
        Runnable messageSender = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(DashboardActivity.this, DataLayerListenerService.class);
                intent.setAction(DataLayerListenerService.ACTION_SEND_MESSAGE);
                intent.putExtra(DataLayerListenerService.MESSAGE, "Messaging other device!");
                intent.putExtra(DataLayerListenerService.PATH, BuildConfig.some_path);
                startService(intent);
            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(messageSender, 10, 10, TimeUnit.SECONDS);
    }

    private void initializeViews() {
        dayMonitoringButton = findViewById(R.id.dayMonitoringButton);
        nightMonitoringButton = findViewById(R.id.nightMonitoringButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dayMonitoringButton:
                NavigationHandler.goToDayMonitoringActivity(this, user);
                break;
            case R.id.nightMonitoringButton:
                //TODO
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
