package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightHeartRates;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.todayHeartRates;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.userWeight;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    // Tag for Logcat
    public static User user;
    private boolean doubleBackToExitPressedOnce = false;
    private Button nightMonitoringButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();

        nightMonitoringButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        fetchPreviousHeartRateData();
        fetchPreviousAbnormalHeartRateData();
        fetchPreviousAbnormalAccelerometerData();
        fetchPreviousAccelerometerData();
        setMessageScheduler();
    }

    private void fetchPreviousAccelerometerData() {
        DatabaseHandler.getAccelerometerData(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                averagedAccelerometerData = new ArrayList<>();
                nightAccelerometerData = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AccelerometerData accelerometerData = postSnapshot.getValue(AccelerometerData.class);
                    averagedAccelerometerData.add(accelerometerData);

                    // Get accelerometer night activity
                    if (accelerometerData.getDate().getHours() >= 0 && accelerometerData.getDate().getHours() <= 8) {
                        if (!nightAccelerometerData.isEmpty() && accelerometerData.getDate().getDate() == nightAccelerometerData.get(nightAccelerometerData.size() - 1).get(0).getDate().getDate() &&
                                accelerometerData.getDate().getMonth() == nightAccelerometerData.get(nightAccelerometerData.size() - 1).get(0).getDate().getMonth() &&
                                accelerometerData.getDate().getYear() == nightAccelerometerData.get(nightAccelerometerData.size() - 1).get(0).getDate().getYear()) {
                            nightAccelerometerData.get(nightAccelerometerData.size() - 1).add(accelerometerData);
                        } else {
                            List<AccelerometerData> data = new ArrayList<>();
                            data.add(accelerometerData);
                            nightAccelerometerData.add(data);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("DatabaseHandler", "An error occured while fetching data");
            }
        });
    }

    private void fetchPreviousAbnormalAccelerometerData() {
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
                Log.v("DatabaseHandler", "An error occured while fetching data");
            }
        });
    }

    private void fetchPreviousAbnormalHeartRateData() {
        DatabaseHandler.getAbnormalHeartRateEvents(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date currentDate = new Date();
                abnormalHeartRateEvents = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AbnormalHeartRateEvent abnormalHeartRateEvent = postSnapshot.getValue(AbnormalHeartRateEvent.class);

                    assert abnormalHeartRateEvent != null;
                    boolean isTodaysHR =
                            abnormalHeartRateEvent.getEndTime().getYear() == currentDate.getYear() &&
                                    abnormalHeartRateEvent.getEndTime().getMonth() == currentDate.getMonth() &&
                                    abnormalHeartRateEvent.getEndTime().getDay() == currentDate.getDay();

                    if (isTodaysHR)
                        abnormalHeartRateEvents.add(abnormalHeartRateEvent);
                }

                DatabaseHandler.addAbnormalHeartEvents(user, abnormalHeartRateEvents);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("DatabaseHandler", "An error occured while fetching data");
            }
        });
    }

    private void fetchPreviousHeartRateData() {
        DatabaseHandler.getHeartRateData(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                averagedHeartRateDataList = new ArrayList<>();
                nightHeartRates = new ArrayList<>();
                todayHeartRates = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    HeartRateData heartRateData = postSnapshot.getValue(HeartRateData.class);
                    averagedHeartRateDataList.add(heartRateData);

                    // Get heart rate night activity
                    if (heartRateData.getDate().getHours() >= 0 && heartRateData.getDate().getHours() <= 8) {
                            if (!nightHeartRates.isEmpty() && heartRateData.getDate().getDate() == nightHeartRates.get(nightHeartRates.size() - 1).get(0).getDate().getDate() &&
                                heartRateData.getDate().getMonth() == nightHeartRates.get(nightHeartRates.size() - 1).get(0).getDate().getMonth() &&
                                heartRateData.getDate().getYear() == nightHeartRates.get(nightHeartRates.size() - 1).get(0).getDate().getYear()) {
                                nightHeartRates.get(nightHeartRates.size() - 1).add(heartRateData);
                            } else {
                                List<HeartRateData> heartRates = new ArrayList<>();
                                heartRates.add(heartRateData);
                                nightHeartRates.add(heartRates);
                            }
                    }

                    //Calculate today's calories burned
                    Date currentDate = new Date();

                    if (heartRateData.getDate().getDate() == currentDate.getDate() &&
                            heartRateData.getDate().getMonth() == currentDate.getMonth() &&
                            heartRateData.getDate().getYear() == currentDate.getYear()) {
                        todayHeartRates.add(heartRateData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("DatabaseHandler", "An error occured while fetching data");
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
        nightMonitoringButton = findViewById(R.id.nightMonitoringButton);
        logoutButton = findViewById(R.id.logoutButton);

        EditText weightEditText = findViewById(R.id.weightEditTxt);

        if (userWeight != 0) {
            weightEditText.setText(userWeight + "");
        }

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.v("Tag", "Msj");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    userWeight = Integer.parseInt(charSequence.toString());
                } catch(NumberFormatException e) {
                    userWeight = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.v("Tagg", "Msjj");
            }
        });

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(user.getName());

        TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(user.getEmail());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nightMonitoringButton:
                NavigationHandler.goToNightMonitoringActivity(this, user);
                break;
            case R.id.logoutButton:
                logOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
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

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        NavigationHandler.goToLoginActivity(this);
    }
}
