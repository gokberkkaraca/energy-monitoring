package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        profileButton.setOnClickListener(this);
    }

    private void initializeViews() {
        profileButton = findViewById(R.id.profileButton);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.profileButton:
                startActivity(new Intent(DashboardActivity.this, UserProfileActivity.class));
                break;
            default:
                break;
        }
    }
}
