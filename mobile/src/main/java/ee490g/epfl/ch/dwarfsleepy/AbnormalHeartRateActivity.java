package ee490g.epfl.ch.dwarfsleepy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class AbnormalHeartRateActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_heart_rate);

        recyclerView = findViewById(R.id.abnormal_hr_recycler_view);

    }
}
