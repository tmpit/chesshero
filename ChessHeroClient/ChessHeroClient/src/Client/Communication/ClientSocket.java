package Client.Communication;

import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/23/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientSocket
{
    private Socket sock = null;
    private CHESCOWriter writer;
    private CHESCOReader reader;

    ClientSocket(String address, int port, int connectionTimeout) throws IOException
    {
        InetAddress addr = InetAddress.getByName(address);
        sock = new Socket();
        sock.connect(new InetSocketAddress(addr, port), connectionTimeout);
        sock.setKeepAlive(true);
        sock.setSoTimeout(0);

        writer = new CHESCOWriter(sock.getOutputStream());
        reader = new CHESCOReader(sock.getInputStream());
    }

    public boolean isConnected()
    {
        // isConnected returns true if the socket has ever been connected, meaning it will return true even after
        // closing the socket. Vice-versa, isClosed returns true only if the socket has ever been closed
        if (sock.isClosed())
        {
            return false;
        }

        return sock.isConnected();
    }

    public void disconnect() throws IOException
    {
        sock.close();
    }

    public void setTimeout(int millis) throws IOException
    {
        sock.setSoTimeout(millis);
    }

    public int getTimeout() throws IOException
    {
        return sock.getSoTimeout();
    }

    public void write(Request request) throws IOException
    {
        HashMap map = request.getParametersMap();
        writer.write(map);
    }

    public Object read() throws IOException, EOFException
    {
        return reader.read();
    }
}
