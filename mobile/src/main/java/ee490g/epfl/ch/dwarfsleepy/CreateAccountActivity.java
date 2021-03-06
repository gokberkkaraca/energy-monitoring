package ee490g.epfl.ch.dwarfsleepy;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.service.DataLayerListenerService;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    protected Button createAccountButton;
    protected EditText nameEditText;
    protected EditText emailEditText;
    protected EditText birthdayEditText;
    protected EditText weightEditText;
    protected EditText heightEditText;
    protected RadioGroup genderRadioGroup;
    protected Calendar calendar;
    protected String name;
    protected String email;
    protected Date birthday;
    protected User.Gender gender;
    protected String height;
    protected String weight;
    private FirebaseAuth mAuth;
    private EditText passwordEditText;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        createAccountButton.setOnClickListener(this);
        birthdayEditText.setOnFocusChangeListener(this);
        calendar = Calendar.getInstance();
        birthday = null;
        gender = null;
    }

    protected void initializeViews() {
        createAccountButton = findViewById(R.id.createAccountButton);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        weightEditText = findViewById(R.id.weightEditText);
        heightEditText = findViewById(R.id.heightEditText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createAccountButton:
                name = nameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                birthday = calendar.getTime();
                gender = getGenderFromRadioGroup();
                weight = weightEditText.getText().toString();
                height = heightEditText.getText().toString();
                createAccount(name, email, password, weight, height);
                break;
            default:
                break;
        }
    }

    protected User.Gender getGenderFromRadioGroup() {
        int radioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        View selectedButton = genderRadioGroup.findViewById(radioButtonId);
        String genderText = ((RadioButton) selectedButton).getText().toString();
        if ("Male".equals(genderText))
            return User.Gender.MALE;
        else
            return User.Gender.FEMALE;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.birthdayEditText:
                if (hasFocus)
                    new DatePickerDialog(this, this,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        birthday = calendar.getTime();
        birthdayEditText.setText(simpleDateFormat.format(birthday));
    }

    protected void createAccount(final String name, final String email, String password, final String weight, final String height) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (checkFields()) {
                                User user = new User(firebaseUser, name, gender, birthday, Double.parseDouble(weight), Integer.parseInt(height));
                                DatabaseHandler.addUser(user);
                                DataLayerListenerService.setUser(user);
                                NavigationHandler.goToDashboardActivity(CreateAccountActivity.this, user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected boolean checkFields() {
        if (name.isEmpty()) {
            Toast.makeText(this, "Name field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Email field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (birthday == null) {
            Toast.makeText(this, "Birthday field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (weight.isEmpty()) {
            Toast.makeText(this, "Weight field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (height.isEmpty()) {
            Toast.makeText(this, "Height field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (gender == null) {
            Toast.makeText(this, "Gender field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
