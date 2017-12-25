package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.service.DataLayerListenerService;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.*;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    // Tag for Logcat
    private static final String TAG = "DashboardActivity";
    public static User user;
    private ImageButton profileButton;
    private Button polarBeltButton;
    private Button accelerometerButton;
    private Button dayMonitoringButton;
    private Button nightMonitoringButton;
    private TextView heartRateTextView;

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

        fetchPreviousData();
        setMessageScheduler();
    }

    private void fetchPreviousData() {
        DatabaseHandler.getHeartRateData(user, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                averagedHeartRateDataList = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
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
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    AbnormalHeartRateEvent abnormalHeartRateEvent = postSnapshot.getValue(AbnormalHeartRateEvent.class);
                    abnormalHeartRateEvents.add(abnormalHeartRateEvent);
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
        scheduler.scheduleAtFixedRate(messageSender, 3, 3, TimeUnit.SECONDS);
    }

    private void initializeViews() {
        profileButton = findViewById(R.id.profileButton);
        polarBeltButton = findViewById(R.id.polarBeltButton);
        accelerometerButton = findViewById(R.id.accelerometerButton);
        dayMonitoringButton = findViewById(R.id.dayMonitoringButton);
        nightMonitoringButton = findViewById(R.id.nightMonitoringButton);
        heartRateTextView = findViewById(R.id.heartRateTextView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileButton:
                NavigationHandler.goToUserProfileActivity(this, user);
                break;
            case R.id.polarBeltButton:
                //TODO
                break;
            case R.id.accelerometerButton:
                break;
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
    public void onConnected(Bundle connectionHint) {
        // Connection to the wear API
        Log.v(TAG, "Google API Client was connected");
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);

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
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    boolean doubleBackToExitPressedOnce = false;

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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
