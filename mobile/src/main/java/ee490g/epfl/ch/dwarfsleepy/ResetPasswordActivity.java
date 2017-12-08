package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    Button resetPasswordButton;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
        resetPasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.resetPasswordButton:
                resetPassword();
                break;
            default:
                break;
        }
    }

    private void initViews() {
        resetPasswordButton = findViewById(R.id.resetPasswordButton) ;
        emailEditText = findViewById(R.id.emailEditText);
    }

    private void resetPassword() {
        // TODO Reset password code goes here
    }
}
