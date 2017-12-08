package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginButton;
    private Button createAccountButton;
    private Button forgotPasswordButton;

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.loginButton:
                login();
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

    private void initializeViews() {
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordBtton);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    private void login() {
        // TODO Login code goes here
    }
}
