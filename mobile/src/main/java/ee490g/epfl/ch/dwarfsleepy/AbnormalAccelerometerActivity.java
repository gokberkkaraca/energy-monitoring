package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ee490g.epfl.ch.dwarfsleepy.adapter.AbnormalAccelerometerAdapter;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.abnormalAccelerometerEvents;

public class AbnormalAccelerometerActivity extends AppCompatActivity {

    AbnormalAccelerometerAdapter abnormalAccelerometerAdapter;
    private User user;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_accelerometer);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        abnormalAccelerometerAdapter = new AbnormalAccelerometerAdapter(abnormalAccelerometerEvents);
        abnormalAccelerometerAdapter.notifyDataSetChanged();

        recyclerView = findViewById(R.id.abnormal_accelerometer_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(abnormalAccelerometerAdapter);

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                abnormalAccelerometerAdapter.notifyDataSetChanged();
            }
        };
        handler.postDelayed(r, 0);
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDayMonitoringActivity(this, user);
    }
}
