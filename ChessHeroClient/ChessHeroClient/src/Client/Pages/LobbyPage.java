package Client.Pages;

import Client.Communication.Request;
import com.kt.api.*;
import com.kt.api.Action;
import com.kt.game.*;
import com.kt.utils.SLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/24/13
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class LobbyPage extends ChessHeroPage{

    public static final Vector<String> LOBBY_TABLE_COLUMNS =
            new Vector<String>(Arrays.asList(
                    "Game Name", "Created By", "Opponent Color")
    );

    public ArrayList<LobbyTableEntry> gameList = null;
    public JTable table = null;

    // HELPER INNER CLASSES

    //Class that describes table entries
    class LobbyTableEntry {
        public Integer gameID = null;
        public String gameName = null;
        public String createdBy = null;
        public String playerColor = null;
        public Integer playerID = null;

        public LobbyTableEntry(Integer gameID, String gameName, String createdBy,String playerColor,Integer playerID)
        {
            this.gameID = gameID;
            this.gameName = gameName;
            this.createdBy = createdBy;
            this.playerColor = playerColor;
            this.playerID = playerID;
        }

        @Override
        public String toString(){
            return this.gameName +"|"+ this.createdBy +"|"+ this.playerColor ;
        }
    }

    //Class that describes the table model of the lobby page
    class LobbyTableModel extends DefaultTableModel {

        public LobbyTableModel(Object[][] tableData, Object[] colNames) {
            super(tableData, colNames);
        }

        public LobbyTableModel(Vector tableData, Vector colNames) {
            super(tableData, colNames);
        }

        @Override
        public boolean isCellEditable(int row, int column)      //override isCellEditable
        {
            return false;
        }
    }

    public LobbyPage(){
        super();
        this.setPageTitle("Lobby Page");
        JPanel mainPanel = new JPanel();
        //JPanel menuPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        JLabel pageTitle = new JLabel(MAIN_TITLE);

        pageTitle.setHorizontalAlignment(JLabel.CENTER);
        pageTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageTitle.setFont(new Font("Serif", Font.BOLD, 48));

        JLabel pageSubTitle = new JLabel(this.getPageTitle());

        pageSubTitle.setHorizontalAlignment(JLabel.CENTER);
        pageSubTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageSubTitle.setFont(new Font("Serif", Font.BOLD, 32));

        JButton joinGameButton = new JButton("Join Game");
        JButton createGameButton = new JButton("Create Game");
        JButton hallOfFameButton = new JButton("Hall Of Fame");
        JButton logoutButton = new JButton("Logout");
        JButton refreshGameButton = new JButton("Refresh Games");

        table = new JTable();
        JScrollPane tableHolder = new JScrollPane(table);

        //Add Components

        new GridBagConstraints();
        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.insets = new Insets(20,100,20,100);
        gridOpt.gridx = 0;
        gridOpt.gridy = 0;
        gridOpt.weightx = 2;
        gridOpt.weighty = 0;
        gridOpt.gridwidth = 2;
        mainPanel.add(pageTitle,gridOpt);

        gridOpt.insets = new Insets(0,100,20,100);
        gridOpt.gridy = 1;
        mainPanel.add(pageSubTitle, gridOpt);

        gridOpt.insets = new Insets(0,100,40,100);
        gridOpt.gridx = 0;
        gridOpt.gridy = 2;
        gridOpt.weighty = 6;
        mainPanel.add(tableHolder, gridOpt);

        gridOpt.insets = new Insets(0,100,20,100);
        gridOpt.gridy = 4;
        gridOpt.weighty = 0.5;
        gridOpt.gridwidth = 2;
        mainPanel.add(refreshGameButton, gridOpt);

        gridOpt.insets = new Insets(0,100,20,10);
        gridOpt.gridy = 5;
        gridOpt.weighty = 0.5;
        gridOpt.gridwidth = 1;
        gridOpt.fill = GridBagConstraints.BOTH;
        mainPanel.add(joinGameButton, gridOpt);

        gridOpt.insets = new Insets(0,10,20,100);
        gridOpt.gridx = 1;
        mainPanel.add(createGameButton, gridOpt);

        gridOpt.insets = new Insets(0,100,20,10);
        gridOpt.gridx = 0;
        gridOpt.gridy = 6;
        mainPanel.add(hallOfFameButton, gridOpt);

        gridOpt.insets = new Insets(0,10,20,100);
        gridOpt.gridx = 1;
        mainPanel.add(logoutButton, gridOpt);


        this.setPagePanel(mainPanel);

        handleRefreshGameButton();

        //Add Listeners

        refreshGameButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                SLog.write("You clicked the REFRESH GAME button");
                handleRefreshGameButton();
            }
        });

        joinGameButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                SLog.write("You clicked the JOIN GAME button");
                int rowCount = table.getSelectedRowCount();
                if (rowCount == 1)
                {
                    int selectedRow = table.getSelectedRow();
                    LobbyTableEntry SelectedGame = gameList.get(selectedRow);
                    handleJoinGameButton(SelectedGame);
                }
            }
        });

        createGameButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                SLog.write("You clicked the CREATE GAME button");
                handleCreateGameButton();
            }
        });

        logoutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SLog.write("You clicked the LOGOUT button");
                handleLogoutButton();
            }
        });

        hallOfFameButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                SLog.write("You clicked the HALL OF FAME button");
                handleHallOfFameButton();
            }
        });
    }

    //Handle Buttons

    private void handleRefreshGameButton()
    {
        Request request = new Request(com.kt.api.Action.FETCH_GAMES);
        this.getConnection().sendRequest(request);
    }

    private void handleHallOfFameButton()
    {
        SLog.write("Entered HALL OF FAME button HANDLER");
        holder.NavigateToPage(new HallOfFamePage());
    }

    private void handleJoinGameButton(LobbyTableEntry selectedGame)
    {
        SLog.write("Entered Join Game with Name " + selectedGame.gameName + " ID " + selectedGame.gameID);
        Request request = new Request(Action.JOIN_GAME);
        request.addParameter("gameid", selectedGame.gameID);

        this.getConnection().sendRequest(request);
    }

    public void handleCreateGameButton()
    {
        this.holder.NavigateToPage(new CreateGamePage());
    }

    public void handleLogoutButton(){
        this.getHolder().player = null;
        this.getConnection().disconnect();
        this.holder.NavigateToPage(new LoginPage());
    }

    //HELPER METHODS
    private Vector<Vector<String>> transformTableData(ArrayList<LobbyTableEntry> data){
        Vector<Vector<String>> transformedData = new Vector<Vector<String>>();
        for (LobbyTableEntry entry : data){
            transformedData.add(new Vector<String>(Arrays.asList(entry.toString().split("\\|"))));
        }
        return  transformedData;
    }

    private ArrayList<LobbyTableEntry> getGamesFromFetchGamesList(ArrayList<HashMap> availableGames)
    {
        ArrayList<LobbyTableEntry> resultGames = new ArrayList<LobbyTableEntry>(availableGames.size());
        for (HashMap game : availableGames)
        {

            Integer gameId = (Integer)game.get("gameid");
            String gameName = (String)game.get("gamename");
            String playerName = (String)game.get("username");
            String playerColor = (String)game.get("usercolor");
            Integer playerID = (Integer)game.get("userid");

            LobbyTableEntry newTableEntry = new LobbyTableEntry(
                    gameId,
                    gameName,
                    playerName,
                    playerColor,
                    playerID
            );

            resultGames.add(newTableEntry);
        }
        return resultGames;
    }

    @Override
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response){
        super.requestDidComplete(success, request, response);
        int resultCode = (Integer)response.get("result");
        int requestActionCode = request.getAction();

        switch (requestActionCode)
        {
            case Action.FETCH_GAMES:
                if (Result.OK == resultCode)
                {
                    gameList = getGamesFromFetchGamesList((ArrayList<HashMap>)response.get("games"));

                    table.setModel(new LobbyTableModel(transformTableData(gameList), LOBBY_TABLE_COLUMNS));
                }
                break;
            case Action.JOIN_GAME:
                if (Result.OK == resultCode)
                {
                    com.kt.game.Color opponentColor = com.kt.game.Color.NONE;
                    Player opponent = null;

                    LobbyTableEntry SelectedGame = gameList.get(table.getSelectedRow());

                    if(SelectedGame != null)
                    {
                        opponentColor = (SelectedGame.playerColor.equals("white") ? com.kt.game.Color.WHITE : com.kt.game.Color.BLACK);
                        opponent = new Player(SelectedGame.playerID, SelectedGame.createdBy);
                    }

                    if (com.kt.game.Color.NONE == opponentColor || null == opponent)
                    {
                        SLog.write("Cannot start game as opponent info could not be found");

                    }

                    Game theGame = new Game(SelectedGame.gameID, SelectedGame.gameName, Game.NO_TIMEOUT);
                    this.getHolder().player.join(theGame, opponentColor.Opposite);
                    opponent.join(theGame, opponentColor);

                    GameController gameContr = new GameController(theGame);
                    this.getHolder().NavigateToPage(new PlayGamePage(gameContr));
                }
        }
    }
}