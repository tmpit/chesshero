package com.chesshero.ui;

/**
 * Created by Lyubomira on 11/30/2014.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.chesshero.R;


public class RegisterActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);

        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Closing registration screen
                // Switching to Login Screen/closing register screen
                finish();
            }
        });
    }
}