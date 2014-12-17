package com.kt.game;

import com.kt.game.ChessPiece;
import com.kt.game.King;

import java.util.ArrayList;

/**
 * The ChessPieceSet class represents a set of chess pieces a chess player possesses. An instance of
 * this class contains two collections of chess pieces - pieces that are still on the chess board
 * and chess pieces that are taken
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class ChessPieceSet
{
	private ArrayList<ChessPiece> activePieces = new ArrayList<ChessPiece>(16);
	private ArrayList<ChessPiece> takenPieces = new ArrayList<ChessPiece>(16);

	private King king = null;

	/**
	 * Initializes a newly created {@code ChessPieceSet} object with a list of active pieces and no taken pieces
	 * @param active An {@code ArrayList} of {@code ChessPiece} instances
	 */
	public ChessPieceSet(ArrayList<ChessPiece> active)
	{
		this(active, null);
	}

	/**
	 * Initializes a newly created {@code ChessPieceSet} object with lists of active and taken pieces
	 * @param active An {@code ArrayList} of {@code ChessPiece} instances
	 * @param taken An {@code ArrayList} of {@code ChessPiece} instances
	 */
	public ChessPieceSet(ArrayList<ChessPiece> active, ArrayList<ChessPiece> taken)
	{
		activePieces.addAll(active);

		if (taken != null)
		{
			takenPieces.addAll(taken);
		}
	}

	public void setOwner(Player owner)
	{
		for (ChessPiece piece : activePieces)
		{
			piece.setOwner(owner);
		}

		for (ChessPiece piece : takenPieces)
		{
			piece.setOwner(owner);
		}
	}

	/**
	 * Gets the active pieces
	 * @return An {@code ArrayList} of {@code ChessPiece} instances
	 */
	public ArrayList<ChessPiece> getActivePieces()
	{
		return activePieces;
	}

	/**
	 * Gets the taken pieces
	 * @return An {@code ArrayList} of {@code ChessPiece} instances
	 */
	public ArrayList<ChessPiece> getTakenPieces()
	{
		return takenPieces;
	}

	/**
	 * Gets the king from the active chess pieces
	 * @return A {@code King} instance
	 */
	public King getKing()
	{
		if (null == king)
		{
			for (ChessPiece piece : activePieces)
			{
				if (piece instanceof King)
				{
					king = (King)piece;
					break;
				}
			}
		}

		return king;
	}

	/**
	 * Moves a {@code ChessPiece} from the active pieces collection to the taken pieces collection
	 * if it is present in the active pieces collection
	 * @param piece The {@code ChessPiece} to take
	 * @return false if the specified chess piece is not in the active pieces collection and was not added
	 * to the taken pieces collection, true otherwise
	 */
	public boolean take(ChessPiece piece)
	{
		if (activePieces.remove(piece))
		{
			return takenPieces.add(piece);
		}

		return false;
	}

	/**
	 * Adds a {@code ChessPiece} to the active pieces collection
	 * @param piece The {@code ChessPiece} to add
	 */
	public void add(ChessPiece piece)
	{
		activePieces.add(piece);
	}
}
