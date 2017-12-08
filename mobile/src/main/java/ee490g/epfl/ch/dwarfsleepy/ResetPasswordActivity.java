package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ee490g.epfl.ch.dwarfsleepy.database.*;

public class ResetPasswordActivity extends AppCompatActivity {

    Button resetPasswordButton;

    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
        setButtonClickListener();
    }

    private void initViews() {
        resetPasswordButton = findViewById(R.id.resetPasswordButton) ;
        emailEditText = findViewById(R.id.emailEditText);
    }

    private void setButtonClickListener() {
       resetPasswordButton.setOnClickListener(new ButtonClickListener());
    }

    private void performPasswordReset() {
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new FirebaseAuthManager().sendPasswordReset(emailEditText.getText().toString(), new Success() {
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
        });
    }


    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.resetPasswordButton:
                    performPasswordReset();
                default:
                    break;
            }
        }
    }

}
