package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ee490g.epfl.ch.dwarfsleepy.adapter.PhysicalActivityAdapter;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.physicalActivities;

public class GoogleFitActivity extends AppCompatActivity {

    private User user;

    private PhysicalActivityAdapter physicalActivityAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        physicalActivityAdapter = new PhysicalActivityAdapter(physicalActivities);
        physicalActivityAdapter.notifyDataSetChanged();

        recyclerView = findViewById(R.id.physicalActivityRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(physicalActivityAdapter);
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDayMonitoringActivity(this, user);
    }
}