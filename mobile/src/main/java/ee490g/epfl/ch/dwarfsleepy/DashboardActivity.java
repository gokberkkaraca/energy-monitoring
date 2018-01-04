package ee490g.epfl.ch.dwarfsleepy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ee490g.epfl.ch.dwarfsleepy.data.DataHolder;
import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.PhysicalActivity;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.service.DataLayerListenerService;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalAccelerometerEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalHeartRateEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.physicalActivities;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.totalCaloriesBurnedDuringDay;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    // Tag for Logcat
    public static User user;
    private boolean doubleBackToExitPressedOnce = false;
    private Button dayMonitoringButton;
    private Button nightMonitoringButton;
    private Button logoutButton;
    private Button calculateButton;
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1905;

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
        logoutButton.setOnClickListener(this);
        calculateButton.setOnClickListener(this);
        fetchPreviousData();
        setMessageScheduler();
    }

    private static void getGoogleFitValues(DataSet totalSet) {
        Log.i("data", "Data returned for Data type: " + totalSet.getDataType().getName());
        DateFormat dateFormat;
        dateFormat = getTimeInstance();
        float totalCaloriesExpended = 0;
        for (DataPoint dp : totalSet.getDataPoints()) {
            Log.i("data", "Data point:");
            Log.i("data", "\tType: " + dp.getDataType().getName());
            Log.i("data", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i("data", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i("data", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                if (dp.getDataType().getName().equals("com.google.calories.expended")) {
                    totalCaloriesExpended += dp.getValue(field).asFloat();
                } else if (dp.getDataType().getName().equals("com.google.activity.segment")) {
                    Date beginTime = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
                    Date endTime = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));
                    PhysicalActivity.ActivityType activityType;


                    if (dp.getValue(field).asInt() == 7 || dp.getValue(field).asInt() == 95) {
                        activityType = PhysicalActivity.ActivityType.WALKING;
                    } else if (dp.getValue(field).asInt() == 8) {
                        activityType = PhysicalActivity.ActivityType.RUNNING;
                    } else if (dp.getValue(field).asInt() == 1) {
                        activityType = PhysicalActivity.ActivityType.BIKING;
                    } else if (dp.getValue(field).asInt() == 3) {
                        continue;
                    } else {
                        activityType = PhysicalActivity.ActivityType.OTHER;
                    }

                    PhysicalActivity physicalActivity = new PhysicalActivity(activityType, beginTime, endTime);
                    DataHolder.physicalActivities.add(physicalActivity);
                    Log.v("PhysicalActivity", physicalActivity.toString());
                }
            }
            Log.v("Total Calories:", "" + totalCaloriesExpended);
        }
        DataHolder.totalCaloriesBurnedDuringDay = (int) totalCaloriesExpended;
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
                Log.v("DatabaseHandler", "An error occured while fetching data");
            }
        });

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
                Log.v("DatabaseHandler", "An error occured while fetching data");
            }
        });

        for (String accountProvider: FirebaseAuth.getInstance().getCurrentUser().getProviders()) {
            if (("google.com").equals(accountProvider)) {
                getGoogleFitData();
                break;
            }
        }
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
        logoutButton = findViewById(R.id.logoutButton);
        calculateButton = findViewById(R.id.calculateButton);

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(user.getName());

        TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(user.getEmail());

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
            case R.id.logoutButton:
                logOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.calculateButton:
                caloriesCalculator();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            accessGoogleFit();
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        NavigationHandler.goToLoginActivity(this);
    }

    private void getGoogleFitData() {
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("GoogleFitActivity", "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("GoogleFitActivity", "There was a problem subscribing.");
                    }
                });

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }

    private void accessGoogleFit() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);

        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -15);
        cal.add(Calendar.SECOND,1);
        long startTime = cal.getTimeInMillis();

        DateFormat dateFormat = getDateInstance();
        Log.i("TAG", "Range Start: " + dateFormat.format(startTime));
        Log.i("TAG", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .read(DataType.AGGREGATE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("GoogleFit", "onSuccess()");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GoogleFit", "onFailure()", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        physicalActivities.clear();
                        Log.d("GoogleFit", "onComplete()");
                        List<DataSet> dataSets = ((Task<DataReadResponse>) task).getResult().getDataSets();
                        for (DataSet dataSet : dataSets) {
                            getGoogleFitValues(dataSet);
                        }
                    }
                });
    }


    private void caloriesCalculator() {
        String targetCalories = ((EditText) findViewById(R.id.targetCaloriesEditText)).getText().toString();
        if (("").equals(targetCalories)) {
            Toast.makeText(this, "Please enter required calories", Toast.LENGTH_SHORT).show();
        } else {
            int targetCaloriesValue = Integer.parseInt(targetCalories);
            int remainingCalories = targetCaloriesValue - totalCaloriesBurnedDuringDay;
            ((TextView) findViewById(R.id.burntCaloriesTextView)).setText(String.valueOf(totalCaloriesBurnedDuringDay));
            if (remainingCalories > 0) {
                ((TextView) findViewById(R.id.remainingCaloriesTextView)).setText(String.valueOf(remainingCalories));

                ((TextView) findViewById(R.id.bikingSuggestionTextView)).setText(PhysicalActivity.calculateDurationForBiking(remainingCalories, user.getBMR()));
                ((TextView) findViewById(R.id.runningSuggestionTextView)).setText(PhysicalActivity.calculateDurationForRunning(remainingCalories, user.getBMR()));
                ((TextView) findViewById(R.id.walkingSuggestionTextView)).setText(PhysicalActivity.calculateDurationForWalking(remainingCalories, user.getBMR()));
                findViewById(R.id.suggestionLayout).setVisibility(View.VISIBLE);
            } else {
                ((TextView) findViewById(R.id.remainingCaloriesTextView)).setText(String.valueOf(0));
                ((ImageView) findViewById(R.id.remainingCaloriesImageView)).setImageResource(R.drawable.check_mark);
            }
        }
    }
}
