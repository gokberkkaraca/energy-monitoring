package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ee490g.epfl.ch.dwarfsleepy.database.*;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button createAccountButton;
    private Button forgotPasswordButton;

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setButtonClickListeners();
    }

    private void initViews() {
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordBtton);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    private void setButtonClickListeners() {
        loginButton.setOnClickListener(new ButtonClickListener());
        createAccountButton.setOnClickListener(new ButtonClickListener());
        forgotPasswordButton.setOnClickListener(new ButtonClickListener());
    }

    private void performLogin() {
        new FirebaseAuthManager().signIn(LoginActivity.this, emailEditText.getText().toString(),
                passwordEditText.getText().toString(), new Success() {
                    @Override
                    public void onSuccess(){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
                case R.id.loginButton:
                    performLogin();
                    break;
                case R.id.createAccountButton:
                    startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                    break;
                case R.id.forgotPasswordBtton:
                    startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                    break;
                default:
                    break;
            }
        }
    }
}
