package com.kt.game;

import com.kt.api.Result;
import com.kt.game.chesspieces.ChessPiece;
import com.kt.utils.SLog;

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

		if (!executor.equals(game.turn))
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

		if (!piece.getOwner().equals(executor))
		{
			return Result.NOT_YOUR_CHESSPIECE;
		}

		if (!piece.isMoveValid(to))
		{
			return Result.INVALID_MOVE;
		}

		BoardField toField = board[to.x][to.y];
		ChessPiece toPiece = toField.getChessPiece();

		if (toPiece != null)
		{
			Player owner = toPiece.getOwner();

			if (owner.equals(executor))
			{	// Cannot take your own chess piece
				return Result.INVALID_MOVE;
			}

			owner.takePiece(toPiece);
		}

		fromField.setChessPiece(null);
		toField.setChessPiece(piece);
		game.turn = executor.getOpponent();

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

		if (Color.WHITE == game.player1.getColor())
		{
			game.turn = game.player1;
		}
		else
		{
			game.turn = game.player2;
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
