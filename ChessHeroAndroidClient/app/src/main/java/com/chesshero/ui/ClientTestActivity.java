package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.chesshero.R;
import com.chesshero.client.ChessHeroApplication;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.game.Color;
import com.kt.game.Game;

public class ClientTestActivity extends Activity implements EventCenterObserver
{
	Client client;
	TextView usernameField;
	TextView passwordField;
	TextView gamenameField;
	RadioGroup colorRadioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_test);

		usernameField = (TextView)findViewById(R.id.username_field);
		passwordField = (TextView)findViewById(R.id.password_field);
		gamenameField = (TextView)findViewById(R.id.gamename_field);
		colorRadioGroup = (RadioGroup)findViewById(R.id.color_rgrp);

		client = ((ChessHeroApplication)getApplication()).getClient();
		EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.REGISTER_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.LOGOUT);
		EventCenter.getSingleton().addObserver(this, Client.Event.CANCEL_GAME_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.PENDING_GAMES_LOAD_RESULT);
	}

	public void logoutButtonWasTapped(View view)
	{
		client.logout();
	}

	public void loginButtonWasTapped(View view)
	{
		client.login(usernameField.getText().toString(), passwordField.getText().toString());
	}

	public void registerButtonWasTapped(View view)
	{
		client.register(usernameField.getText().toString(), passwordField.getText().toString());
	}

	public void createGameButtonWasTapped(View view)
	{
		Color color = null;

		if (colorRadioGroup.getCheckedRadioButtonId() == R.id.black_rbtn)
		{
			color = Color.BLACK;
		}
		else
		{
			color = Color.WHITE;
		}

		client.createGame(gamenameField.getText().toString(), color, Game.NO_TIMEOUT);
	}

	public void cancelGameButtonWasTapped(View view)
	{
		client.cancelGame();
	}

	public void fetchPendingGamesButtonWasTapped(View view)
	{
		client.loadPendingGames();
	}

	public void joinGameButtonWasTapped(View view)
	{
	}

	public void exitGameButtonWasTapped(View view)
	{
	}

	public void executeMoveButtonWasTapped(View view)
	{
	}

	@Override
	public void eventCenterDidPostEvent(String eventName, Object userData)
	{
		Log.e("ClientTestActivity", "did receive event with name " + eventName + " user data " + userData);
	}
}
