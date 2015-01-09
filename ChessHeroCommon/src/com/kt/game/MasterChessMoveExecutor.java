package com.kt.game;

import com.kt.api.Result;
import com.kt.utils.SLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Toshko on 12/17/14.
 */
public class MasterChessMoveExecutor extends ChessMoveValidator implements ChessMoveExecutor
{
	/**
	 * Executes a move performed by a player as per the Pure coordinate notation as described at
	 * http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	 * @param executor A {@code Player} for the player executing the move
	 * @param move A {@code String} for the move
	 * @return A constant from the {@code Result} class
	 */
	@Override
	public ChessMoveResult executeMove(Player executor, String move)
	{
		Game game = getGame();
		BoardField board[][] = getBoard();

		if (game.getState() != Game.STATE_ACTIVE)
		{	// Game has not started yet
			return new ChessMoveResult(Result.NOT_PLAYING);
		}

		if (!executor.equals(game.turn))
		{	// Attempting to make two consecutive moves
			return new ChessMoveResult(Result.NOT_YOUR_TURN);
		}

		SLog.write("Decoding move: " + move);

		Position from, to = from = Position.ZERO;
		char promotion = '\0';

		// Move should be in the Pure coordinate notation: http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
		move = move.toLowerCase();
		int moveLen = move.length();

		if (moveLen != 4 && moveLen != 5)
		{
			return new ChessMoveResult(Result.INVALID_MOVE_FORMAT);
		}

		from = Position.positionFromBoardPosition(move.substring(0, 2));

		if (null == from)
		{
			return new ChessMoveResult(Result.INVALID_MOVE_FORMAT);
		}

		to = Position.positionFromBoardPosition(move.substring(2, 4));

		if (null == to)
		{
			return new ChessMoveResult(Result.INVALID_MOVE_FORMAT);
		}

		if (5 == moveLen)
		{
			promotion = move.charAt(4);

			if (promotion != 'q' && promotion != 'r' && promotion != 'b' && promotion != 'n')
			{
				return new ChessMoveResult(Result.INVALID_MOVE_FORMAT);
			}
		}

		SLog.write("Executing move: from " + from + " to " + to + (promotion != '\0' ? " promotion " + promotion : ""));

		if (from.equals(to))
		{
			SLog.write("no-op");
			return new ChessMoveResult(Result.SAME_POSITION);
		}

		BoardField fromField = board[from.x][from.y];
		ChessPiece movedPiece = fromField.getChessPiece(); // The chess piece that is being moved

		SLog.write("moved piece: " + movedPiece);

		if (null == movedPiece)
		{	// There is no chess piece at that position in the board
			return new ChessMoveResult(Result.NO_CHESSPIECE);
		}

		if (!movedPiece.getOwner().equals(executor))
		{	// Attempting to move your opponent's chess piece
			return new ChessMoveResult(Result.NOT_YOUR_CHESSPIECE);
		}

		BoardField toField = board[to.x][to.y];
		ChessPiece toPiece = toField.getChessPiece(); // The chess piece that is at the destination position

		MoveContext context = new MoveContext();
		int moveResult = validateMove(executor, movedPiece, from, to, context);

		if (moveResult != Result.OK)
		{
			return new ChessMoveResult(moveResult);
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
					promoted = new Queen(to, executor.getColor());
					break;

				case 'r':
					promoted = new Rook(to, executor.getColor());
					break;

				case 'b':
					promoted = new Bishop(to, executor.getColor());
					break;

				case 'n':
					promoted = new Knight(to, executor.getColor());
					break;

				default:
					return new ChessMoveResult(Result.MISSING_PROMOTION);
			}

			promoted.setOwner(executor);
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

				return new ChessMoveResult(Result.WRONG_MOVE);
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

			// Update variable so all checks from here work with the promoted piece
			movedPiece = promoted;
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

			BoardField castleField = board[castlePos.x][castlePos.y];
			ChessPiece oldMovedPiece = movedPiece;
			Position oldTo = to;
			movedPiece = castleField.getChessPiece();
			to = castleField.getPosition();

			Player opponent = executor.getOpponent();
			ChessPieceSet opponentPieceSet = opponent.getChessPieceSet();

			// Check whether this move would make the opponent's king in check
			Position opponentKingPosition = opponentPieceSet.getKing().getPosition();

			// If the moved piece is a knight, we do not need to check whether the path is clear
			if (movedPiece.isMoveValid(opponentKingPosition, true) && (movedPiece instanceof Knight || !isPathIntercepted(to, opponentKingPosition)))
			{	// The opponent's king is in check by the chess piece we just moved
				SLog.write("opponent's king is in check by: " + movedPiece + " at position: " + to);
				game.inCheck = opponent;
				if(!game.attackers.contains(movedPiece))
				{
					game.attackers.add(movedPiece);
				}
			}

			movedPiece = oldMovedPiece;
			to = oldTo;
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
			if(!game.attackers.contains(movedPiece))
			{
				game.attackers.add(movedPiece);
			}
		}

		ChessPiece discovery = firstChessPieceInDirection(opponentKingPosition, MovementSet.directionFromPositions(opponentKingPosition, from));

		// Ignore knights - they cannot be a threat if they are positioned horizontally, vertically or diagonally relative to the king
		if (discovery != null && discovery.getOwner().equals(executor) && !(discovery instanceof Knight) &&
				discovery.isMoveValid(opponentKingPosition, true) && !isPathIntercepted(discovery.getPosition(), opponentKingPosition))
		{	// The opponent's king is in check by a chess piece discovered by the move
			SLog.write("opponent's king is in check by discovery by: " + discovery + " at position: " + discovery.getPosition());
			game.inCheck = opponent;
			if(!game.attackers.contains(discovery))
			{
				game.attackers.add(discovery);
			}
		}

		boolean checkmate = false;

		// Check whether this is a checkmate
		checkmate:
		if (game.inCheck != null)
		{
			SLog.write("Checking for checkmate...\nchecking whether the king can escape");
			ArrayList<ChessPiece> myChessPieces = myPieceSet.getActivePieces();
			ArrayList<ChessPiece> attackers = game.attackers;

			// Check if the king can escape
			HashSet<Position> possibleEscapes = new HashSet<Position>(Arrays.asList(new Position[]{
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
				checkmate = true;
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
				checkmate = true;
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
					checkmate = true;
					break;
				}

				for (ChessPiece hero : opponentChessPieces)
				{
					if (hero instanceof King)
					{	// The king cannot intercept
						continue;
					}

					if (Result.OK == validateMove(opponent, hero, hero.getPosition(), cursor, null))
					{	// The attacker can be intercepted
						SLog.write("attacker can be intercepted by: " + hero + " at position: " + hero.getPosition());
						break checkmate;
					}
				}
			}
			while (true);
		}

		if (checkmate)
		{
			return new ChessMoveResult(Result.OK, true, executor);
		}

		return new ChessMoveResult(Result.OK);
	}
}
