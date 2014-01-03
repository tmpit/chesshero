import com.kt.*;
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

	private static boolean shouldRespondToMessages = true;

	private static int lastAction;
	private static String joinColor;
	private static int joinID;
	private static ArrayList<HashMap> availableGames;
	private static String lastMove;

	private static Player me;
	private static Player notMe;
	private static Game theGame;

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
                createGame(args[1], (args.length > 2 ? args[2] : null));
            }
            else if (args[0].equals("cancelgame"))
            {
                cancelGame(Integer.parseInt(args[1]));
            }
            else if (args[0].equals("fetchgames"))
            {
                int offset = -1, limit = -1;
                if (args.length > 1)
                {
                    offset = Integer.parseInt(args[1]);
                }
                if (args.length > 2)
                {
                    limit = Integer.parseInt(args[2]);
                }
                fetchGames(offset, limit);
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
			createGame("mygame", "white");
			listen(1);
		}
		else if (2 == index)
		{
			connect();
			login("toshko", "parola");
			fetchGames(-1, -1);
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

		if (!shouldRespondToMessages)
		{
			return;
		}

		if (msg.containsKey("result"))
		{
			if ((Integer)msg.get("result") != Result.OK)
			{
				return;
			}

			switch (lastAction)
			{
				case Action.LOGIN:
				case Action.REGISTER:
					me = new Player((Integer)msg.get("userid"), (String)msg.get("username"));
					break;

				case Action.CREATE_GAME:
					theGame = new Game(-1, "");
					me.join(theGame, (null == joinColor ? Color.WHITE : joinColor.equals("white") ? Color.WHITE : Color.BLACK));
					break;

				case Action.CANCEL_GAME:
					me.leave();
					theGame = null;
					break;

				case Action.FETCH_GAMES:
					availableGames = (ArrayList)msg.get("games");
					break;

				case Action.JOIN_GAME:
					theGame = new Game(-1, "");
					Color opponentColor = Color.NONE;

					for (HashMap game : availableGames)
					{
						if ((Integer)game.get("gameid") == joinID)
						{
							String theColor = (String)game.get("playercolor");
							opponentColor = (theColor.equals("white") ? Color.WHITE : Color.BLACK);
						}
					}

					if (Color.NONE == opponentColor)
					{
						SLog.write("Cannot start game as player color cannot be determined");
						try { disconnect(); } catch (IOException ignore) {}
						me = null;
						theGame = null;
						return;
					}

					me.join(theGame, opponentColor.Opposite);
					notMe = new Player((Integer)msg.get("opponentid"), (String)msg.get("opponentname"));
					notMe.join(theGame, opponentColor);
					new GameController(theGame).startGame();
					printBoard();
					break;

				case Action.EXIT_GAME:
					theGame = null;
					notMe = null;
					break;

				case Action.MOVE:
					theGame.getController().execute(me, lastMove);
					printBoard();
					break;

				default:
					SLog.write("unrecognized action");
					break;
			}
		}
		else if (msg.containsKey("push"))
		{
			switch((Integer)msg.get("event"))
			{
				case Push.GAME_STARTED:
					notMe = new Player((Integer)msg.get("opponentid"), (String)msg.get("opponentname"));
					notMe.join(theGame, me.getColor().Opposite);
					new GameController(theGame).startGame();
					printBoard();
					break;

				case Push.GAME_ENDED:
					notMe = null;
					theGame = null;
					break;

				case Push.GAME_MOVE:
					String move = (String)msg.get("move");
					theGame.getController().execute(notMe, move);
					printBoard();
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
		lastAction = Action.LOGIN;

        HashMap req = new HashMap();
        req.put("action", Action.LOGIN);
        req.put("username", name);
        req.put("password", pass);
        writer.write(req);

        listen(1);
    }

    public static void reg(String name, String pass) throws IOException
    {
		lastAction = Action.REGISTER;

        HashMap req = new HashMap();
        req.put("action", Action.REGISTER);
        req.put("username", name);
        req.put("password", pass);
        writer.write(req);

        listen(1);
    }

    public static void createGame(String name, String color) throws IOException
    {
		lastAction = Action.CREATE_GAME;
		joinColor = color;

        HashMap req = new HashMap();
        req.put("action", Action.CREATE_GAME);
        req.put("gamename", name);
		if (color != null)
		{
			req.put("color", color);
		}
        writer.write(req);

        listen(1);
    }

    public static void cancelGame(int gameid) throws IOException
    {
		lastAction = Action.CANCEL_GAME;

        HashMap req = new HashMap();
        req.put("action", Action.CANCEL_GAME);
        req.put("gameid", gameid);
        writer.write(req);

        listen(1);
    }

    public static void fetchGames(int offset, int limit) throws IOException
    {
		lastAction = Action.FETCH_GAMES;

        HashMap req = new HashMap();
        req.put("action", Action.FETCH_GAMES);
        if (offset != -1)
        {
            req.put("offset", offset);
        }
        if (limit != -1)
        {
            req.put("limit", limit);
        }
        writer.write(req);

        listen(1);
    }

    public static void joinGame(int gameID) throws IOException
    {
		lastAction = Action.JOIN_GAME;
		joinID = gameID;

        HashMap req = new HashMap();
        req.put("action", Action.JOIN_GAME);
        req.put("gameid", gameID);
        writer.write(req);

        listen(1);
    }

	public static void exitGame(int gameID) throws IOException
	{
		lastAction = Action.EXIT_GAME;

		HashMap req = new HashMap();
		req.put("action", Action.EXIT_GAME);
		req.put("gameid", gameID);
		writer.write(req);

		listen(1);
	}

	public static void move(String move) throws IOException
	{
		lastAction = Action.MOVE;
		lastMove = move;

		HashMap req = new HashMap();
		req.put("action", Action.MOVE);
		req.put("move", move);
		writer.write(req);

		listen(1);
	}

	public static void printBoard()
	{
		SLog.write(theGame);
	}
}
