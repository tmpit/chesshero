package com.kt.game;

import com.kt.api.Result;
import com.kt.utils.SLog;

/**
 * Created by Toshko on 12/16/14.
 *
 * A controller object responsible for updating the state of a @{code Game} object and @{code Player} objects
 * associated with the game. You typically operate on a @{code Game} object through a @{code GameController} instance
 */
public class GameController
{
	private Game game;
	private ChessMoveValidator moveExecutor;

	private GameClock gameClock = null;

	/**
	 * Designated initializer for the class
	 * @param game A non-@{code null} @{code Game} object in a @{code Game.STATE_INIT} state. If such an instance is not
	 *             provided, this controller won't function
	 * @param executor A @{code ChessMoveValidator} subclass that will be responsible for executing chess moves.
	 *                 Must not be @{code null}
	 * @param shouldUseGameClock Pass @{code true} if you wish the controller to initialize a @{code GameClock}
	 *                           used to track the time. Ignored if the game has no timeout
	 */
	public GameController(Game game, ChessMoveValidator executor, boolean shouldUseGameClock)
	{
		if (game.getState() != Game.STATE_INIT)
		{
			return;
		}

		this.game = game;
		this.moveExecutor = executor;

		if (game.getTimeout() != Game.NO_TIMEOUT && shouldUseGameClock)
		{
			this.gameClock = new GameClock(game);
		}

		if (game.wasResumed())
		{
			this.game.setState(Game.STATE_WAITING);
		}
		else
		{
			this.game.setState(Game.STATE_PENDING);
		}
	}

	/**
	 * Call to get the @{code Game} instance this controller operates on
	 * @return The controller's @{code Game} instance
	 */
	public Game getGame()
	{
		return game;
	}

	/**
	 * Gets the game clock of the game
	 * @return A {@code GameClock}
	 */
	public GameClock getClock()
	{
		return gameClock;
	}

	/**
	 * Adds a player to the game. Use to add a player to a new game (not a resumed one). Does nothing if
	 * one of the parameters is @{code null}, if the game is full or not waiting for players to join
	 * @param player A non-@{code null} @{code Player} instance representing the player joining the game
	 * @param color A @{code Color} instance describing the @{code player}'s color. Must not be @{code null} and
	 *              must contain a color other than @{code Color.NONE}
	 */
	public void addPlayer(Player player, Color color)
	{
		addPlayer(player, color, 0);
	}

	/**
	 * Adds a player to the game. Does nothing if one of the parameters is @{code null}, if the game is full or
	 * not waiting for players to join
	 * @param player A non-@{code null} @{code Player} instance representing the player joining the game
	 * @param color A @{code Color} instance describing the @{code player}'s color. Must not be @{code null} and
	 *              must contain a color other than @{code Color.NONE}
	 * @param millisPlayed The time measured in milliseconds the player has played in the game. If this controller's
	 *                     game is a new game, set this to 0, otherwise, if this is a resumed game, set this to
	 *                     the player's total time played in the game
	 * @return @{code true} if the player was successfully added to the game, @{code false} if the game is full,
	 * @{code player} or @{code color} is @{code null}, or if the game is not waiting for players to join
	 */
	public boolean addPlayer(Player player, Color color, long millisPlayed)
	{
		if (game.getState() != Game.STATE_PENDING && game.getState() != Game.STATE_WAITING)
		{
			return false;
		}

		if (game.player1 != null && game.player2 != null)
		{
			return false;
		}

		if ((game.player1 != null && color == game.player1.color) || (game.player2 != null && color == game.player2.color))
		{
			return false;
		}

		if (game.player1 != null)
		{
			game.player2 = player;
		}
		else
		{
			game.player1 = player;
		}

		player.game = game;
		player.color = color;
		player.chessPieceSet = color == Color.WHITE ? game.whiteChessPieceSet : game.blackChessPieceSet;
		player.millisPlayed = millisPlayed;
		player.chessPieceSet.setOwner(player);

		return true;
	}

	/**
	 * Removes all players from the game (if there are any)
	 */
	public void removePlayers()
	{
		if (game.player1 != null)
		{
			removePlayer(game.player1);
			game.player1 = null;
		}

		if (game.player2 != null)
		{
			removePlayer(game.player2);
			game.player2 = null;
		}
	}

	/**
	 * Removes the specified player from the game. Use to reset a @{code Player} instance's state after a game ends
	 * @param player The @{code Player} instance to remove from the game. Must not be @{code null}
	 */
	private void removePlayer(Player player)
	{
		player.game = null;
		player.color = Color.NONE;
		player.chessPieceSet.setOwner(null);
		player.chessPieceSet = null;
		player.millisPlayed = 0;
	}

	/**
	 * Use to set a @{code Player} instance's @{code millisPlayed} instance variable (or game time)
	 * @param player The @{code Player} instance to set the time on. Must not be @{code null}
	 * @param millisPlayed The game time measured in milliseconds
	 */
	public void setPlayerMillisPlayed(Player player, long millisPlayed)
	{
		player.millisPlayed = millisPlayed;
	}

	/**
	 * Starts the game with the white player taking the first turn. Does nothing if the game is in an inconsistent
	 * state or if it is waiting for players to join
	 */
	public void startGame()
	{
		startGame(null);
	}

	/**
	 * Starts the game. Does nothing if the game is in an inconsistent state or if it is waiting for players to join
	 * @param turn Optional. If provided, this will be the player that will take the first turn in the game. If @{code null},
	 *             the white player will take the first turn
	 * @return @{code true} if the game was successfully started, @{code false} if the conditions necessary for the game
	 * to start are not met
	 */
	public boolean startGame(Player turn)
	{
		if ((game.getState() != Game.STATE_WAITING && game.getState() != Game.STATE_PENDING) || game.player1 == null || game.player2 == null)
		{
			return false;
		}

		game.setState(Game.STATE_ACTIVE);
		game.initializeBoard();
		moveExecutor.setGame(game);
		moveExecutor.setBoard(game.getBoard());

		if (turn != null)
		{
			game.turn = turn;
		}
		else if (Color.WHITE == game.player1.getColor())
		{
			game.turn = game.player1;
		}
		else
		{
			game.turn = game.player2;
		}

		if (gameClock != null)
		{
			gameClock.start();
		}

		return true;
	}

	/**
	 * Ends the game. Does nothing if the game is in an inconsistent state
	 * @param winner A @{code Player} instance specifying the winner in the game
	 * @param ending A @{code Game.Ending} value specifying how should the game end
	 */
	public void endGame(Player winner, Game.Ending ending)
	{
		if (game.getState() != Game.STATE_ACTIVE && game.getState() != Game.STATE_PAUSED)
		{
			return;
		}

		if (ending == null)
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
		game.winner = winner;
		game.ending = ending;

		if (gameClock != null)
		{
			gameClock.interrupt();
		}
	}

	/**
	 * Pauses the game. Does nothing if the game is in an inconsistent state
	 */
	public void pauseGame()
	{
		if (game.getState() != Game.STATE_ACTIVE)
		{
			return;
		}

		game.setState(Game.STATE_PAUSED);
	}

	/**
	 * Resumes the game. Does nothing if the game is in an inconsistent state
	 */
	public void resumeGame()
	{
		if (game.getState() != Game.STATE_PAUSED)
		{
			return;
		}

		game.setState(Game.STATE_ACTIVE);
	}

	/**
	 * Finalizes the game without ending it. The game will stop, but will not have a winner or a @{code Game.Ending} value
	 */
	public void saveGame()
	{
		if (game.getState() != Game.STATE_ACTIVE && game.getState() != Game.STATE_PAUSED)
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
	}

	/**
	 * Executes a chess move. The game controller uses its @{code ChessMoveValidator} object to execute the move
	 * @param player The player executing the move. Must not be @{code null}
	 * @param move The encoded move represented by a @{code String}. Must not be @{code null}
	 * @return
	 */
	public int executeMove(Player player, String move)
	{
		long currentTime = System.currentTimeMillis();

		ChessMoveResult result = moveExecutor.executeMove(player, move);

		if (Result.OK == result.resultCode)
		{
			if (gameClock != null)
			{
				player.millisPlayed = (currentTime - gameClock.getStartTimeMillis()) - player.getOpponent().millisPlayed;
			}

			game.moves.add(new Move(player, move));

			if (result.checkmate)
			{
				endGame(result.winner, Game.Ending.CHECKMATE);
			}
		}

		return result.resultCode;
	}
}
