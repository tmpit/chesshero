package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.chesshero.R;
import com.chesshero.ui.chessboard.Chessboard;

/**
 * Created by Vasil on 30.11.2014 г..
 */
public class PlayChessActivity extends Activity {

    private String mPlayerName = "proba player";
    private String mOponentName = "proba oponent";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //generate an empty chessboard
        setContentView(R.layout.play_chess);

        TextView playerName = (TextView) findViewById(R.id.playerName);
        playerName.setText(mPlayerName);

        TextView oponentName = (TextView) findViewById(R.id.oponentName);
        oponentName.setText(mOponentName);
    }
}
