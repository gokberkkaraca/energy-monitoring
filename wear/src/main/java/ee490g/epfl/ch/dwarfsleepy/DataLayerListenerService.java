package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Collections;

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

public class DataLayerListenerService extends WearableListenerService {

    // Constants
    public static final String ACTION_SEND_DATAMAP = "ACTION_SEND_DATAMAP";
    public static final String DATAMAP_INT = "DATAMAP_INT";
    public static final String DATAMAP_INT_ARRAYLIST = "DATAMAP_INT_ARRAYLIST";
    // Tag for Logcat
    private static final String TAG = "WearListenerService";
    // Member for the Wear API handle
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        // Start the Wear API connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;

        switch (action) {
            case ACTION_SEND_DATAMAP:
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(BuildConfig.another_path);
                putDataMapRequest.getDataMap().putInt(BuildConfig.a_key, intent.getIntExtra(DATAMAP_INT, -1));
                putDataMapRequest.getDataMap().putIntegerArrayList(BuildConfig.some_other_key, intent.getIntegerArrayListExtra(DATAMAP_INT_ARRAYLIST));
                sendPutDataMapRequest(putDataMapRequest);
                break;
            default:
                Log.w(TAG, "Unknown action");
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(TAG, "onDataChanged: " + dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // A message has been received from the Wear API
        Log.v(TAG, "onMessageReceived: " + messageEvent);

        // Get the URI of the event
        String path = messageEvent.getPath();

        switch (path) {
            case BuildConfig.some_path:
                Log.v(TAG, "Received a message for path " + BuildConfig.some_path + " : " + new String(messageEvent.getData()));
                // For demo, send back a dataMap
                ArrayList<HeartRateData> heartRateList = MainActivity.getAveragedHeartRateData();
                Log.v(TAG, "Sending heart rate list of size: " + heartRateList.size());
                ArrayList<DataMap> dataMapHeartRateList = new ArrayList<>();
                for (int i = 0; i < heartRateList.size(); i++) {
                    dataMapHeartRateList.add(heartRateList.get(i).putToDataMap(new DataMap()));
                }

                ArrayList<AccelerometerData> accelerometerList = MainActivity.getAccelerometerData();
                Log.v(TAG, "Sending accelerometer list of size: " + accelerometerList.size());
                ArrayList<DataMap> dataMapAccelerometer = new ArrayList<>();
                for (int i = 0; i < accelerometerList.size(); i++) {
                    dataMapAccelerometer.add(accelerometerList.get(i).putToDataMap(new DataMap()));
                }

                sendSpecificDatamap(dataMapHeartRateList, dataMapAccelerometer);
                break;
            default:
                Log.w(TAG, "Received a message for unknown path " + path + " : " + new String(messageEvent.getData()));
        }
    }

    void sendSpecificDatamap(ArrayList<DataMap> heartRateList, ArrayList<DataMap> accelerometerList) {
        // Sends data (a datamap) through the Wear API
        // It's specific to a datamap containing an int and an arraylist. Duplicate and change
        // according to your needs
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(BuildConfig.another_path);
        putDataMapRequest.getDataMap().putDataMapArrayList(BuildConfig.a_key, accelerometerList);
        //putDataMapRequest.getDataMap().putDataMapArrayList(BuildConfig.a_key, accelerometerList);
        putDataMapRequest.getDataMap().putDataMapArrayList(BuildConfig.some_other_key, heartRateList);
        sendPutDataMapRequest(putDataMapRequest);
    }

    void sendPutDataMapRequest(PutDataMapRequest putDataMapRequest) {
        putDataMapRequest.getDataMap().putLong("time", System.nanoTime());
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        Log.v(TAG, "Sent datamap. Result = " + dataItemResult.getStatus());

                    }
                });
    }
}