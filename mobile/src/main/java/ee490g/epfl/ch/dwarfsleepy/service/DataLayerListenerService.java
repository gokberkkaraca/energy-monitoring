package ee490g.epfl.ch.dwarfsleepy.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

import ee490g.epfl.ch.dwarfsleepy.AbnormalAccelerometerActivity;
import ee490g.epfl.ch.dwarfsleepy.BuildConfig;
import ee490g.epfl.ch.dwarfsleepy.R;
import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalAccelerometerEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalHeartRateEvents;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;
import static ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler.USER;

public class DataLayerListenerService extends WearableListenerService {

    // Constants
    public static final String ACTION_SEND_MESSAGE = "ACTION_SEND_MESSAGE";
    public static final String MESSAGE = "MESSAGE";
    public static final String PATH = "PATH";
    // Tag for Logcat
    private static final String TAG = "WearListenerService";
    private static User user;
    // Member for the Wear API handle
    private GoogleApiClient mGoogleApiClient;

    public static void setUser(User newUser) {
        user = newUser;
    }

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
            case ACTION_SEND_MESSAGE:
                sendMessage(intent.getStringExtra(MESSAGE), intent.getStringExtra(PATH));
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

        for (DataEvent event : dataEvents) {

            // Get the URI of the event
            Uri uri = event.getDataItem().getUri();

            // Test if data has changed or has been removed
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // Extract the dataMap from the event
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Log.v(TAG, "DataItem Changed: " + event.getDataItem().toString() + "\n"
                        + "\tPath: " + uri
                        + "\tDatamap: " + dataMapItem.getDataMap() + "\n");

                Intent intent;

                switch (uri.getPath()) {
                    case BuildConfig.another_path:
                        // Extract the data behind the key you know contains data
                        retrieveAndUploadHeartRateData(dataMapItem);
                        retrieveAndUploadAbnormalHeartRateData(dataMapItem);
                        retrieveAndUploadAccelerometerData(dataMapItem);
                        retrieveAndUploadAbnormalAccelerometerData(dataMapItem);

                        intent = new Intent("STRING_OF_ANOTHER_ACTION_PREFERABLY_DEFINED_AS_A_CONSTANT_IN_TARGET_ACTIVITY");
                        intent.putExtra("STRING_OF_INTEGER_PREFERABLY_DEFINED_AS_A_CONSTANT_IN_TARGET_ACTIVITY", abnormalHeartRateEvents);
                        intent.putExtra("STRING_OF_ARRAYLIST_PREFERABLY_DEFINED_AS_A_CONSTANT_IN_TARGET_ACTIVITY", averagedHeartRateDataList);
                        intent.putExtra("STRING_OF_ARRAYLIST_PREFERABLY_DEFINED_AS_A_CONSTANT_IN_TARGET_ACTIVITY NEW", averagedAccelerometerData);
                        intent.putExtra("STRING_OF_ARRAYLIST_PREFERABLY_DEFINED_AS_A_CONSTANT_IN_TARGET_ACTIVITY NEW 2", abnormalAccelerometerEvents);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    default:
                        Log.v(TAG, "Data changed for unrecognized path: " + uri);
                        break;
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.w(TAG, "DataItem deleted: " + event.getDataItem().toString());
            }

            // For demo, send a acknowledgement message back to the node that created the data item
            String payload = "Received data OK!";
            String path = BuildConfig.acknowledge;
            String nodeId = uri.getHost();
            sendMessage(payload, path, nodeId);
        }
    }

    private void retrieveAndUploadAbnormalAccelerometerData(DataMapItem dataMapItem) {
        ArrayList<DataMap> abnormalAccelerometerDataMapList = dataMapItem.getDataMap().getDataMapArrayList(BuildConfig.b_key);
        Log.i(TAG, "Got abnormal accelerometer of size: " + abnormalAccelerometerDataMapList.size());
        for (int i = 0; i < abnormalAccelerometerDataMapList.size(); i++) {
            DataMap dataMap = abnormalAccelerometerDataMapList.get(i);
            AbnormalAccelerometerEvent abnormalAccelerometerEvent = new AbnormalAccelerometerEvent(dataMap);
            abnormalAccelerometerEvents.add(abnormalAccelerometerEvent);
        }

        if (!abnormalAccelerometerDataMapList.isEmpty()) {
            DatabaseHandler.addAbnormalAccelerometerEvents(user, abnormalAccelerometerEvents);
            sendAbnormalAccelerometerNotification();
        }
    }

    private void sendAbnormalAccelerometerNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Be careful!")
                .setContentText("Hey sleepy! We realized that you are moving too fast!")
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent notificationIntent = new Intent(this, AbnormalAccelerometerActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        notificationIntent.putExtras(extras);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.setContentIntent(pendingIntent);

        Notification abnormalAccelerometerNotification = mNotifyBuilder.build();
        assert mNotificationManager != null;
        mNotificationManager.notify(0, abnormalAccelerometerNotification);
    }

    private void retrieveAndUploadAccelerometerData(DataMapItem dataMapItem) {
        ArrayList<DataMap> accelerometerDataMapList = dataMapItem.getDataMap().getDataMapArrayList(BuildConfig.more_other_key);
        Log.i(TAG, "Got accelerometer list of size: " + accelerometerDataMapList.size());
        for (int i = 0; i < accelerometerDataMapList.size(); i++) {
            DataMap dataMap = accelerometerDataMapList.get(i);
            AccelerometerData accelerometerData = new AccelerometerData(dataMap);
            averagedAccelerometerData.add(accelerometerData);
        }

        ArrayList<AccelerometerData> latestAccelerometerData = new ArrayList<>();

        if (averagedAccelerometerData.size() > 600) {
            for (int i = 600; i >= 1; i--) {
                AccelerometerData accelerometerData = averagedAccelerometerData.get(averagedAccelerometerData.size() - 1);
                latestAccelerometerData.add(accelerometerData);
            }
        } else {
            latestAccelerometerData.addAll(averagedAccelerometerData);
        }

        DatabaseHandler.addAccelerometerData(user, latestAccelerometerData);
    }

    private void retrieveAndUploadAbnormalHeartRateData(DataMapItem dataMapItem) {
        ArrayList<DataMap> abnormalHeartRateDataMapList = dataMapItem.getDataMap().getDataMapArrayList(BuildConfig.a_key);
        Log.i(TAG, "Got abnormal heart rate list of size: " + abnormalHeartRateDataMapList.size());
        for (int i = 0; i < abnormalHeartRateDataMapList.size(); i++) {
            DataMap dataMap = abnormalHeartRateDataMapList.get(i);
            AbnormalHeartRateEvent abnormalHeartRateEvent = new AbnormalHeartRateEvent(dataMap);
            abnormalHeartRateEvents.add(abnormalHeartRateEvent);
        }
        DatabaseHandler.addAbnormalHeartEvents(user, abnormalHeartRateEvents);
    }

    private void retrieveAndUploadHeartRateData(DataMapItem dataMapItem) {
        ArrayList<DataMap> heartRateDataMapList = dataMapItem.getDataMap().getDataMapArrayList(BuildConfig.some_other_key);
        Log.i(TAG, "Got heart rate list of size: " + heartRateDataMapList.size());
        for (int i = 0; i < heartRateDataMapList.size(); i++) {
            DataMap dataMap = heartRateDataMapList.get(i);
            HeartRateData heartRateData = new HeartRateData(dataMap);
            averagedHeartRateDataList.add(heartRateData);
        }

        ArrayList<HeartRateData> latestHeartRateData = new ArrayList<>();

        if (averagedHeartRateDataList.size() > 600) {
            for (int i = 600; i >= 1; i--) {
                HeartRateData heartRateData = averagedHeartRateDataList.get(averagedHeartRateDataList.size() - i);
                latestHeartRateData.add(heartRateData);
            }
        } else {
            latestHeartRateData.addAll(averagedHeartRateDataList);
        }
        DatabaseHandler.addHeartRateData(user, latestHeartRateData);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // A message has been received from the Wear API
        Log.v(TAG, "onMessageReceived: " + messageEvent);

        // Get the URI of the event
        String path = messageEvent.getPath();

        switch (path) {
            default:
                Log.w(TAG, "Received a message for unknown path " + path + " : " + new String(messageEvent.getData()));
                break;
        }
    }

    private void sendMessage(String message, String path, final String nodeId) {
        // Sends a message through the Wear API
        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, path, message.getBytes())
                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                        Log.v(TAG, "Sent message to " + nodeId + ". Result = " + sendMessageResult.getStatus());
                    }
                });
    }

    private void sendMessage(String message, String path) {
        // Send message to ALL connected nodes
        sendMessageToNodes(message, path);
    }

    private void sendMessageToNodes(final String message, final String path) {
        Log.v(TAG, "Sending message " + message);
        // Lists all the nodes (devices) connected to the Wear API
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult connectedNodes) {
                for (Node node : connectedNodes.getNodes()) {
                    sendMessage(message, path, node.getId());
                }
            }
        });
    }
}