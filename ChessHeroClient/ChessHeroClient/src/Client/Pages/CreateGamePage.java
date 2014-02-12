package Client.Pages;

import Client.Communication.Request;
import com.kt.api.*;
import com.kt.api.Action;
import com.kt.game.*;
import com.kt.utils.SLog;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/24/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateGamePage extends ChessHeroPage {

    public static GameSettings createGameSettings;//new CreateGameSettings();
    public JLabel infoLabel;
    public static boolean gameCreated;
    public static Game createdGame;
    private JButton createGameButton;
    private JButton lobbyPageButton;

    private JTextField  gameNameTextBox;
    private ButtonGroup radioButtonGroup;
    private JRadioButton toggleButtonWhite;
    private JSpinner turnTimeLimitSpinbox;
    private JSpinner gameTimeLimitSpinbox;
    private JSpinner minimumOpponentLevelSpinbox;
    private JSpinner maximumOpponentLevelSpinbox;


    private int playerLevel;

        // HELPER INNER CLASSES

        class GameSettings {
            public String GameName = "";
            public boolean IsWithWhite = true;
            public int TurnTimeLimit = 0;
            public int GameTimeLimit = 0;
            public int minOpponentLvL = 0;
            public int maxOpponentLvL = 0;

            public GameSettings(){

            }
            public GameSettings(
                    String gameName,
                    boolean isWithWhite,
                    int turnTimeLimit,
                    int gameTimeLimit,
                    int minOpponentLvL,
                    int maxOpponentLvL
            ){
                this.GameName = gameName;
                this.IsWithWhite = isWithWhite;
                this.TurnTimeLimit = turnTimeLimit;
                this.GameTimeLimit = gameTimeLimit;
                this.minOpponentLvL = minOpponentLvL;
                this.maxOpponentLvL = maxOpponentLvL;
            }

            @Override
            public String toString(){
                String isWithWhiteString = this.IsWithWhite ? "TRUE": "FALSE";
                return
                        this.GameName +"|"+
                        isWithWhiteString +"|"+
                        this.TurnTimeLimit + "|" +
                        this.GameTimeLimit + "|" +
                        this.minOpponentLvL + "|" +
                        this.maxOpponentLvL
                        ;
            }
        }

        public  CreateGamePage(){
            super();

            infoLabel = new JLabel(" ");

            infoLabel.setHorizontalAlignment(JLabel.CENTER);
            infoLabel.setHorizontalTextPosition(JLabel.CENTER);
            infoLabel.setFont(new Font("Serif", Font.BOLD, 12));
            infoLabel.setForeground(Color.green);


            this.setPageTitle("Create Game Page");
            //Initialize Components
            JPanel mainPanel = new JPanel();
            JPanel menuPanel = new JPanel();
//            mainPanel.setLayout(new GridBagLayout());
//            menuPanel.setLayout(new GridBagLayout());

            JLabel pageTitle = new JLabel(MAIN_TITLE);

            pageTitle.setHorizontalAlignment(JLabel.CENTER);
            pageTitle.setHorizontalTextPosition(JLabel.CENTER);
            pageTitle.setFont(new Font("Serif", Font.BOLD, 48));

            JLabel pageSubTitle = new JLabel(this.getPageTitle());

            pageSubTitle.setHorizontalAlignment(JLabel.CENTER);
            pageSubTitle.setHorizontalTextPosition(JLabel.CENTER);
            pageSubTitle.setFont(new Font("Serif", Font.BOLD, 32));

            JLabel gameNameLabel = new JLabel("Enter a name for your Game");
            JLabel turnTimeLimitLabel = new JLabel("Turn Time Limit In Minutes");
            JLabel gameTimeLimitLabel = new JLabel("Game Time Limit In Minutes");

            this.gameNameTextBox = new JTextField();

//            gameNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
//            gameNameTextBox.setHorizontalAlignment(SwingConstants.CENTER);

            lobbyPageButton = new JButton("Back To Lobby");
            createGameButton = new JButton("Create Game");

            toggleButtonWhite = new JRadioButton("Play With White");
            toggleButtonWhite.setSelected(true);
            JRadioButton toggleButtonBlack = new JRadioButton("Play With Black");

            JLabel minimumOpponentLevelLabel = new JLabel("Minimum Opponent Level");
            JLabel maximumOpponentLevelLabel = new JLabel("Maximum Opponent Level");

            SpinnerNumberModel spinnerTurnTimeModel = new SpinnerNumberModel(1, 0, 60,1);
            SpinnerNumberModel spinnerGameTimeModel = new SpinnerNumberModel(10, 0, 300, 10);
            //SpinnerNumberModel spinnerGameTimeModel = new SpinnerNumberModel(10, 0, 300, 10);
            playerLevel = 2;
            SpinnerNumberModel spinnerMinimumOpponentLevelModel = new SpinnerNumberModel(playerLevel, playerLevel > 10 ? playerLevel-10:0, playerLevel, 1);
            SpinnerNumberModel spinnerMaximumOpponentLevelModel = new SpinnerNumberModel(playerLevel, playerLevel, playerLevel < 90 ? playerLevel+10:99, 1);

//            SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss");
//            formatDate.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//            minimumOpponentLevelSpinbox = new JSpinner(spinnerTurnTimeModel);
//            minimumOpponentLevelSpinbox.setModel(new SpinnerDateModel(new Date(0), null, null, Calendar.MINUTE));
//            ((JSpinner.DefaultEditor) minimumOpponentLevelSpinbox.getEditor()).getTextField().setFormatterFactory(
//                    new DefaultFormatterFactory(new DateFormatter(formatDate)));//DateFormat.getDateInstance())));

            turnTimeLimitSpinbox = new JSpinner(spinnerTurnTimeModel);
            gameTimeLimitSpinbox = new JSpinner(spinnerGameTimeModel);
            minimumOpponentLevelSpinbox = new JSpinner(spinnerMinimumOpponentLevelModel);
            maximumOpponentLevelSpinbox = new JSpinner(spinnerMaximumOpponentLevelModel);

            toggleButtonWhite.setHorizontalAlignment(SwingConstants.CENTER);
            toggleButtonBlack.setHorizontalAlignment(SwingConstants.CENTER);
            gameNameTextBox.setHorizontalAlignment(SwingConstants.CENTER);

            mainPanel.setLayout(new GridBagLayout());
            menuPanel.setLayout(new GridLayout(6,2));

            //Add Components

            radioButtonGroup = new ButtonGroup();

            radioButtonGroup.add(toggleButtonWhite);
            radioButtonGroup.add(toggleButtonBlack);

            menuPanel.add(toggleButtonWhite);
            menuPanel.add(toggleButtonBlack);

            menuPanel.add(gameNameLabel);
            menuPanel.add(gameNameTextBox);

            menuPanel.add(turnTimeLimitLabel);
            menuPanel.add(turnTimeLimitSpinbox);

            menuPanel.add(gameTimeLimitLabel);
            menuPanel.add(gameTimeLimitSpinbox);


            menuPanel.add(minimumOpponentLevelLabel);
            menuPanel.add(minimumOpponentLevelSpinbox);
            menuPanel.add(maximumOpponentLevelLabel);
            menuPanel.add(maximumOpponentLevelSpinbox);

            //menuPanel.add(innerPanel);

            new GridBagConstraints();
            GridBagConstraints gridOpt = new GridBagConstraints();
            gridOpt.fill = GridBagConstraints.BOTH;
            gridOpt.insets = new Insets(20,200,20,200);
            gridOpt.gridx = 0;
            gridOpt.gridy = 0;
            gridOpt.weightx = 1;
            gridOpt.weighty = 0;
            mainPanel.add(pageTitle,gridOpt);

            gridOpt.insets = new Insets(0,200,20,200);
            gridOpt.gridy = 1;
            mainPanel.add(pageSubTitle, gridOpt);

            gridOpt.insets = new Insets(0,200,40,200);
            gridOpt.gridx = 0;
            gridOpt.gridy = 2;
            gridOpt.weighty = 6;
            mainPanel.add(menuPanel, gridOpt);

            gridOpt.insets = new Insets(0,200,20,200);
            gridOpt.gridy = 4;
            gridOpt.weighty = 0.5;
            mainPanel.add(createGameButton, gridOpt);

            gridOpt.gridy = 5;
            gridOpt.weighty = 0.5;
            mainPanel.add(lobbyPageButton, gridOpt);

            gridOpt.gridy = 6;
            gridOpt.insets = new Insets(0,200,20,200);
            mainPanel.add(infoLabel, gridOpt);

            this.setPagePanel(mainPanel);

            createGameSettings = new GameSettings();

            //Add Listeners

            lobbyPageButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    System.out.println("You clicked the Lobby page button");
                    handleLobbyPageButton();
                }
            });

            createGameButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    System.out.println("You clicked the Create Game Page button");
                    handleCreateGameButton();
                }
            });
        }

    //Handle Buttons

    private void handleLobbyPageButton() {
        SLog.write("Entered Lobby button HANDLER");
        getHolder().NavigateToPage(new LobbyPage());
    }

    private void handleCreateGameButton() {
        SLog.write("Entered Create Game button HANDLER");
        if(gameCreated == true)
        {
            Request request = new Request(Action.CANCEL_GAME);
            request.addParameter("gameid",createdGame.getID());

            this.getConnection().sendRequest(request);
        }
        else
        {
            createGameSettings = getCreateGameSettings();
            SLog.write(createGameSettings.toString());

            String color = createGameSettings.IsWithWhite ? "white" : "black";

            Request request = new Request(Action.CREATE_GAME);
            request.addParameter("gamename", createGameSettings.GameName);
            request.addParameter("timeout", createGameSettings.GameTimeLimit);
            request.addParameter("color", color);

            this.getConnection().sendRequest(request);
        }
    }


    //HELPER METHODS

    private GameSettings getCreateGameSettings (){
        GameSettings newGameSettings = new GameSettings();
        if (gameNameTextBox != null && gameNameTextBox.getText() != null){
            newGameSettings.GameName = gameNameTextBox.getText();
        }
        newGameSettings.TurnTimeLimit = Integer.parseInt(turnTimeLimitSpinbox.getValue().toString());
        newGameSettings.GameTimeLimit = Integer.parseInt(gameTimeLimitSpinbox.getValue().toString());
        newGameSettings.minOpponentLvL = Integer.parseInt(minimumOpponentLevelSpinbox.getValue().toString());
        newGameSettings.maxOpponentLvL = Integer.parseInt(maximumOpponentLevelSpinbox.getValue().toString());
        newGameSettings.IsWithWhite = toggleButtonWhite.isSelected();
        return newGameSettings;
    }

//        private Vector<Vector<String>> transformTableData(Vector<HallOfFameEntry> data){
//            Vector<Vector<String>> transformedData = new Vector<Vector<String>>();
//            for (HallOfFameEntry entry : data){
//                transformedData.add(new Vector<String>(Arrays.asList(entry.toString().split("\\|"))));
//            }
//            return  transformedData;
//        }


    @Override
    public void didReceiveMessage(HashMap<String, Object> message)
    {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        super.didReceiveMessage(message);
        SLog.write("In Push Event");

        String opponentName = (String)message.get("opponentname");
        Integer opponentId = (Integer)message.get("opponentid");

        Player opponent = new Player(opponentId, opponentName);

        opponent.join(createdGame,opponent.getColor());

        GameController gameContr = new GameController(createdGame);

        this.getHolder().NavigateToPage(new PlayGamePage(gameContr));
    }


    @Override
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response)
    {
        super.requestDidComplete(success, request, response);
        int resultCode = (Integer)response.get("result");
        int requestActionCode = request.getAction();

        switch (requestActionCode)
        {
            case Action.CANCEL_GAME:
                if (Result.OK == resultCode)
                {
                    gameCreated = false;
                    createdGame = null;
                    createGameButton.setText("Create Game");
                    lobbyPageButton.setEnabled(true);
                }
                break;
            case Action.CREATE_GAME:
                if (Result.OK == resultCode)
                {
                    String color = createGameSettings.IsWithWhite ? "white" : "black";
                    Integer gameID = (Integer)response.get("gameid");
                    createdGame = new Game(gameID, createGameSettings.GameName, createGameSettings.GameTimeLimit);
                    player.join(createdGame, (createGameSettings.IsWithWhite ? com.kt.game.Color.WHITE : com.kt.game.Color.BLACK));

                    gameCreated = true;
                    createGameButton.setText("Cancel Game");
                    lobbyPageButton.setEnabled(false);
                    infoLabel.setText("Game successfully created, waiting for another player to join...");
                }
                break;
        }
    }
//        GameController newGameController = new GameController(new Game(),new NetworkPlayer(1), ChessColor.White, new NetworkPlayer(2),ChessColor.Black);
//        newGameController.game.startNewGame(newGameController.game.getWhitePlayer(), newGameController.game.getBlackPlayer());
//
//        newGameController.game.setTakenPieces(newGameController.game.getPlayerStartingPieceSet(newGameController.player.getGamePlayer()),newGameController.opponent.getGamePlayer());

//        getHolder().NavigateToPage(new PlayGamePage(newGameController));








    }