package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class GoogleAccountActivity extends CreateAccountActivity {

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_account);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeViews();
        createAccountButton.setOnClickListener(this);
        birthdayEditText.setOnFocusChangeListener(this);
        nameEditText.setText(firebaseUser.getDisplayName());
        emailEditText.setText(firebaseUser.getEmail());

        calendar = Calendar.getInstance();
        birthday = null;
        gender = null;
    }

    @Override
    protected void initializeViews() {
        createAccountButton = findViewById(R.id.createAccountButton);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createAccountButton:
                name = nameEditText.getText().toString();
                email = emailEditText.getText().toString();
                birthday = calendar.getTime();
                gender = getGenderFromRadioGroup();
                createAccount(name, email, null);
                break;
            default:
                break;
        }
    }

    protected void createAccount(final String name, final String email, String password) {
        if (checkFields()) {
            User user = new User(firebaseUser, name, gender, birthday);
            DatabaseHandler.addUser(user);
            NavigationHandler.goToDashboardActivity(GoogleAccountActivity.this, user);
        }
    }

    @Override
    protected boolean checkFields() {
        if (name.isEmpty()) {
            Toast.makeText(this, "Name field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Email field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (birthday == null) {
            Toast.makeText(this, "Birthday field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (gender == null) {
            Toast.makeText(this, "Gender field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
