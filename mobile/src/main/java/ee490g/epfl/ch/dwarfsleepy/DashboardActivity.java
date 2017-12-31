package ee490g.epfl.ch.dwarfsleepy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightHeartRates;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.physicalActivities;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.totalCaloriesBurnedDuringDay;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.todayHeartRates;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.userWeight;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    // Tag for Logcat
    public static User user;
    private boolean doubleBackToExitPressedOnce = false;
    private Button dayMonitoringButton;
    private Button nightMonitoringButton;
    private Button calculateCaloriesBurnedButton;
    private Button logoutButton;

    private EditText weightEditText;
    private Button calculateButton;
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1905;

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
        //DataHolder.totalCaloriesBurnedDuringDay = (int) totalCaloriesExpended;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        findViewById(R.id.targetCaloriesEditText).clearFocus();;

        dayMonitoringButton.setOnClickListener(this);
        nightMonitoringButton.setOnClickListener(this);
        calculateCaloriesBurnedButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        calculateButton.setOnClickListener(this);
        fetchPreviousData();
        setMessageScheduler();
    }

    private void fetchPreviousData() {
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
                        todayHeartRates.add(heartRateData);;
                    }

                    calculateCaloriesBurnedToday();
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
                abnormalHeartRateEvents = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AbnormalHeartRateEvent abnormalHeartRateEvent = postSnapshot.getValue(AbnormalHeartRateEvent.class);
                    abnormalHeartRateEvents.add(abnormalHeartRateEvent);
                }
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

        getGoogleFitData();
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
        calculateCaloriesBurnedButton = findViewById(R.id.calculateButton);
        logoutButton = findViewById(R.id.logoutButton);

        weightEditText = findViewById(R.id.weightEditTxt);

        if (userWeight != 0) {
            weightEditText.setText(userWeight + "");
        }

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    userWeight = Integer.parseInt(charSequence.toString());
                } catch(NumberFormatException e) {
                    userWeight = 0;
                }

                calculateCaloriesBurnedToday();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

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
                NavigationHandler.goToNightMonitoringActivity(this, user);
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
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -12);
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

    private void calculateCaloriesBurnedToday() {
        Log.v("TAGGERR", todayHeartRates.size() + "; " + userWeight);
        if (!todayHeartRates.isEmpty() && userWeight != 0) {
            Date currentDate = new Date();
            int todayAverageHeartRate = 0;
            int age = currentDate.getYear() - user.getBirthday().getYear();
            int duration = todayHeartRates.get(todayHeartRates.size() - 1).getDate().getHours();
            double calories;

            for (HeartRateData heartRate: todayHeartRates) {
                todayAverageHeartRate += heartRate.getValue();
            }

            todayAverageHeartRate /= todayHeartRates.size();

            if (user.getGender() == User.Gender.MALE) {
                calories = ((-55.0969 + (0.6309 * todayAverageHeartRate) + (0.1988 * userWeight) + (0.2017 * age))/4.184) * 60 * duration;
            } else {
                calories = ((-20.4022 + (0.4472 * todayAverageHeartRate) - (0.1263 * userWeight) + (0.074 * age))/4.184) * 60 * duration;
            }

            totalCaloriesBurnedDuringDay = (int) calories;

            Log.v("TAGGGGG", totalCaloriesBurnedDuringDay + "");
        }
    }

    private void caloriesCalculator() {
        String targetCalories = ((EditText) findViewById(R.id.targetCaloriesEditText)).getText().toString();
        if (("").equals(targetCalories)) {
            Toast.makeText(this, "Please enter required calories", Toast.LENGTH_SHORT).show();
        }
        else {
            int targetCaloriesValue = Integer.parseInt(targetCalories);
            int remainingCalories = targetCaloriesValue - totalCaloriesBurnedDuringDay;
            ((TextView) findViewById(R.id.burntCaloriesTextView)).setText(String.valueOf(totalCaloriesBurnedDuringDay));
            if (remainingCalories > 0) {
                ((TextView) findViewById(R.id.remainingCaloriesTextView)).setText(String.valueOf(remainingCalories));
            }
            else {
                ((TextView) findViewById(R.id.remainingCaloriesTextView)).setText(String.valueOf(0));
                ((ImageView) findViewById(R.id.remainingCaloriesImageView)).setImageResource(R.drawable.check_mark);
            }
        }
    }
}
