package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logoutButton;

    private TextView userTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setViews();
    }

    private void initializeViews() {
        logoutButton = findViewById(R.id.logoutButton);
        userTextView = findViewById(R.id.userTextView);
        emailTextView = findViewById(R.id.emailTextView);
    }

    private void setViews() {
        // TODO Will be changed when user class is created
        userTextView.setText("");
        emailTextView.setText("");
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.logoutButton:
                logOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            default:
                break;
        }
    }

    private void logOut() {
        // TODO Logout code here
    }

}
