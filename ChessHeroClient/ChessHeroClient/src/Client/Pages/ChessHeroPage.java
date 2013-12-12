package Client.Pages;

import Client.ClientMain;
import Client.Communication.Connection;
import Client.Communication.ConnectionListener;
import Client.Communication.Request;
import com.kt.utils.SLog;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/23/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChessHeroPage extends JPanel implements ConnectionListener{

    public static final String MAIN_TITLE = "Chess Hero";

    protected ClientMain holder = null;
    private Connection con = null;
    protected static boolean isConnected = false;

    //protected JList<JComponent> pageComponents = new JList<JComponent>();

    private JPanel pagePanel = null;
    private String pageTitle = "";

    public Connection getConnection(){
        if (this.con == null)    {
            this.con = Connection.getSingleton();
            this.con.addEventListener(this);
        }
        return con;
    }

    public ClientMain getHolder() {
        return this.holder;
    }

    public void setHolder(ClientMain holder) {
        this.holder = holder;
    }

    public ChessHeroPage(){
       //getConnection();
        if (isConnected == false){
            getConnection().connect();
        }

    }

    public JPanel getPagePanel() {
        return pagePanel;
    }

    protected void setPagePanel(JPanel pagePanel) {
        this.pagePanel = pagePanel;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    protected void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    @Override
    public void socketConnected() {
        SLog.write("in socked connected event");
            isConnected = true;
    }

    @Override
    public void socketFailedToConnect() {
        SLog.write("in socked failed to connect event");

    }

    @Override
    public void socketDisconnected(boolean error) {
        SLog.write("in socked disconnected event");
        isConnected = false;
    }

    @Override
    public void didReceiveMessage(HashMap<String, Object> message) {
        SLog.write("in recieved message connected event");

    }

    @Override
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response){
        SLog.write("in request did complete event");
    }
}
