package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button createAccountButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeViews();
        createAccountButton.setOnClickListener(this);
    }

    private void initializeViews() {
        createAccountButton = findViewById(R.id.createAccountButton);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.createAccountButton:
                createAccount();
            default:
                break;
        }
    }

    private void createAccount() {
        // TODO Create account code goes here
    }
}
