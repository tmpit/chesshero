package com.kt.game.chesspieces;

import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Toshko on 12/23/13.
 */
public class Knight extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.LEFT2_UP,
			MovementSet.LEFT2_DOWN,
			MovementSet.RIGHT2_UP,
			MovementSet.RIGHT2_DOWN,
			MovementSet.LEFT_UP2,
			MovementSet.LEFT_DOWN2,
			MovementSet.RIGHT_UP2,
			MovementSet.RIGHT_DOWN2
	)));

	public Knight(Position position, Player owner)
	{
		super(position, owner, set);
	}

	@Override
	public boolean isMoveValid()
	{
		return false;
	}
}
