package Client.Pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

            JButton lobbyPageButton = new JButton("Back To Lobby");
            JButton createGameButton = new JButton("Create Game");

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
        System.out.println("Entered Lobby button HANDLER");
        holder.NavigateToPage(new LobbyPage());
    }

    private void handleCreateGameButton() {
        System.out.println("Entered Create Game button HANDLER");
        createGameSettings = getCreateGameSettings();
        System.out.println(createGameSettings.toString());
        holder.NavigateToPage(new LobbyPage());
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
    }