package com.kt.game;

import com.kt.api.Result;
import com.kt.game.chesspieces.ChessPiece;

/**
 * Created by Toshko on 12/23/13.
 */
public class GameController
{
	private Game game;

	public GameController(Game game)
	{
		this.game = game;
		game.controller = this;
	}

	public int execute(Player executor, Position from, Position to)
	{
		if (game.getState() != Game.STATE_STARTED)
		{
			return Result.NOT_PLAYING;
		}

		if (executor != game.turn)
		{
			return Result.NOT_YOUR_TURN;
		}

		BoardField board[][] = game.getBoard();

		BoardField fromField = board[from.x][from.y];
		ChessPiece piece = fromField.getChessPiece();

		if (null == piece)
		{
			return Result.NO_CHESSPIECE;
		}

		if (piece.getOwner() != executor)
		{
			return Result.NOT_YOUR_CHESSPIECE;
		}

		if (!piece.isMoveValid(to))
		{
			return Result.INVALID_MOVE;
		}

		BoardField toField = board[to.x][to.y];

		if (toField.getChessPiece() != null)
		{
			return Result.INVALID_MOVE;
		}

		fromField.setChessPiece(null);
		toField.setChessPiece(piece);

		return Result.OK;
	}

	public void startGame()
	{
		if (Game.STATE_STARTED == game.getState())
		{
			return;
		}

		game.setState(Game.STATE_STARTED);
		game.initializeBoard();

		if (Color.WHITE == game.getPlayer1().getColor())
		{
			game.turn = game.getPlayer1();
		}
		else
		{
			game.turn = game.getPlayer2();
		}
	}

	// Used for prematurely ending a game
	public void endGame(Player winner)
	{
		if (Game.STATE_FINISHED == game.getState())
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
		game.winner = winner;
	}
}
