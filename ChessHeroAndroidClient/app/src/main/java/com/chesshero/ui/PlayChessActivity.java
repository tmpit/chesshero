package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;

import com.chesshero.ui.chessboard.Chessboard;

/**
 * Created by Vasil on 30.11.2014 г..
 */
public class PlayChessActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //generate an empty chessboard
        Chessboard chessBoardView = new Chessboard(this, null);
        setContentView(chessBoardView);
    }
}
