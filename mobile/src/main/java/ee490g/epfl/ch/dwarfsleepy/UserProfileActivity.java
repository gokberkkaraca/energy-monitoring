package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private User user;

    private Button logOutButton;
    private TextView nameTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        setViews();
        logOutButton.setOnClickListener(this);
    }

    private void initializeViews() {
        logOutButton = findViewById(R.id.logoutButton);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
    }

    private void setViews() {
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logoutButton:
                logOut();
                startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDashboardActivity(this, user);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        NavigationHandler.goToLoginActivity(this);
    }

}
