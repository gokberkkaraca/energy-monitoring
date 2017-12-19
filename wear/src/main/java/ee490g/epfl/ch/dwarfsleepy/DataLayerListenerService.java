package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayerListenerService extends WearableListenerService {

    // Tag for Logcat
    private static final String TAG = "DataLayerService";

    // Member for the Wear API handle
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        // Start the Wear API connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        if(mGoogleApiClient.isConnected())
            Log.v("CONNECTION", "Connection successful");
        else
            Log.v("CONNECTION", "Connection unsuccessful");
    }

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
}
