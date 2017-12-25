package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class DayMonitoringActivity extends AppCompatActivity {

    private User user;
    private TextView heartRateTextView;
    private TextView caloriesBurntTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_monitoring);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        int heartRate = 28;
        heartRateTextView.setText(String.valueOf(heartRate));

        setHeartRateView();
    }

    private void setHeartRateView() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                HeartRateData lastHeartRateData = averagedHeartRateDataList.get(averagedHeartRateDataList.size() - 1);
                int heartRateValue = (int) lastHeartRateData.getValue().floatValue();
                heartRateTextView.setText(String.valueOf(heartRateValue));
            }
        };
        handler.postDelayed(r, 0);
    }

    private void initializeViews() {
        heartRateTextView = findViewById(R.id.heartRateTextView);
        caloriesBurntTextView = findViewById(R.id.caloriesBurntTextView);
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDashboardActivity(this, user);
    }
}
