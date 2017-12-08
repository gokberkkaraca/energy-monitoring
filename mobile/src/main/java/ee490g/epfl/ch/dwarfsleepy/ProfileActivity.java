package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ee490g.epfl.ch.dwarfsleepy.database.FirebaseAuthManager;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuthManager authManager = new FirebaseAuthManager();

    private Button logoutButton;

    private TextView userTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setButtonClickListener();
    }

    private void initViews() {
        logoutButton = findViewById(R.id.logoutButton);

        userTextView = findViewById(R.id.userTextView);
        emailTextView = findViewById(R.id.emailTextView);

        userTextView.setText(authManager.getCurrentUser().getDisplayName());
        emailTextView.setText(authManager.getCurrentUser().getEmail());
    }

    private void setButtonClickListener() {
        logoutButton.setOnClickListener(new ButtonClickListener());
    }

    private void performLogout() {
        authManager.signOut();
    }


    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.logoutButton:
                    performLogout();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                default:
                    break;
            }
        }
    }
}
