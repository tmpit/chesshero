package com.kt.game;

import com.kt.api.Result;
import com.kt.game.chesspieces.*;
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

	public void endGame(Player winner)
	{
		if (Game.STATE_FINISHED == game.getState())
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
		game.winner = winner;
	}

	public int execute(Player executor, Position from, Position to)
	{
		if (game.getState() != Game.STATE_STARTED)
		{	// Game has not started yet
			return Result.NOT_PLAYING;
		}

		if (!executor.equals(game.turn))
		{	// Attempting to make two consecutive moves
			return Result.NOT_YOUR_TURN;
		}

		BoardField board[][] = game.getBoard();

		BoardField fromField = board[from.x][from.y];
		ChessPiece movedPiece = fromField.getChessPiece(); // The chess piece that is being moved

		if (null == movedPiece)
		{	// There is no chess piece at that position in the board
			return Result.NO_CHESSPIECE;
		}

		if (!movedPiece.getOwner().equals(executor))
		{	// Attempting to move your opponent's chess piece
			return Result.NOT_YOUR_CHESSPIECE;
		}

		if (!movedPiece.isMoveValid(to))
		{	// This chess piece does move in that fashion
			return Result.INVALID_MOVE;
		}

		BoardField toField = board[to.x][to.y];
		ChessPiece toPiece = toField.getChessPiece(); // The chess piece that is at the destination position
		Player pieceOwner = toPiece.getOwner(); // The owner of the chess piece at the destination position

		if (toPiece != null && pieceOwner.equals(executor))
		{	// Cannot take your own chess piece
			return Result.INVALID_MOVE;
		}

		if (movedPiece instanceof Pawn)
		{
			int vertical = Math.abs(to.y - from.y);
			int horizontal = to.x - from.y;

			// We don't need to make the basic move validation checks as isMoveValid has covered that
			if (0 == horizontal && toPiece != null)
			{	// Attempting to take a piece that is in front of the pawn
				return Result.INVALID_MOVE;
			}
			if (2 == vertical && ((Pawn)movedPiece).hasMoved())
			{	// Attempting to move the pawn 2 positions forward twice
				return Result.INVALID_MOVE;
			}
		}
		else if ((movedPiece instanceof Queen || movedPiece instanceof Bishop || movedPiece instanceof Rook) && isPathIntercepted(from, to))
		{	// The path is not clear
			return Result.INVALID_MOVE;
		}

		if (toPiece != null)
		{
			pieceOwner.takePiece(toPiece);
		}

		movedPiece.setPosition(to);
		fromField.setChessPiece(null);
		toField.setChessPiece(movedPiece);
		game.turn = executor.getOpponent();

		return Result.OK;
	}

	boolean isPathIntercepted(Position from, Position to)
	{
		// Normalizing the vector
		int dx = to.x - from.x;
		int dy = to.y - from.y;
		Position step = new Position(dx / Math.abs(dx), dy / Math.abs(dy));
		Position cursor = from.clone();
		BoardField board[][] = game.getBoard();
		boolean result = false;

		do
		{
			cursor.add(step);

			if (cursor.equals(to))
			{
				break;
			}

			if (board[cursor.x][cursor.y].getChessPiece() != null)
			{
				result = true;
				break;
			}
		}
		while (true);

		return result;
	}
}
