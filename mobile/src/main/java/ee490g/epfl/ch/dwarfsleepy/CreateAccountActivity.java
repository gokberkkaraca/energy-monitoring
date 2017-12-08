package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ee490g.epfl.ch.dwarfsleepy.database.*;

public class CreateAccountActivity extends AppCompatActivity {

    Button createAccountButton;

    EditText nameEditText;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initViews();
        setButtonClickListener();
    }

    private void initViews() {
        createAccountButton = findViewById(R.id.createAccountButton);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    private void setButtonClickListener() {
        createAccountButton.setOnClickListener(new ButtonClickListener());
    }

    private void performCreateAccount() {
        new FirebaseAuthManager().signUp(CreateAccountActivity.this, nameEditText.getText().toString(),
                emailEditText.getText().toString(), passwordEditText.getText().toString(),
                new Success() {
                    @Override
                    public void onSuccess() {
                        finish();
                    }
                }, new Failure() {
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.createAccountButton:
                    performCreateAccount();
                default:
                    break;
            }
        }
    }

}
