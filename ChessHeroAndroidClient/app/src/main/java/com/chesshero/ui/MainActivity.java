package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chesshero.R;


public class MainActivity extends Activity {

    private Intent pageToOpen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting default screen to login.xml
        setContentView(R.layout.login);
    }

    public void openRegisterPage(View view) {
        pageToOpen = new Intent(this, RegisterActivity.class);
        startActivity(pageToOpen);
    }

    public void openPlayChessPage(View view) {
        pageToOpen = new Intent(this, PlayChessActivity.class);
        startActivity(pageToOpen);
    }
}

