import com.kt.*;
import com.kt.api.Action;
import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;
import com.kt.utils.SLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Toshko on 12/7/13.
 */
public class Main
{
    static Socket sock;

    private static CHESCOReader reader;
    private static CHESCOWriter writer;

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
                routine();
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
                listen(Integer.parseInt(args[1]));
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
                createGame(args[1]);
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
            else
            {
                SLog.write("Unrecognized command");
            }
        }
    }

    public static void routine() throws IOException
    {
        connect();
        login("tttt", "pppp");
    }

    public static void connect() throws IOException
    {
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
                SLog.write(reader.read());
            }
        }
        else
        {
            while (messages-- > 0)
            {
                SLog.write(reader.read());
            }
        }
    }

    public static void login(String name, String pass) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.LOGIN);
        req.put("username", name);
        req.put("password", pass);
        writer.write(req);

        listen(1);
    }

    public static void reg(String name, String pass) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.REGISTER);
        req.put("username", name);
        req.put("password", pass);
        writer.write(req);

        listen(1);
    }

    public static void createGame(String name) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.CREATE_GAME);
        req.put("gamename", name);
        writer.write(req);

        listen(1);
    }

    public static void cancelGame(int gameid) throws IOException
    {
        HashMap req = new HashMap();
        req.put("action", Action.CANCEL_GAME);
        req.put("gameid", gameid);
        writer.write(req);

        listen(1);
    }

    public static void fetchGames(int offset, int limit) throws IOException
    {
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
        HashMap req = new HashMap();
        req.put("action", Action.JOIN_GAME);
        req.put("gameid", gameID);
        writer.write(req);

        listen(1);
    }
}
