package com.kt.game;

import com.kt.game.ChessPiece;
import com.kt.game.King;

import java.util.ArrayList;

/**
 * Created by Toshko on 12/27/13.
 */
public class ChessPieceSet
{
	private ArrayList<ChessPiece> activePieces = new ArrayList<ChessPiece>(16);
	private ArrayList<ChessPiece> takenPieces = new ArrayList<ChessPiece>(16);

	private King king = null;

	public ChessPieceSet(ArrayList<ChessPiece> active)
	{
		this(active, null);
	}

	public ChessPieceSet(ArrayList<ChessPiece> active, ArrayList<ChessPiece> taken)
	{
		activePieces.addAll(active);

		if (taken != null)
		{
			takenPieces.addAll(taken);
		}
	}

	public ArrayList<ChessPiece> getActivePieces()
	{
		return activePieces;
	}

	public ArrayList<ChessPiece> getTakenPieces()
	{
		return takenPieces;
	}

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

	public boolean take(ChessPiece piece)
	{
		if (activePieces.remove(piece))
		{
			return takenPieces.add(piece);
		}

		return false;
	}

	public void add(ChessPiece piece)
	{
		activePieces.add(piece);
	}
}
