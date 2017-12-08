package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import ee490g.epfl.ch.dwarfsleepy.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setButtonClickListener();
    }

    private void initViews() {
        profileButton = findViewById(R.id.profileButton);
    }

    private void setButtonClickListener() {
        profileButton.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.profileButton:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                default:
                    break;
            }
        }
    }
}
