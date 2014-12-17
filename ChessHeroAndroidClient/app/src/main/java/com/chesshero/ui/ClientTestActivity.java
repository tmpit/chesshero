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
import com.chesshero.client.parsers.GameTicket;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;
import com.kt.game.Color;
import com.kt.game.Game;
import com.kt.game.Position;

import java.util.ArrayList;
import java.util.List;

public class ClientTestActivity extends Activity implements EventCenterObserver
{
	Client client;
	TextView usernameField;
	TextView passwordField;
	TextView moveField;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_test);

		usernameField = (TextView)findViewById(R.id.username_field);
		passwordField = (TextView)findViewById(R.id.password_field);
		moveField = (TextView)findViewById(R.id.move_field);

		client = ((ChessHeroApplication)getApplication()).getClient();
		EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.REGISTER_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.LOGOUT);
		EventCenter.getSingleton().addObserver(this, Client.Event.CANCEL_GAME_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.PENDING_GAMES_LOAD_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.EXIT_GAME_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.MOVE_RESULT);
		EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_PUSH);
		EventCenter.getSingleton().addObserver(this, Client.Event.END_GAME_PUSH);
		EventCenter.getSingleton().addObserver(this, Client.Event.MOVE_PUSH);
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
		client.createGame("cool game name", Color.WHITE);
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
		List<GameTicket> games = client.getCachedPendingGames();

		if (null == games || 0 == games.size())
		{
			return;
		}

		client.joinGame(games.get(0));
	}

	public void exitGameButtonWasTapped(View view)
	{
		client.exitGame();
	}

	public void executeMoveButtonWasTapped(View view)
	{
		String move = moveField.getText().toString().trim();

		if (move.length() != 4 && move.length() != 5)
		{
			return;
		}

		Position from = Position.positionFromBoardPosition(move.substring(0, 2));
		Position to = Position.positionFromBoardPosition(move.substring(2, 4));

		client.executeMove(from, to);
	}

	@Override
	public void eventCenterDidPostEvent(String eventName, Object userData)
	{
		if (eventName == Client.Event.LOGIN_RESULT)
		{
			log("did complete login with result code " + userData);

			if (userData != null && (Integer)userData == Result.OK)
			{
				log(client.getPlayer().toString());
			}
		}
		else if (eventName == Client.Event.REGISTER_RESULT)
		{
			log("did complete register with result code " + userData);

			if (userData != null && (Integer)userData == Result.OK)
			{
				log(client.getPlayer().toString());
			}
		}
		else if (eventName == Client.Event.LOGOUT)
		{
			log("did logout");
		}
		else if (eventName == Client.Event.CANCEL_GAME_RESULT)
		{
			log("did complete cancel game with result code " + userData);
		}
		else if (eventName == Client.Event.CREATE_GAME_RESULT)
		{
			log("did complete create game with result code " + userData);

			if (userData != null && (Integer)userData == Result.OK)
			{
				log(client.getGame().toString());
			}
		}
		else if (eventName == Client.Event.PENDING_GAMES_LOAD_RESULT)
		{
			log("did complete pending games load with result code " + userData);

			if (userData != null && (Integer)userData == Result.OK)
			{
				if (client.getCachedPendingGames() != null)
				{
					log(client.getCachedPendingGames().toString());
				}
				else
				{
					log("no pending games");
				}
			}
		}
		else if (eventName == Client.Event.JOIN_GAME_RESULT)
		{
			log("did complete join game with result code " + userData);

			if (userData != null && (Integer)userData == Result.OK)
			{
				log(client.getGame().toString());
			}
		}
		else if (eventName == Client.Event.EXIT_GAME_RESULT)
		{
			log("did complete exit game with result code " + userData);
		}
		else if (eventName == Client.Event.MOVE_RESULT)
		{
			log("did complete move with result code " + userData);

			if (userData != null && (Integer)userData == Result.OK)
			{
				log(client.getGame().toString());
			}
		}
		else if (eventName == Client.Event.JOIN_GAME_PUSH)
		{
			log("did receive join game push");
			log(client.getGame().toString());
		}
		else if (eventName == Client.Event.END_GAME_PUSH)
		{
			log("did receive end game push");
			log(client.getGame().toString());
		}
		else if (eventName == Client.Event.MOVE_PUSH)
		{
			log("did receive move push");
			log(client.getGame().toString());
		}
	}

	private void log(String text)
	{
		Log.e("ClientTestActivity", text);
	}
}
