package com.kt.game;

import com.kt.api.Result;
import com.kt.utils.SLog;

import java.util.ArrayList;

/**
 * Created by Toshko on 12/17/14.
 */
public abstract class ChessMoveValidator implements ChessMoveExecutor
{
	private Game game;
	private BoardField board[][];

	protected void setGame(Game game)
	{
		this.game = game;
	}

	protected Game getGame()
	{
		return game;
	}

	protected void setBoard(BoardField[][] board)
	{
		this.board = board;
	}

	protected BoardField[][] getBoard()
	{
		return board;
	}

	protected int validateMove(Player executor, ChessPiece movedPiece, Position from, Position to, MoveContext ctx)
	{
		ChessPiece toPiece = board[to.x][to.y].getChessPiece(); // The chess piece that is at the destination position
		ChessPiece take = toPiece;
		Pawn doubleMove = null;
		Rook castle = null;
		Position castlePosition = null;

		if (toPiece != null && toPiece.getOwner().equals(executor))
		{	// Cannot take your own chess piece
			SLog.write("attempting to take your own chess piece");
			return Result.INVALID_MOVE;
		}

		// Validate the movement of the chess piece
		if (movedPiece instanceof Pawn && null == toPiece && movedPiece.isMoveValid(to, true)) // Check for en passant
		{
			Pawn lastPawnRunner = game.lastPawnRunner;

			if (null == lastPawnRunner || executor.equals(lastPawnRunner.getOwner()))
			{	// Attempting to move diagonally to an empty space and en passant does not apply
				SLog.write("attempting invalid en passant - no runner or runner's owner is executor");
				return Result.INVALID_MOVE;
			}

			// The position of the pawn that this pawn should take en passant
			Position runnerPosition = (Color.WHITE == movedPiece.getColor() ? to.plus(MovementSet.DOWN) : to.plus(MovementSet.UP));

			if (!runnerPosition.equals(lastPawnRunner.getPosition()))
			{	// The runner position does not match the position of the last runner - en passant does not apply
				SLog.write("attempting invalid en passant - no runner to intercept");
				return Result.INVALID_MOVE;
			}

			take = lastPawnRunner;
		}
		else if (movedPiece instanceof King && null == toPiece && !movedPiece.isMoveValid(to, false)) // Check for castling
		{
			int vertical = to.x - from.x;

			if (from.y != to.y || (vertical != 2 && vertical != -2))
			{	// This is not an attempt for castling and the move is not valid
				SLog.write("the king does not move in that fashion");
				return Result.INVALID_MOVE;
			}

			if (((King)movedPiece).hasMoved())
			{	// The king has moved - cannot perform castling
				SLog.write("attempting invalid castling - the king has moved");
				return Result.INVALID_MOVE;
			}

			if (game.inCheck != null && executor.equals(game.inCheck))
			{	// The player's king is in check
				SLog.write("attempting invalid castling - player in check");
				return Result.INVALID_MOVE;
			}

			int x = (vertical > 0 ? Game.BOARD_SIDE - 1 : 0);
			int y = (Color.WHITE == executor.getColor() ? 0 : Game.BOARD_SIDE - 1);

			ChessPiece rookMaybe = board[x][y].getChessPiece();

			if (null == rookMaybe || !(rookMaybe instanceof Rook) || ((Rook)rookMaybe).hasMoved())
			{	// No rook, not rook or rook has already moved
				SLog.write("attempting invalid castling - no rook, not rook or rook has moved");
				return Result.INVALID_MOVE;
			}

			if (isPathIntercepted(from, new Position(x, y)))
			{	// The path from the king to the rook is not clear
				SLog.write("attempting invalid castling - path not clear");
				return Result.INVALID_MOVE;
			}

			// Check whether the king would pass through a field that is attacked by an enemy piece
			Position passThroughPosition = new Position(from.x + (2 == vertical ? 1 : -1), y); // One step towards the rook
			ChessPiece attacker = positionAttacker(passThroughPosition, executor.getOpponent().getChessPieceSet().getActivePieces());

			if (attacker != null)
			{	// The king is passing through a filed that is attacked by an enemy piece
				SLog.write("attempting invalid castling - king passing through " + passThroughPosition + " which is attacked by " + attacker + " at position " + attacker.getPosition());
				return Result.INVALID_MOVE;
			}

			castle = (Rook)rookMaybe;
			castlePosition = passThroughPosition;
		}
		else if (!movedPiece.isMoveValid(to, (toPiece != null)))
		{	// This chess piece does move in that fashion
			SLog.write("the chess piece does not move in that fashion");
			return Result.INVALID_MOVE;
		}

		if (movedPiece instanceof Pawn && null == take)
		{	// Additional checks for the pawn are needed only when it is moving forward, otherwise the isMoveValid method covers everything else
			int vertical = Math.abs(to.y - from.y);

			if (2 == vertical)
			{
				if (((Pawn)movedPiece).hasMoved() || isPathIntercepted(from, to))
				{	// Attempting to move the pawn 2 positions forward twice or attempting to go over another chess piece
					SLog.write("attempting to move the pawn 2 positions forward twice or attempting to go over another chess piece with the pawn");
					return Result.INVALID_MOVE;
				}

				doubleMove = (Pawn)movedPiece;
			}
		}
		else if ((movedPiece instanceof Queen || movedPiece instanceof Bishop || movedPiece instanceof Rook) && isPathIntercepted(from, to))
		{	// The path is not clear
			SLog.write("this chess piece cannot go over another one");
			return Result.INVALID_MOVE;
		}

		if (movedPiece instanceof King)
		{	// Check whether the move would make the king in check
			ChessPiece threat = positionAttacker(to, executor.getOpponent().getChessPieceSet().getActivePieces());

			if (threat != null)
			{
				SLog.write("the king will be in check by: " + threat + " at position: " + threat.getPosition());
				return Result.WRONG_MOVE;
			}
		}
		else
		{	// Check whether the move would make the king in check
			Position myKingPosition = executor.getChessPieceSet().getKing().getPosition();

			boolean horORVer = myKingPosition.isHorizontalOrVerticalTo(from);
			boolean diagonal = false;

			if (horORVer || (diagonal = myKingPosition.isDiagonalTo(from)))
			{	// The chess piece we are moving is positioned horizontally, vertically or diagonally relative to the king
				// Check if there is something between it and the king
				Position direction = MovementSet.directionFromPositions(myKingPosition, from); // The direction from the king to the old position of the piece we are moving
				SLog.write("chess piece is at " + direction + " direction relative to the king");

				if (null == firstChessPieceInDirection(myKingPosition, direction, from))
				{	// There is nothing between the king and the chess piece we are moving
					// Check if the new position of the chess piece clears a path to the king
					Position nextDirection = MovementSet.directionFromPositions(myKingPosition, to); // The direction from the king to the new position of the piece we are moving
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

		if (ctx != null)
		{
			ctx.take = take;
			ctx.doubleMove = doubleMove;
			ctx.castle = castle;
			ctx.castlePosition = castlePosition;
		}

		return Result.OK;
	}

	/**
	 * Checks whether a path specified by two positions is intercepted by a chess piece.
	 * The path does not include the {@code from} and {@code to} positions
	 * @param from The starting {@code Position}
	 * @param to The destination {@code Position}
	 * @return True if the path is intercepted by a chess piece, false if not
	 */
	protected boolean isPathIntercepted(Position from, Position to)
	{
		Position direction = MovementSet.directionFromPositions(from, to);
		return firstChessPieceInDirection(from, direction, to) != null;
	}

	/**
	 * Returns the first chess piece in a direction starting from a position. The search starts at
	 * {@code from} + {@code direction} and ends when a chess piece is found or the end of the board
	 * is reached
	 * @param from The starting {@code Position}
	 * @param direction A {@code Position} representing the direction
	 * @return The first {@code ChessPiece} found or null if no chess piece can be found in the specified
	 * direction
	 */
	protected ChessPiece firstChessPieceInDirection(Position from, Position direction)
	{
		return firstChessPieceInDirection(from, direction, null);
	}

	/**
	 * Returns the first chess piece in a direction starting from a position. The search starts at
	 * {@code from} + {@code direction} and ends when a chess piece is found, the end of the board
	 * is reached or {@code end} is reached (if specified). {@code end} is not checked for a chess piece,
	 * so if the path is clear up to {@code end} and there is a chess piece at {@code end}, it will not be returned
	 * @param from The starting {@code Position}
	 * @param direction A {@code Position} representing the direction
	 * @return The first {@code ChessPiece} found or null if no chess piece can be found in the specified
	 * direction
	 */
	protected ChessPiece firstChessPieceInDirection(Position from, Position direction, Position end)
	{
		Position cursor = from.clone();
		ChessPiece piece = null;

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

	/**
	 * Returns the first chess piece out of the specified list that attacks the specified position
	 * @param pos A {@code Position} to check against
	 * @param pieces An {@code ArrayList} of {@code ChessPiece} instances
	 * @return The first {@code ChessPiece} that attacks {@code pos}
	 */
	protected ChessPiece positionAttacker(Position pos, ArrayList<ChessPiece> pieces)
	{
		for (ChessPiece piece : pieces)
		{
			if (piece instanceof Pawn)
			{	// No need to check all pawns, skip them here, we do a simpler check after the loop
				continue;
			}

			if (piece.isMoveValid(pos, true) && (piece instanceof Knight || !isPathIntercepted(piece.getPosition(), pos)))
			{	// Position is threatened by this chess piece
				if (!pos.equals(piece.getPosition()))
				{
					return piece;
				}
			}
		}

		// Check if the position is threatened by pawns
		Position left, right;
		Color threatColor = pieces.get(0).getColor();

		if (Color.BLACK == threatColor)
		{	// Black is positioned at the top
			left = pos.plus(MovementSet.UP_LEFT);
			right = pos.plus(MovementSet.UP_RIGHT);
		}
		else
		{	// White is positioned at the bottom
			left = pos.plus(MovementSet.DOWN_LEFT);
			right = pos.plus(MovementSet.DOWN_RIGHT);
		}

		ChessPiece adjacent;

		if (left.isWithinBoard() && (adjacent = board[left.x][left.y].getChessPiece()) != null && threatColor == adjacent.getColor() && adjacent instanceof Pawn)
		{	// There is a pawn to the left
			return adjacent;
		}
		if (right.isWithinBoard() && (adjacent = board[right.x][right.y].getChessPiece()) != null && threatColor == adjacent.getColor() && adjacent instanceof Pawn)
		{	// There is a pawn to the right
			return adjacent;
		}

		return null;
	}

	/**
	 * The MoveContext class is used to transfer special-move-related information between the validation
	 * and execution methods
	 */
	public class MoveContext
	{
		public ChessPiece take = null;

		public Pawn doubleMove = null;

		public Rook castle = null;
		public Position castlePosition = null;

		@Override
		public String toString()
		{
			return "<MoveContext :: take: " + take + ", doubleMove: " + doubleMove + ", castle: " + castle + ", castlePosition: " + castlePosition + ">";
		}
	}
}
