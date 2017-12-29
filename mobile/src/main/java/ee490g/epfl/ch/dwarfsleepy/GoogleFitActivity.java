package ee490g.epfl.ch.dwarfsleepy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ee490g.epfl.ch.dwarfsleepy.data.DataHolder;
import ee490g.epfl.ch.dwarfsleepy.models.PhysicalActivity;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class GoogleFitActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GoogleFitActivity";
    private User user;
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1905;

    private static void getGoogleFitValues(DataSet totalSet) {
        Log.i("data", "Data returned for Data type: " + totalSet.getDataType().getName());
        DateFormat dateFormat;
        dateFormat = getTimeInstance();
        float totalCaloriesExpended = 0;
        DataHolder.physicalActivities = new ArrayList<>();
        for (DataPoint dp : totalSet.getDataPoints()) {
            Log.i("data", "Data point:");
            Log.i("data", "\tType: " + dp.getDataType().getName());
            Log.i("data", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i("data", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i("data", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                if (dp.getDataType().getName().equals("com.google.calories.expended")) {
                    totalCaloriesExpended += dp.getValue(field).asFloat();
                }
                else if(dp.getDataType().getName().equals("com.google.activity.segment")) {
                    Date beginTime = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
                    Date endTime = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));
                    PhysicalActivity.ActivityType activityType;


                    if (dp.getValue(field).asInt() == 7 || dp.getValue(field).asInt()== 95) {
                        activityType = PhysicalActivity.ActivityType.WALKING;
                    }
                    else if (dp.getValue(field).asInt() == 8) {
                        activityType = PhysicalActivity.ActivityType.RUNNING;
                    }
                    else if (dp.getValue(field).asInt() == 1) {
                        activityType = PhysicalActivity.ActivityType.BIKING;
                    }
                    else if (dp.getValue(field).asInt() == 3) {
                        continue;
                    }
                    else {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("dfds", "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("dsad", "There was a problem subscribing.");
                    }
                });

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                //.addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_READ)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            }
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

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i("TAG", "Range Start: " + dateFormat.format(startTime));
        Log.i("TAG", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                //aggregate(DataType.TYPE_CALORIES_EXPENDED,DataType.AGGREGATE_CALORIES_EXPENDED)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .read(DataType.AGGREGATE_CALORIES_EXPENDED)
                //.bucketByTime(1000, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(LOG_TAG, "onSuccess()");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "onFailure()", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(LOG_TAG, "onComplete()");
                        List<DataSet> dataSets = ((Task<DataReadResponse>) task).getResult().getDataSets();

                        for (DataSet dataSet : dataSets) {

                            getGoogleFitValues(dataSet);

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDayMonitoringActivity(this, user);
    }
}