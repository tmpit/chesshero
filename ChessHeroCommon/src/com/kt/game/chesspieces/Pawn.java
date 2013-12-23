package com.kt.game.chesspieces;

import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Toshko on 12/23/13.
 */
public class Pawn extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.UP2, MovementSet.UP_LEFT, MovementSet.UP_RIGHT
	)), true);

	public Pawn(Position position, Player owner)
	{
		super(position, owner, set);
	}

	@Override
	public boolean isMoveValid()
	{
		return true;
	}
}
