import com.kt.api.Action;
import com.kt.api.Push;
import com.kt.api.Result;
import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;
import com.kt.game.*;
import com.kt.utils.SLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Toshko on 12/7/13.
 */
public class Main
{
    static Socket sock;

    private static CHESCOReader reader;
    private static CHESCOWriter writer;

	private static boolean shouldHandlePushMessages = true;

	private static ArrayList<HashMap> availableGames;

	private static Boolean iAmNextOnResume = null;

	private static Player me;
	private static Player notMe;
	private static GameController theGameController;

    public static void main(String []args)
    {
        while (true)
        {
            try
            {
                start();
            }
            catch (Throwable e)
            {
                System.out.println("Exception: " + e);
            }
        }
    }

    public static void start() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            SLog.write("enter command: ");
            String input = br.readLine();

            if (null == input)
            {
                SLog.write("End of file stream");
                System.exit(1);
            }

            input = input.toLowerCase();
            String args[] = input.split(" ");

            if (args[0].equals("exit"))
            {
                System.exit(0);
            }
            else if (args[0].equals("routine"))
            {
                routine(Integer.parseInt(args[1]));
            }
            else if (args[0].equals("connect"))
            {
                connect();
            }
            else if (args[0].equals("disconnect"))
            {
                disconnect();
            }
            else if (args[0].equals("reconnect"))
            {
                disconnect();
                connect();
            }
            else if (args[0].equals("listen"))
            {
                listen((args.length == 2 ? Integer.parseInt(args[1]) : 1));
            }
            else if (args[0].equals("login"))
            {
                login(args[1], args[2]);
            }
            else if (args[0].equals("register"))
            {
                reg(args[1], args[2]);
            }
            else if (args[0].equals("creategame"))
            {
                createGame(args[1], (args.length > 2 ? args[2] : null), (args.length > 3 ? Integer.parseInt(args[3]) : Game.NO_TIMEOUT));
            }
            else if (args[0].equals("cancelgame"))
            {
                cancelGame(Integer.parseInt(args[1]));
            }
            else if (args[0].equals("fetchgames"))
            {
                int offset = -1, limit = -1;
				String type = null;
				if (args.length > 1)
				{
					type = args[1];
				}
                if (args.length > 2)
                {
                    offset = Integer.parseInt(args[2]);
                }
                if (args.length > 3)
                {
                    limit = Integer.parseInt(args[3]);
                }
                fetchGames(type, offset, limit);
            }
            else if (args[0].equals("joingame"))
            {
                joinGame(Integer.parseInt(args[1]));
            }
			else if (args[0].equals("exitgame"))
			{
				exitGame(Integer.parseInt(args[1]));
			}
			else if (args[0].equals("move"))
			{
				move(args[1]);
			}
			else if (args[0].equals("savegame"))
			{
				saveGame(Integer.parseInt(args[1]));
			}
			else if (args[0].equals("deletesave"))
			{
				deleteSavedGame(Integer.parseInt(args[1]));
			}
			else if (args[0].equals("resumegame"))
			{
				resumeGame(Integer.parseInt(args[1]));
			}
            else
            {
                SLog.write("Unrecognized command");
            }
        }
    }

    public static void routine(int index) throws IOException
    {
		if (1 == index)
		{
			connect();
			login("tttt", "pppp");
			createGame("mygame", "white", Game.NO_TIMEOUT);
			listen(1);
		}
		else if (2 == index)
		{
			connect();
			login("toshko", "parola");
			fetchGames(null, -1, -1);
		}
    }

    public static void connect() throws IOException
    {
//        sock = new Socket(InetAddress.getByName("95.111.43.117"), 4848);
		sock = new Socket(InetAddress.getLocalHost(), 4848);

        reader = new CHESCOReader(sock.getInputStream());
        writer = new CHESCOWriter(sock.getOutputStream());
    }

    public static void disconnect() throws IOException
    {
        if (sock != null && !sock.isClosed())
        {
            sock.close();
        }

        reader = null;
        writer = null;
    }

    public static void listen(int messages) throws IOException
    {
        if (messages < 0)
        {
            while (true)
            {
				handleMessage((HashMap)reader.read());
            }
        }
        else
        {
            while (messages-- > 0)
            {
				handleMessage((HashMap)reader.read());
            }
        }
    }

	public static void handleMessage(HashMap msg)
	{
		SLog.write(msg);

		if (!shouldHandlePushMessages)
		{
			return;
		}

		if (msg.containsKey("push"))
		{
			switch((Integer)msg.get("event"))
			{
				case Push.GAME_JOIN:
					notMe = new Player((Integer)msg.get("opponentid"), (String)msg.get("opponentname"));
					theGameController.addPlayer(notMe, me.getColor().Opposite);

					if (iAmNextOnResume != null)
					{
						theGameController.startGame(iAmNextOnResume ? me : notMe);
					}
					else
					{
						theGameController.startGame();
					}

					iAmNextOnResume = null;

					printBoard();
					break;

				case Push.GAME_END:
					notMe = null;
					theGameController = null;
					break;

				case Push.GAME_MOVE:
					String move = (String)msg.get("move");
					theGameController.executeMove(notMe, move);
					printBoard();
					break;

				case Push.GAME_SAVE:
					break;

				default:
					SLog.write("unrecognized push event");
					break;
			}
		}
		else
		{
			SLog.write("unrecognized message");
		}
	}

    public static void login(String name, String pass) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.LOGIN);
        req.put("username", name);
        req.put("password", pass);
        writer.write(req);

        HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			me = new Player((Integer)res.get("userid"), (String)res.get("username"));
		}
    }

    public static void reg(String name, String pass) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.REGISTER);
        req.put("username", name);
        req.put("password", pass);
        writer.write(req);

		HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			me = new Player((Integer)res.get("userid"), (String)res.get("username"));
		}
    }

    public static void createGame(String name, String color, int timeout) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.CREATE_GAME);
        req.put("gamename", name);
		req.put("timeout", timeout);
		if (color != null)
		{
			req.put("color", color);
		}
        writer.write(req);

        HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			theGameController = new GameController(new Game(-1, "", Game.NO_TIMEOUT), new MasterChessMoveExecutor());
			theGameController.addPlayer(me, (null == color ? Color.WHITE : color.equals("white") ? Color.WHITE : Color.BLACK));
		}
    }

    public static void cancelGame(int gameid) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.CANCEL_GAME);
        req.put("gameid", gameid);
        writer.write(req);

     	HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			theGameController.removePlayers();
			theGameController = null;
		}
    }

    public static void fetchGames(String type, int offset, int limit) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.FETCH_GAMES);
		if (type != null)
		{
			req.put("type", type);
		}
        if (offset != -1)
        {
            req.put("offset", offset);
        }
        if (limit != -1)
        {
            req.put("limit", limit);
        }
        writer.write(req);

        HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			availableGames = (ArrayList)res.get("games");
		}
    }

    public static void joinGame(int gameID) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.JOIN_GAME);
        req.put("gameid", gameID);
        writer.write(req);

        HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			Color opponentColor = Color.NONE;
			Player opponent = null;

			for (HashMap game : availableGames)
			{
				if ((Integer)game.get("gameid") == gameID)
				{
					String theColor = (String)game.get("usercolor");
					opponentColor = (theColor.equals("white") ? Color.WHITE : Color.BLACK);
					opponent = new Player((Integer)game.get("userid"), (String)game.get("username"));
				}
			}

			if (Color.NONE == opponentColor || null == opponent)
			{
				SLog.write("Cannot start game as opponent info could not be found");
				try { disconnect(); } catch (IOException ignore) {}
				me = null;
				return;
			}

			notMe = opponent;
			theGameController = new GameController(new Game(-1, "", Game.NO_TIMEOUT), new MasterChessMoveExecutor());
			theGameController.addPlayer(me, opponentColor.Opposite);
			theGameController.addPlayer(notMe, opponentColor);
			theGameController.startGame();

			printBoard();
		}
	}

	public static void exitGame(int gameID) throws IOException
	{
		HashMap req = new HashMap();
		req.put("action", Action.EXIT_GAME);
		req.put("gameid", gameID);
		writer.write(req);

		HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			theGameController.removePlayers();
			theGameController = null;
			notMe = null;
		}
	}

	public static void move(String move) throws IOException
	{
		HashMap req = new HashMap();
		req.put("action", Action.MOVE);
		req.put("move", move);
		writer.write(req);

		HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			theGameController.executeMove(me, move);
			printBoard();
		}
	}

	public static void saveGame(int gameID) throws IOException
	{
		HashMap req = new HashMap();
		req.put("action", Action.SAVE_GAME);
		req.put("gameid", gameID);
		writer.write(req);

		HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			theGameController.removePlayers();
			theGameController = null;
			notMe = null;
		}
	}

	public static void deleteSavedGame(int gameID) throws IOException
	{
		HashMap req = new HashMap();
		req.put("action", Action.DELETE_SAVED_GAME);
		req.put("gameid", gameID);
		writer.write(req);

		HashMap res = (HashMap)reader.read();
		SLog.write(res);
	}

	public static void resumeGame(int gameID) throws IOException
	{
		HashMap req = new HashMap();
		req.put("action", Action.RESUME_GAME);
		req.put("gameid", gameID);
		writer.write(req);

		HashMap res = (HashMap)reader.read();
		SLog.write(res);

		if (Result.OK == (Integer)res.get("result"))
		{
			String dataStr = (String)res.get("game");
			byte data[] = dataStr.getBytes("UTF-8");
			theGameController = new GameController(new Game(-1, "", Game.NO_TIMEOUT, data), new MasterChessMoveExecutor());

			Color opponentColor = Color.NONE;
			Player opponent = null;

			for (HashMap game : availableGames)
			{
				if ((Integer)game.get("gameid") == gameID)
				{
					String theColor = (String)game.get("usercolor");
					opponentColor = (theColor.equals("white") ? Color.WHITE : Color.BLACK);
					opponent = new Player((Integer)game.get("userid"), (String)game.get("username"));
				}
			}

			if (Color.NONE == opponentColor || null == opponent)
			{
				SLog.write("Cannot start game as opponent info could not be found");
				try { disconnect(); } catch (IOException ignore) {}
				me = null;
				theGameController = null;
				return;
			}

			theGameController.addPlayer(me, opponentColor.Opposite);
			iAmNextOnResume = (Boolean)res.get("next");

			if ((Boolean)res.get("started"))
			{
				notMe = opponent;
				theGameController.addPlayer(notMe, opponentColor);
				theGameController.startGame(iAmNextOnResume ? me : notMe);
				printBoard();
			}
		}
	}

	public static void printBoard()
	{
		SLog.write(theGameController.getGame());
	}
}
