package ee490g.epfl.ch.dwarfsleepy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;

public class MainActivity extends WearableActivity implements SensorEventListener, DataApi.DataListener, GoogleApiClient.ConnectionCallbacks {

    // Tag for Logcat
    private static final String TAG = "MainActivity";

    // Member for the Wear API handle
    GoogleApiClient mGoogleApiClient;


    private String userId;

    private ArrayList<Float> heartRateData;
    private ArrayList<HeartRateData> averagedHeartRateData;
    private ArrayList<AccelerometerData> accelerometerData;

    private TextView textViewHeartRate;
    private TextView textViewHeartRateAverage;
    private TextView textViewHeartRateAverageDate;
    private TextView textViewAccelerometerX;
    private TextView textViewAccelerometerY;
    private TextView textViewAccelerometerZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission("android.permission.BODY_SENSORS") ==
                PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]
                    {"android.permission.BODY_SENSORS"}, 0);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        assert sensorManager != null;

        Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_UI);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

        textViewHeartRate = findViewById(R.id.heart_rate);
        textViewAccelerometerX = findViewById(R.id.accelerometerX);
        textViewAccelerometerY = findViewById(R.id.accelerometerY);
        textViewAccelerometerZ = findViewById(R.id.accelerometerZ);
        textViewHeartRateAverage = findViewById(R.id.heart_rate_average);
        textViewHeartRateAverageDate = findViewById(R.id.heart_rate_average_date);

        heartRateData = new ArrayList<>();
        averagedHeartRateData = new ArrayList<>();

        accelerometerData = new ArrayList<>();

        // Start the Wear API connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
//        if (mGoogleApiClient.isConnected())
//            Log.v("CONNECTION", "Connection successful");
//        else
//            Log.v("CONNECTION", "Connection unsuccessful");

        // Register to receive messages from the service handling the Wear API connection
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named as IMAGE_DECODED
        LocalBroadcastManager.getInstance(this).registerReceiver(mUserIdReceiver,
                new IntentFilter("RECEIVED_ID"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "Start connectıon");
        mGoogleApiClient.connect();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("HeartRateData", "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                Log.v("HeartRate", "Hey");
                if (textViewHeartRate != null) {
                    Float newHeartRate = event.values[0];
                    heartRateData.add(newHeartRate);
                    textViewHeartRate.setText(String.valueOf(newHeartRate));

                    if (!heartRateData.isEmpty() && heartRateData.size() % 20 == 0) {
                        Float average = 0f;
                        for (Float heartRateValue : this.heartRateData) {
                            average += heartRateValue;
                        }

                        average = average / 20;
                        HeartRateData heartRateData = new HeartRateData(average, Calendar.getInstance().getTime());
                        averagedHeartRateData.add(heartRateData);
                        textViewHeartRateAverage.setText(String.valueOf(heartRateData.getValue()));
                        textViewHeartRateAverageDate.setText(String.valueOf(heartRateData.getDate()));
                        this.heartRateData.clear();
                        DatabaseHandler.addHeartRateData(averagedHeartRateData);
                    }
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (textViewAccelerometerX != null && textViewAccelerometerY != null && textViewAccelerometerZ != null) {
                    AccelerometerData newAccelerometerData = new AccelerometerData(event.values[0], event.values[0], event.values[0], Calendar.getInstance().getTime());
                    accelerometerData.add(newAccelerometerData);
                    DatabaseHandler.addAccelerometerData(accelerometerData);

                    textViewAccelerometerX.setText(String.valueOf(newAccelerometerData.getXAxisValue()));
                    textViewAccelerometerY.setText(String.valueOf(newAccelerometerData.getYAxisValue()));
                    textViewAccelerometerZ.setText(String.valueOf(newAccelerometerData.getZAxisValue()));
                }
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver mUserIdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the image, display it and fade out
            userId = intent.getStringExtra("RECEIVED_ID");
            Log.v("USER_ID", "Got UserID!" + userId);
        }
    };

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(TAG, "onDataChanged: " + dataEvents);
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.v(TAG, "DataItem Changed: " + event.getDataItem().toString() + "\n"
                        + DataMapItem.fromDataItem(event.getDataItem()).getDataMap());

                String path = event.getDataItem().getUri().getPath();
                switch (path) {
                    case "/user":
                        Log.v(TAG, "Data Changed for USER_ID: " + event.getDataItem().toString());
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                        String userId = dataMapItem.getDataMap().getString("USER_ID");
                        Log.v(TAG, "Broadcasting message to activity that user_id is ready" + userId);
                        Intent intent = new Intent("RECEIVED_ID");
                        intent.putExtra("RECEIVED_ID", userId);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    default:
                        Log.v(TAG, "Data Changed for unrecognized path: " + path);
                        break;
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.v(TAG, "DataItem Deleted: " + event.getDataItem().toString());
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Connection to the wear API
        Log.v(TAG, "Google API Client was connected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Connection suspended" + i);
    }

    @Override
    protected void onStop() {
        // App is stopped, close the wear API connection
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
