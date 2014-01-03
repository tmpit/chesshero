package com.kt.game;

import com.kt.api.Result;
import com.kt.game.chesspieces.*;
import com.kt.utils.SLog;

import java.util.*;

/**
 * Created by Toshko on 12/23/13.
 */
public class GameController
{
	private Game game;
	private BoardField board[][];

	public GameController(Game game)
	{
		this.game = game;
		this.board = game.getBoard();
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

	public int execute(Player executor, String move)
	{
		if (game.getState() != Game.STATE_STARTED)
		{	// Game has not started yet
			return Result.NOT_PLAYING;
		}

		if (!executor.equals(game.turn))
		{	// Attempting to make two consecutive moves
			return Result.NOT_YOUR_TURN;
		}

		SLog.write("Decoding move: " + move);

		Position from, to = from = Position.ZERO;
		char promotion = '\0';

		// Move should be in the Pure coordinate notation: http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
		move = move.toLowerCase();
		int moveLen = move.length();

		if (moveLen != 4 && moveLen != 5)
		{
			return Result.INVALID_MOVE_FORMAT;
		}

		from = Position.positionFromBoardPosition(move.substring(0, 2));

		if (null == from)
		{
			return Result.INVALID_MOVE_FORMAT;
		}

		to = Position.positionFromBoardPosition(move.substring(2, 4));

		if (null == to)
		{
			return Result.INVALID_MOVE_FORMAT;
		}

		if (5 == moveLen)
		{
			promotion = move.charAt(4);

			if (promotion != 'q' && promotion != 'r' && promotion != 'b' && promotion != 'n')
			{
				return Result.INVALID_MOVE_FORMAT;
			}
		}

		SLog.write("Executing move: from " + from + " to " + to + (promotion != '\0' ? " promotion " + promotion : ""));

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

		BoardField toField = board[to.x][to.y];
		ChessPiece toPiece = toField.getChessPiece(); // The chess piece that is at the destination position

		MoveContext context = new MoveContext();
		int moveResult = validateMove(executor, movedPiece, from, to, context);

		if (moveResult != Result.OK)
		{
			return moveResult;
		}

		SLog.write("move context: " + context);

		ChessPiece take = context.take;
		ChessPiece promoted = null;

		// Check whether pawn promotion is applicable
		if (movedPiece instanceof Pawn && ((Color.WHITE == movedPiece.getColor() && Game.BOARD_SIDE - 1 == to.y) || (Color.BLACK == movedPiece.getColor() && 0 == to.y)))
		{	// The pawn reaches its maximum rank
			switch (promotion)
			{
				case 'q':
					promoted = new Queen(to, executor, executor.getColor());
					break;

				case 'r':
					promoted = new Rook(to, executor, executor.getColor());
					break;

				case 'b':
					promoted = new Bishop(to, executor, executor.getColor());
					break;

				case 'n':
					promoted = new Knight(to, executor, executor.getColor());
					break;

				default:
					return Result.INVALID_MOVE_FORMAT;
			}
		}

		// Change positions of chess pieces
		// We do that before verification that the player's king is safe as it will be a lot faster and easier to do that check after positions have been updated
		// If the player cannot actually make that move we can just revert back
		movedPiece.setPosition(to);
		fromField.setChessPiece(null);
		toField.setChessPiece(movedPiece);

		ChessPieceSet myPieceSet = executor.getChessPieceSet();

		// Verify that the executor's king would be safe after the move
		Player inCheck = game.inCheck;

		if (inCheck != null && inCheck.equals(executor))
		{
			ArrayList<ChessPiece> attackers = game.attackers;
			Position myKingPosition = myPieceSet.getKing().getPosition();
			SLog.write("king is in check");

			for (Iterator<ChessPiece> iterator = attackers.iterator(); iterator.hasNext();)
			{
				ChessPiece piece = iterator.next();
				SLog.write("attacker: " + piece + " at position: " + piece.getPosition());

				if (piece == take || !piece.isMoveValid(myKingPosition, true) || (!(piece instanceof Knight) && isPathIntercepted(piece.getPosition(), myKingPosition)))
				{	// The piece is no longer a threat if it is to be taken or if it can no longer take the king
					SLog.write(piece + " no longer threatens the king");
					iterator.remove();
				}
			}

			if (attackers.size() != 0)
			{	// The king is still in check - revert positions back to how they were before this move was executed
				movedPiece.setPosition(from);
				fromField.setChessPiece(movedPiece);
				toField.setChessPiece(toPiece);

				return Result.WRONG_MOVE;
			}

			SLog.write("king is no longer in check");
			game.inCheck = null;
		}

		// Take opponents piece
		if (take != null)
		{
			SLog.write("taking piece: " + take + " at position: " + to);
			take.getOwner().takePiece(take);

			Position piecePosition = take.getPosition();

			if (!piecePosition.equals(to))
			{	// En passant
				board[piecePosition.x][piecePosition.y].setChessPiece(null);
			}
		}

		// Apply promotion
		if (promoted != null)
		{
			SLog.write("promoting " + movedPiece + " at position " + from + " to " + promoted);
			executor.takePiece(movedPiece);
			executor.addPiece(promoted);
			toField.setChessPiece(promoted);
		}

		Rook castle = context.castle;

		// Apply castling
		if (castle != null)
		{
			Position oldPos = castle.getPosition();
			Position castlePos = context.castlePosition;
			SLog.write("castling - moving rook from " + oldPos + " to " + castlePos);
			castle.setPosition(castlePos);
			board[castlePos.x][castlePos.y].setChessPiece(castle);
			board[oldPos.x][oldPos.y].setChessPiece(null);
		}

		Player opponent = executor.getOpponent();
		ChessPieceSet opponentPieceSet = opponent.getChessPieceSet();

		// Update whose turn it is
		game.turn = opponent;
		SLog.write("player to play next turn: " + opponent);

		// Update general game state
		game.lastPawnRunner = context.doubleMove;

		// Check whether this move would make the opponent's king in check
		Position opponentKingPosition = opponentPieceSet.getKing().getPosition();

		// If the moved piece is a knight, we do not need to check whether the path is clear
		if (movedPiece.isMoveValid(opponentKingPosition, true) && (movedPiece instanceof Knight || !isPathIntercepted(to, opponentKingPosition)))
		{	// The opponent's king is in check by the chess piece we just moved
			SLog.write("opponent's king is in check by: " + movedPiece + " at position: " + to);
			game.inCheck = opponent;
			game.attackers.add(movedPiece);
		}

		ChessPiece discovery = firstChessPieceInDirection(opponentKingPosition, from);

		// Ignore knights - they cannot be a threat if they are positioned horizontally, vertically or diagonally relative to the king
		if (discovery != null && discovery.getOwner().equals(executor) && !(discovery instanceof Knight) &&
				discovery.isMoveValid(opponentKingPosition, true) && !isPathIntercepted(discovery.getPosition(), opponentKingPosition))
		{	// The opponent's king is in check by a chess piece discovered by the move
			SLog.write("opponent's king is in check by discovery by: " + discovery + " at position: " + discovery.getPosition());
			game.inCheck = opponent;
			game.attackers.add(discovery);
		}

		// Check whether this is a checkmate
		checkmate:
		if (game.inCheck != null)
		{
			SLog.write("Checking for checkmate...\nchecking whether the king can escape");
			ArrayList<ChessPiece> myChessPieces = myPieceSet.getActivePieces();
			ArrayList<ChessPiece> attackers = game.attackers;

			// Check if the king can escape
			HashSet<Position> possibleEscapes = new HashSet<Position>(Arrays.asList(new Position[] {
					MovementSet.UP, MovementSet.UP_LEFT, MovementSet.LEFT, MovementSet.DOWN_LEFT,
					MovementSet.DOWN, MovementSet.DOWN_RIGHT, MovementSet.RIGHT, MovementSet.UP_RIGHT
			}));

			// Attempt to rule out some of the possible escape positions
			for (ChessPiece attacker : attackers)
			{
				boolean queen = false;
				boolean rook = false;
				Position attackerPosition = attacker.getPosition();

				if (attacker instanceof Bishop || (rook = attacker instanceof Rook) || (queen = attacker instanceof Queen))
				{
					Position direction = MovementSet.directionFromPositions(attackerPosition, opponentKingPosition);
					Position oppositeDirection = direction.negated();
					possibleEscapes.remove(direction); // Remove direction away from the attacker
					possibleEscapes.remove(oppositeDirection); // Remove direction to the attacker
					SLog.write("ruling out directions: " + direction + ", " + oppositeDirection);

					if (rook || queen)
					{	// Check if the rook/queen is adjacent to the king to possibly eliminate a lot of escape directions
						Position offset = attackerPosition.minus(opponentKingPosition);

						if (Math.abs(offset.x) < 2 && Math.abs(offset.y) < 2)
						{	// Rook/queen is adjacent to the king - eliminate all places where the king cannot go
							for (Iterator<Position> iterator = possibleEscapes.iterator(); iterator.hasNext();)
							{
								Position dir = iterator.next();
								Position pos = opponentKingPosition.plus(dir);

								if (pos.isHorizontalOrVerticalTo(attackerPosition) || (queen && pos.isDiagonalTo(attackerPosition)))
								{
									iterator.remove();
									SLog.write("ruling out direction: " + dir);
								}
							}
						}
					}
				}
				else if (attacker instanceof Knight)
				{	// When a knight is checking the king, there is always one more place around the king that the knight can go to - remove it as a possible escape
					Position attackerMove = opponentKingPosition.minus(attackerPosition).swappedAbsolute();
					Position attackPosition = attackerPosition.plus(attackerMove);
					Position direction = attackPosition.minus(opponentKingPosition);
					possibleEscapes.remove(direction);
					SLog.write("ruling out direction: " + direction);
				}
			}

			SLog.write("possible escapes after ruling out: " + possibleEscapes);

			for (Position escape : possibleEscapes)
			{
				Position tryPos = opponentKingPosition.plus(escape);

				if (!tryPos.isWithinBoard())
				{
					continue;
				}

				if (board[tryPos.x][tryPos.y].getChessPiece() != null)
				{	// Cannot go there - a chess piece is occupying the spot
					continue;
				}

				if (null == positionAttacker(tryPos, myChessPieces))
				{	// This is a position the king can move to to save himself
					SLog.write("the king can escape to position: " + tryPos);
					break checkmate;
				}
			}

			// The king cannot move
			SLog.write("the king cannot move");

			if (2 == attackers.size())
			{	// The only possible reply to a double check is a king move. Since the king cannot move, this is a checkmate
				SLog.write("double checking the king while he cannot move - checkmate!");
				endGame(executor);
				break checkmate;
			}

			// Check if the attacker can be taken
			SLog.write("checking whether the attacker can be taken");
			ArrayList<ChessPiece> opponentChessPieces = opponentPieceSet.getActivePieces();
			ChessPiece attacker = attackers.get(0);
			Position attackerPosition = attacker.getPosition();

			for (ChessPiece hero : opponentChessPieces)
			{
				if (Result.OK == validateMove(opponent, hero, hero.getPosition(), attackerPosition, null))
				{	// The attacker can be taken
					SLog.write("the attacker can be taken by: " + hero + " at position: " + hero.getPosition());
					break checkmate;
				}
			}

			// The attacker cannot be taken
			SLog.write("the attacker cannot be taken\nchecking whether the attacker can be intercepted");

			// Check if the attacker can be intercepted

			if (attacker instanceof Knight || attacker instanceof Pawn)
			{	// Knight or pawn cannot be intercepted - this is a checkmate
				SLog.write("attacker is :" + attacker + ", it cannot be intercepted - checkmate!");
				endGame(executor);
				break checkmate;
			}

			Position step = MovementSet.directionFromPositions(opponentKingPosition, attackerPosition);
			Position cursor = opponentKingPosition.clone();

			do
			{
				cursor.add(step);

				if (board[cursor.x][cursor.y].getChessPiece() != null)
				{	// cursor has reached the attacker which means that nothing can intercept it - this is a checkmate
					SLog.write("attacker cannot be intercepted - checkmate!");
					endGame(executor);
					break;
				}

				for (ChessPiece hero : opponentChessPieces)
				{
					if (Result.OK == validateMove(opponent, hero, hero.getPosition(), cursor, null))
					{	// The attacker can be intercepted
						SLog.write("attacker can be intercepted by: " + hero + " at position: " + hero.getPosition());
						break checkmate;
					}
				}
			}
			while (true);
		}

		return Result.OK;
	}

	// Method validates the move by checking the following:
	// - can the chess piece at all move to the destination position
	// - can the chess piece take the other chess piece at the destination position
	// - can the chess piece move without exposing the king to check
	// - if the king is moved, would he be safe at the destination position
	// Returns a Result error code if the move is invalid
	// Returns Result.OK if the move is valid
	private int validateMove(Player executor, ChessPiece movedPiece, Position from, Position to, MoveContext ctx)
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

			int x = (vertical > 0 ? Game.BOARD_SIDE - 1 : 0);
			int y = (Color.WHITE == executor.getColor() ? 0 : Game.BOARD_SIDE - 1);

			ChessPiece rookMaybe = board[x][y].getChessPiece();

			if (null == rookMaybe || !(rookMaybe instanceof Rook) || ((Rook)rookMaybe).hasMoved())
			{	// No rook, not rook or rook has already moved
				SLog.write("attempting invalid castling - no rook, not rook or rook has moved");
				return Result.INVALID_MOVE;
			}

			if (game.inCheck != null && executor.equals(game.inCheck))
			{	// The player's king is in check
				SLog.write("attempting invalid castling - player in check");
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

		if (movedPiece instanceof Pawn && null == toPiece)
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

	private boolean isPathIntercepted(Position from, Position to)
	{
		Position direction = MovementSet.directionFromPositions(from, to);
		return firstChessPieceInDirection(from, direction, to) != null;
	}

	// Returns the first chess piece in a direction starting from a position
	// Search starts at 'from' + 'direction'
	// Search ends when a chess piece is found or the end of the board is reached
	private ChessPiece firstChessPieceInDirection(Position from, Position direction)
	{
		return firstChessPieceInDirection(from, direction, null);
	}

	// Returns the first chess piece in a direction starting from a position
	// Search starts at 'from' + 'direction'
	// Search ends when a chess piece is found, 'end' is reached (if specified) or when the end of the board is reached, the 'end' position is not checked for a chess piece
	private ChessPiece firstChessPieceInDirection(Position from, Position direction, Position end)
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

	// Returns the first chess piece out of the list that attacks the specified position
	// Important: bare in mind that before actually executing a move, this method might not return accurate results
	private ChessPiece positionAttacker(Position pos, ArrayList<ChessPiece> pieces)
	{
		for (ChessPiece piece : pieces)
		{
			if (piece instanceof Pawn)
			{	// No need to check all pawns, skip them here, we do a simpler check after the loop
				continue;
			}

			if (piece.isMoveValid(pos, true) && (piece instanceof Knight || !isPathIntercepted(piece.getPosition(), pos)))
			{	// Position is threatened by this chess piece
				return piece;
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

	private class MoveContext
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
