package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.chesshero.R;
import com.kt.game.BoardField;
import com.kt.game.Game;
import com.kt.game.Position;



public class MainActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}

