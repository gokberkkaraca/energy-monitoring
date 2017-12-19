package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener, CapabilityApi.CapabilityListener{

    private ImageButton profileButton;
    private Button refreshButton;
    private Button polarBeltButton;
    private Button accelerometerButton;
    private Button dayMonitoringButton;
    private Button nightMonitoringButton;
    private TextView heartRateTextView;

    private User user;

    // Tag for Logcat
    private static final String TAG = "DashboardActivity";

    // Members used for the Wear API
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

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

        // Start the Wear API connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
            case R.id.polarBeltButton:
                //TODO
                break;
            case R.id.accelerometerButton:
                break;
            case R.id.dayMonitoringButton:
                //TODO
                break;
            case R.id.nightMonitoringButton:
                //TODO
                break;
            case R.id.refreshButton:
                //TODO
                break;
            default:
                break;
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        // Connection to the wear API
        Log.v(TAG, "Google API Client was connected");
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.CapabilityApi.addListener(
                mGoogleApiClient, this, Uri.parse("wear://"), CapabilityApi.FILTER_REACHABLE);

        sendUserId(user.getUserId());

        Intent intent = new Intent(this, DataLayerListenerService.class);
        intent.setAction(DataLayerListenerService.ACTION_SEND_MESSAGE);
        intent.putExtra(DataLayerListenerService.MESSAGE, "");
        intent.putExtra(DataLayerListenerService.PATH, BuildConfig.start_activity);
        startService(intent);

        intent = new Intent(this, DataLayerListenerService.class);
        intent.setAction(DataLayerListenerService.ACTION_SEND_MESSAGE);
        intent.putExtra(DataLayerListenerService.MESSAGE, "Messaging other device!");
        intent.putExtra(DataLayerListenerService.PATH, BuildConfig.some_path);
        startService(intent);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Connection to the wear API is halted
        Log.v(TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Connection to the wear API failed, try to restore it
        if (!mResolvingError) {
            if (result.hasResolution()) {
                try {
                    mResolvingError = true;
                    result.startResolutionForResult(this, 0);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                Log.e(TAG, "Connection to Google API client has failed");
                mResolvingError = false;
                Wearable.DataApi.removeListener(mGoogleApiClient, this);
                Wearable.MessageApi.removeListener(mGoogleApiClient, this);
                Wearable.CapabilityApi.removeListener(mGoogleApiClient, this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        // App is stopped, close the wear API connection
        if (!mResolvingError && (mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.CapabilityApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    private void sendUserId(String userId) {
        // Sends an asset through the Wear API
        PutDataMapRequest dataMap = PutDataMapRequest.create("/user");
        dataMap.getDataMap().putString("USER_ID", userId);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        Log.v(TAG, "Sending user id was successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });
    }
}
