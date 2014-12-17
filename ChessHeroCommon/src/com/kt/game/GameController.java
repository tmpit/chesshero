package com.kt.game;

import com.kt.utils.SLog;

/**
 * Created by Toshko on 12/16/14.
 */
public class GameController
{
	private Game game;
	private ChessMoveValidator moveExecutor;

	public GameController(Game game, ChessMoveValidator executor)
	{
		if (game.getState() != Game.STATE_INIT)
		{
			return;
		}

		this.game = game;
		this.moveExecutor = executor;

		if (game.wasResumed())
		{
			this.game.setState(Game.STATE_WAITING);
		}
		else
		{
			this.game.setState(Game.STATE_PENDING);
		}
	}

	public Game getGame()
	{
		return game;
	}

	public void addPlayer(Player player, Color color)
	{
		addPlayer(player, color, 0);
	}

	public boolean addPlayer(Player player, Color color, long millisPlayed)
	{
		if (game.getState() != Game.STATE_PENDING && game.getState() != Game.STATE_WAITING)
		{
			return false;
		}

		if (game.player1 != null && game.player2 != null)
		{
			return false;
		}

		if ((game.player1 != null && color == game.player1.color) || (game.player2 != null && color == game.player2.color))
		{
			return false;
		}

		if (game.player1 != null)
		{
			game.player2 = player;
		}
		else
		{
			game.player1 = player;
		}

		player.game = game;
		player.color = color;
		player.chessPieceSet = color == Color.WHITE ? game.whiteChessPieceSet : game.blackChessPieceSet;
		player.millisPlayed = millisPlayed;
		player.chessPieceSet.setOwner(player);

		return true;
	}

	public void removePlayers()
	{
		if (game.player1 != null)
		{
			removePlayer(game.player1);
			game.player1 = null;
		}

		if (game.player2 != null)
		{
			removePlayer(game.player2);
			game.player2 = null;
		}
	}

	private void removePlayer(Player player)
	{
		player.game = null;
		player.color = Color.NONE;
		player.chessPieceSet.setOwner(null);
		player.chessPieceSet = null;
		player.millisPlayed = 0;
	}

	public void startGame()
	{
		startGame(null);
	}

	public boolean startGame(Player turn)
	{
		if ((game.getState() != Game.STATE_WAITING && game.getState() != Game.STATE_PENDING) || game.player1 == null || game.player2 == null)
		{
			return false;
		}

		game.setState(Game.STATE_ACTIVE);
		game.initializeBoard();
		moveExecutor.setGame(game);
		moveExecutor.setBoard(game.getBoard());

		if (turn != null)
		{
			game.turn = turn;
		}
		else if (Color.WHITE == game.player1.getColor())
		{
			game.turn = game.player1;
		}
		else
		{
			game.turn = game.player2;
		}

		GameClock clock = game.getClock();

		if (clock != null)
		{
			clock.start();
		}

		return true;
	}

	public void endGame(Player winner, boolean checkmate)
	{
		if (game.getState() != Game.STATE_ACTIVE && game.getState() != Game.STATE_PAUSED)
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
		game.winner = winner;
		game.checkmate = checkmate;

		GameClock clock = game.getClock();

		if (clock != null)
		{
			clock.interrupt();
		}
	}

	public void pauseGame()
	{
		if (game.getState() != Game.STATE_ACTIVE)
		{
			return;
		}

		game.setState(Game.STATE_PAUSED);
	}

	public void resumeGame()
	{
		if (game.getState() != Game.STATE_PAUSED)
		{
			return;
		}

		game.setState(Game.STATE_ACTIVE);
	}

	public void saveGame()
	{
		if (game.getState() != Game.STATE_ACTIVE && game.getState() != Game.STATE_PAUSED)
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
	}

	public int executeMove(Player player, String move)
	{
		ChessMoveResult result = moveExecutor.executeMove(player, move);

		if (result.checkmate)
		{
			endGame(result.winner, true);
		}

		return result.resultCode;
	}
}
