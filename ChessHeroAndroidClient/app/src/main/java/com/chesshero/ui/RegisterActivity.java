package com.chesshero.ui;

/**
 * Created by Lyubomira on 11/30/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chesshero.R;


public class RegisterActivity extends Activity {

    private Intent pageToOpen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set View to register.xml
        setContentView(R.layout.register);
    }

    public void openLoginPage(View view) {
        pageToOpen = new Intent(this, MainActivity.class);
        startActivity(pageToOpen);
    }
}