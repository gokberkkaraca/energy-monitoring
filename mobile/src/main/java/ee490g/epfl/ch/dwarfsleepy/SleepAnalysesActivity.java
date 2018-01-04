package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ee490g.epfl.ch.dwarfsleepy.adapter.SleepAnalysesAdapter;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightHeartRates;

public class SleepAnalysesActivity extends AppCompatActivity {

    private SleepAnalysesAdapter sleepAnalysesAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_analyses);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        sleepAnalysesAdapter = new SleepAnalysesAdapter(nightHeartRates);
        sleepAnalysesAdapter.notifyDataSetChanged();

        RecyclerView recyclerView = findViewById(R.id.sleep_anayses_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sleepAnalysesAdapter);

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                sleepAnalysesAdapter.notifyDataSetChanged();
            }
        };
        handler.postDelayed(r, 0);
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToNightMonitoringActivity(this, user);
    }
}
