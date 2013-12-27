package com.kt.game;

import com.kt.api.Result;
import com.kt.game.chesspieces.*;
import com.kt.utils.SLog;

import java.util.ArrayList;

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
		SLog.write("Executing move...");

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

		SLog.write("moved piece: " + movedPiece);

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
			SLog.write("the chess piece does not move in that fashion");
			return Result.INVALID_MOVE;
		}

		BoardField toField = board[to.x][to.y];
		ChessPiece toPiece = toField.getChessPiece(); // The chess piece that is at the destination position
		Player pieceOwner = toPiece.getOwner(); // The owner of the chess piece at the destination position

		if (toPiece != null && pieceOwner.equals(executor))
		{	// Cannot take your own chess piece
			SLog.write("attempting to take your own chess piece");
			return Result.INVALID_MOVE;
		}

		if (movedPiece instanceof Pawn)
		{
			int vertical = Math.abs(to.y - from.y);
			int horizontal = to.x - from.y;

			// We don't need to make the basic move validation checks as isMoveValid has covered that
			if (0 == horizontal && toPiece != null)
			{	// Attempting to take a piece that is in front of the pawn
				SLog.write("attempting to take a piece in front of the pawn");
				return Result.INVALID_MOVE;
			}
			if (2 == vertical && (((Pawn)movedPiece).hasMoved() || isPathIntercepted(from, to)))
			{	// Attempting to move the pawn 2 positions forward twice or attempting to go over another chess piece
				SLog.write("attempting to move the pawn 2 positions forward twice or attempting to go over another chess piece with the pawn");
				return Result.INVALID_MOVE;
			}
		}
		else if ((movedPiece instanceof Queen || movedPiece instanceof Bishop || movedPiece instanceof Rook) && isPathIntercepted(from, to))
		{	// The path is not clear
			SLog.write("this chess piece cannot go over another one");
			return Result.INVALID_MOVE;
		}

		if (movedPiece instanceof King)
		{	// Check whether the move would make the king in check
			ArrayList<ChessPiece> opponentActivePieces = executor.getOpponent().getChessPieceSet().getActivePieces();

			for (ChessPiece piece : opponentActivePieces)
			{
				if (piece instanceof Pawn)
				{	// No need to check all pawns, skip them here, we do a simpler check after the loop
					continue;
				}

				if (piece.isMoveValid(to) && !isPathIntercepted(piece.getPosition(), to))
				{	// The king will be in check if the move is executed
					SLog.write("the king will be in check by: " + piece + " at position: " + piece.getPosition());
					return Result.WRONG_MOVE;
				}
			}

			// Check if the king would be in check by pawns at the destination position
			Position left, right;

			if (Color.WHITE == executor.getColor())
			{
				left = to.plus(MovementSet.UP_LEFT);
				right = to.plus(MovementSet.UP_RIGHT);
			}
			else
			{
				left = to.plus(MovementSet.DOWN_LEFT);
				right = to.plus(MovementSet.DOWN_RIGHT);
			}

			ChessPiece adjacent;
			// TODO: add logic for when the other king is in check by this player's moves
			if (left.isWithinBoard() && (adjacent = board[left.x][left.y].getChessPiece()) != null && adjacent instanceof Pawn)
			{	// There is a pawn to the left
				SLog.write("the king will be in check by a pawn :" + adjacent + " at position: " + adjacent.getPosition());
				return Result.WRONG_MOVE;
			}
			if (right.isWithinBoard() && (adjacent = board[right.x][right.y].getChessPiece()) != null && adjacent instanceof Pawn)
			{	// There is a pawn to the right
				SLog.write("the king will be in check by a pawn :" + adjacent + " at position: " + adjacent.getPosition());
				return Result.WRONG_MOVE;
			}
		}
		else
		{	// Check whether the move would make the king in check
			King myKing = executor.getChessPieceSet().getKing();
			Position kingPosition = myKing.getPosition();

			boolean horORVer = kingPosition.isHorizontalOrVerticalTo(from);
			boolean diagonal = false;

			if (horORVer || (diagonal = kingPosition.isDiagonalTo(from)))
			{	// The chess piece we are moving is positioned horizontally, vertically or diagonally relative to the king
				// Check if there is something between it and the king
				Position direction = MovementSet.directionFromPositions(kingPosition, from); // The direction from the king to the old position of the piece we are moving
				SLog.write("chess piece is at " + direction + " direction relative to the king");
				if (null == firstChessPieceInDirection(kingPosition, direction, from))
				{	// There is nothing between the king and the chess piece we are moving
					// Check if the new position of the chess piece clears a path to the king
					Position nextDirection = MovementSet.directionFromPositions(kingPosition, to); // The direction from the king to the new position of the piece we are moving
					SLog.write("chess piece would be at " + nextDirection + " direction relative to the king");
					if (!direction.equals(nextDirection))
					{	// The new position of the chess piece we are moving clears a path to the king
						ChessPiece interceptor = firstChessPieceInDirection(from, direction); // The chess piece the path to the king is cleared to

						if (interceptor != null && !interceptor.getOwner().equals(executor) &&
								(interceptor instanceof Queen || (horORVer && interceptor instanceof Rook) || (diagonal && interceptor instanceof Bishop)))
						{	// The chess piece is will make the king in check
							SLog.write("the king will be in check by " + interceptor + " at position: " + interceptor.getPosition());
							return Result.WRONG_MOVE;
						}
					}
				}
			}
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

	private boolean isPathIntercepted(Position from, Position to)
	{
		Position direction = MovementSet.directionFromPositions(from, to);
		return firstChessPieceInDirection(from, direction, to) != null;
	}

	// Returns the first chess piece in a direction starting from a position
	// Search starts at 'from' + 'direction'
	// Search ends when the end of the board is reached
	private ChessPiece firstChessPieceInDirection(Position from, Position direction)
	{
		return firstChessPieceInDirection(from, direction, null);
	}

	// Returns the first chess piece in a direction starting from a position
	// Search starts at 'from' + 'direction'
	// Search ends when 'end' is reached (if specified) or when the end of the board is reached, thus the 'end' position is not checked for a chess piece
	private ChessPiece firstChessPieceInDirection(Position from, Position direction, Position end)
	{
		Position cursor = from.clone();
		ChessPiece piece = null;
		BoardField board[][] = game.getBoard();

		do
		{
			cursor.add(direction);

			if (!cursor.isWithinBoard())
			{
				break;
			}

			if (end != null && cursor.equals(end))
			{
				break;
			}

			piece = board[cursor.x][cursor.y].getChessPiece();
		}
		while (null == piece);

		return piece;
	}
}
