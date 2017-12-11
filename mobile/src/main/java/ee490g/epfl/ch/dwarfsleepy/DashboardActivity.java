package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import ee490g.epfl.ch.dwarfsleepy.user.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton profileButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

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
                NavigationHandler.goToUserProfileActivity(this, user);
                break;
            default:
                break;
        }
    }
}
