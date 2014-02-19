package Client.Pages;

import Client.ClientMain;
import Client.Communication.Request;
import Client.Pages.PlayGameVisualization.ChessBoardFieldPanel;
import Client.Pages.PlayGameVisualization.ChessBoardPanel;
import Client.Pages.PlayGameVisualization.ChessBoardTakenPiecesPanel;
import com.kt.api.Push;
import com.kt.api.Result;
import com.kt.game.*;
import com.kt.utils.SLog;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayGamePage extends ChessHeroPage implements MouseListener {

    public GameController gameController = null;

    public int PIECE_SIZE = 50;
    public int TAKEN_PIECE_SIZE = 40;
    private boolean isBoardReversed = false;

    private BoardField selectedField = null;
    private BoardField targetField = null;
    private boolean inMousePressedEvent = false;
    private String currentMoveString = null;


    //Control references
    private JLabel errorLabel;
    private ChessBoardPanel chessBoardPanel;
    private JPanel mainPanel = new JPanel();
    private JPanel chatPanel = new JPanel();
    private JPanel logPanel = new JPanel();
    private JPanel playerPanel = new JPanel();
    private JPanel opponentPanel = new JPanel();
    private JLabel playerPanelLabel = new JLabel();
    private JLabel opponentPanelLabel = new JLabel();
    private ChessBoardTakenPiecesPanel playerTakenPanel;// = new ChessBoardTakenPiecesPanel();
    private ChessBoardTakenPiecesPanel opponentTakenPanel;// = new ChessBoardTakenPiecesPanel();
    //Menu controls
    private JPanel menuPanel = new JPanel();
    private JToggleButton flipBoardButton = new JToggleButton("Flip Board", false);
    //private JPanel opponentTakenPanel = new JPanel();

    private String playerName = "";
    private String opponentName = "";

    public static Map<Pair<Byte, com.kt.game.Color>, BufferedImage> ChessPieceImages =
            new HashMap<Pair<Byte, com.kt.game.Color>,BufferedImage>();

    public static Map<Pair<Byte, com.kt.game.Color>, BufferedImage> TakenChessPieceImages =
            new HashMap<Pair<Byte, com.kt.game.Color>,BufferedImage>();

    public boolean getIsBoardReversed() {
        return this.isBoardReversed;
    }

    public void setIsBoardReversed(boolean isBoardReversed) {
        this.isBoardReversed = isBoardReversed;
    }


    public static class CHPlayGameLayoutConstants
    {
        public static final int PlayerNameLayoutTableRow = 3;
        public static final int PlayerNameLayoutTableCol = 1;
        public static final int OpponentNameLayoutTableRow = 1;
        public static final int OpponentNameLayoutTableCol = 1;
        public static final int PlayerTakenPiecesLayoutTableRow = 0;
        public static final int PlayerTakenPiecesLayoutTableCol = 0;
        public static final int OpponentTakenPiecesLayoutTableRow = 4;
        public static final int OpponentTakenPiecesLayoutTableCol = 0;

        public static final int ReversedPlayerNameLayoutTableRow = 1;
        public static final int ReversedPlayerNameLayoutTableCol = 1;
        public static final int ReversedOpponentNameLayoutTableRow = 3;
        public static final int ReversedOpponentNameLayoutTableCol = 1;
        public static final int ReversedPlayerTakenPiecesLayoutTableRow = 4;
        public static final int ReversedPlayerTakenPiecesLayoutTableCol = 0;
        public static final int ReversedOpponentTakenPiecesLayoutTableRow = 0;
        public static final int ReversedOpponentTakenPiecesLayoutTableCol = 0;
    }

    public PlayGamePage(GameController gameController){
        super();

        this.getConnection();

        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }

        errorLabel = new JLabel(" ");

        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setHorizontalTextPosition(JLabel.CENTER);
        errorLabel.setFont(new Font("Serif", Font.BOLD, 12));
        errorLabel.setForeground(Color.red);

        this.gameController = gameController;
        this.gameController.startGame();
        playerName = "Player - " + this.gameController.game.getPlayer1().getName();
        opponentName = "Opponent - " + this.gameController.game.getPlayer2().getName();

        chessBoardPanel = new ChessBoardPanel(this.gameController.board, PIECE_SIZE);

        playerTakenPanel = new ChessBoardTakenPiecesPanel(
               this.gameController.game, this.gameController.game.getPlayer2().getColor(),TAKEN_PIECE_SIZE);

        opponentTakenPanel = new ChessBoardTakenPiecesPanel(
                this.gameController.game, this.gameController.game.getPlayer1().getColor(),TAKEN_PIECE_SIZE);


        com.kt.game.Color playerColor = (this.gameController.game.getPlayer1().getColor());
        boolean test = false;
        if (playerColor == com.kt.game.Color.WHITE)
            test = false;
        else
            test = true;
        //this.setIsBoardReversed(test);
        this.chessBoardPanel.setIsBoardReversed(test);

        RearrangeLayout();
        //mainPanel.setLayout(new GridBagLayout());
        flipBoardButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                handleFlipBoard();
            }
        });

        this.AddEventListenersToFields();
    }

    private void RearrangeLayout() {

        this.removeAll();
        mainPanel.setLayout(new GridBagLayout());

        //mainPanel.setBackground(new Color(255,0,0));
        chatPanel.setBackground(new Color(255,0,0));
        logPanel.setBackground(new Color(0,255,0));
        //playerPanel.setBackground(new Color(0,0,255));
        //opponentPanel.setBackground(new Color(255,255,0));
        //playerTakenPanel.setBackground(new Color(255,0,255));
        //opponentTakenPanel.setBackground(new Color(0,255,255));
        //chessBoardPanel.setBackground(new Color(255,0,0));

        playerPanelLabel.setText(playerName);
        opponentPanelLabel.setText(opponentName);
        playerPanel.add(playerPanelLabel);
        opponentPanel.add(opponentPanelLabel);

        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.gridy = 2;
        gridOpt.gridx = 1;
        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        //mainPanel.add(pageTitle,gridOpt);

        chessBoardPanel.redrawBoard();

        mainPanel.add(chessBoardPanel, gridOpt);

        gridOpt.fill = GridBagConstraints.HORIZONTAL;

//        gridOpt.gridy = 0;
//        gridOpt.gridx = 0;

        if(getIsBoardReversed() == true)
            gridOpt.gridy = CHPlayGameLayoutConstants.ReversedOpponentTakenPiecesLayoutTableRow;
        else
            gridOpt.gridy = CHPlayGameLayoutConstants.OpponentTakenPiecesLayoutTableRow;

        if(getIsBoardReversed() == true)
            gridOpt.gridx = CHPlayGameLayoutConstants.ReversedOpponentTakenPiecesLayoutTableCol;
        else
            gridOpt.gridx = CHPlayGameLayoutConstants.OpponentTakenPiecesLayoutTableCol;

        gridOpt.weighty = 1;
        gridOpt.weightx = 3;
        gridOpt.gridwidth = 3;
        gridOpt.gridheight = 1;

        mainPanel.add(opponentTakenPanel, gridOpt);

//        gridOpt.gridx = 0;
//        gridOpt.gridy = 4;
        if(getIsBoardReversed() == true)
            gridOpt.gridy =CHPlayGameLayoutConstants.ReversedPlayerTakenPiecesLayoutTableRow;
        else
            gridOpt.gridy =CHPlayGameLayoutConstants.PlayerTakenPiecesLayoutTableRow;

        if(getIsBoardReversed() == true)
            gridOpt.gridx =CHPlayGameLayoutConstants.ReversedPlayerTakenPiecesLayoutTableCol;
        else
            gridOpt.gridx =CHPlayGameLayoutConstants.PlayerTakenPiecesLayoutTableCol;

        gridOpt.weighty = 1;
        gridOpt.weightx = 3;
        gridOpt.gridwidth = 3;
        gridOpt.gridheight = 1;
        mainPanel.add(playerTakenPanel, gridOpt);
        gridOpt.fill = GridBagConstraints.VERTICAL;

        gridOpt.gridy = 1;
        gridOpt.gridx = 0;
        gridOpt.weighty = 3;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 3;
        mainPanel.add(chatPanel, gridOpt);

        gridOpt.gridy = 1;
        gridOpt.gridx = 2;
        gridOpt.weighty = 3;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 3;
        mainPanel.add(logPanel, gridOpt);

        gridOpt.fill = GridBagConstraints.HORIZONTAL;

//        gridOpt.gridy = 1;
//        gridOpt.gridx = 1;

        if(getIsBoardReversed() == true)
            gridOpt.gridy = CHPlayGameLayoutConstants.ReversedPlayerNameLayoutTableRow;
        else
            gridOpt.gridy = CHPlayGameLayoutConstants.PlayerNameLayoutTableRow;

        if(getIsBoardReversed() == true)
            gridOpt.gridx = CHPlayGameLayoutConstants.ReversedPlayerNameLayoutTableCol;
        else
            gridOpt.gridx = CHPlayGameLayoutConstants.PlayerNameLayoutTableCol;

        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        mainPanel.add(playerPanel, gridOpt);

//        gridOpt.gridy = 3;
//        gridOpt.gridx = 1;
        if(getIsBoardReversed() == true)
            gridOpt.gridy = CHPlayGameLayoutConstants.ReversedOpponentNameLayoutTableRow;
        else
            gridOpt.gridy = CHPlayGameLayoutConstants.OpponentNameLayoutTableRow;

        if(getIsBoardReversed() == true)
            gridOpt.gridx = CHPlayGameLayoutConstants.ReversedOpponentNameLayoutTableCol;
        else
            gridOpt.gridx = CHPlayGameLayoutConstants.OpponentNameLayoutTableCol;

        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        mainPanel.add(opponentPanel, gridOpt);

        //Menu and Buttons
        menuPanel.setLayout(new GridLayout());
        menuPanel.add(flipBoardButton);

        gridOpt.gridy = 5;
        gridOpt.gridx = 1;
        gridOpt.weighty = 1;
        gridOpt.weightx = 3;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        gridOpt.fill = GridBagConstraints.HORIZONTAL;
//        errorLabel.setBorder(BorderFactory.createLineBorder(Color.black,1));
        mainPanel.add(errorLabel, gridOpt);

        gridOpt.gridy = 6;
        gridOpt.fill = GridBagConstraints.HORIZONTAL;
//        menuPanel.setBorder(BorderFactory.createLineBorder(Color.black,1));


        mainPanel.add(menuPanel, gridOpt);

        this.mainPanel.updateUI();

        this.setPagePanel(mainPanel);

        mainPanel.setVisible(true);
    }

    private void loadImages() throws IOException {
        String path = "ChessHeroClient/src/Client/Pages/Pieces/";
        //File file = new File(path + "BlackPawn.png");
        BufferedImage BlackPawn = ImageIO.read(new File(path + "BlackPawn.png"));
        BufferedImage BlackKing = ImageIO.read(new File(path + "BlackKing.png"));
        BufferedImage BlackKnight = ImageIO.read(new File(path + "BlackKnight.png"));
        BufferedImage BlackQueen = ImageIO.read(new File(path + "BlackQueen.png"));
        BufferedImage BlackRook = ImageIO.read(new File(path + "BlackRook.png"));
        BufferedImage BlackBishop = ImageIO.read(new File(path + "BlackBishop.png"));

        BufferedImage WhitePawn = ImageIO.read(new File(path + "WhitePawn.png"));
        BufferedImage WhiteKing = ImageIO.read(new File(path + "WhiteKing.png"));
        BufferedImage WhiteKnight = ImageIO.read(new File(path + "WhiteKnight.png"));
        BufferedImage WhiteQueen = ImageIO.read(new File(path + "WhiteQueen.png"));
        BufferedImage WhiteRook = ImageIO.read(new File(path + "WhiteRook.png"));
        BufferedImage WhiteBishop = ImageIO.read(new File(path + "WhiteBishop.png"));

        //BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
        //public BufferedImage buffImg = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);

        //Resize Chess Board Pieces
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.PAWN,com.kt.game.Color.BLACK),resizeImage(BlackPawn,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KING,com.kt.game.Color.BLACK),resizeImage(BlackKing,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KNIGHT,com.kt.game.Color.BLACK),resizeImage(BlackKnight,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.QUEEN,com.kt.game.Color.BLACK),resizeImage(BlackQueen,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.ROOK,com.kt.game.Color.BLACK),resizeImage(BlackRook,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.BISHOP,com.kt.game.Color.BLACK),resizeImage(BlackBishop,this.PIECE_SIZE));

        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.PAWN,com.kt.game.Color.WHITE),resizeImage(WhitePawn,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KING,com.kt.game.Color.WHITE),resizeImage(WhiteKing,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KNIGHT,com.kt.game.Color.WHITE),resizeImage(WhiteKnight,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.QUEEN,com.kt.game.Color.WHITE),resizeImage(WhiteQueen,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.ROOK,com.kt.game.Color.WHITE),resizeImage(WhiteRook,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.BISHOP,com.kt.game.Color.WHITE),resizeImage(WhiteBishop,this.PIECE_SIZE));


        //Resize Taken Chess Pieces Images

        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.PAWN,com.kt.game.Color.BLACK),resizeImage(BlackPawn,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KING,com.kt.game.Color.BLACK),resizeImage(BlackKing,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KNIGHT,com.kt.game.Color.BLACK),resizeImage(BlackKnight,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.QUEEN,com.kt.game.Color.BLACK),resizeImage(BlackQueen,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.ROOK,com.kt.game.Color.BLACK),resizeImage(BlackRook,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.BISHOP,com.kt.game.Color.BLACK),resizeImage(BlackBishop,this.TAKEN_PIECE_SIZE));

        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.PAWN,com.kt.game.Color.WHITE),resizeImage(WhitePawn,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KING,com.kt.game.Color.WHITE),resizeImage(WhiteKing,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.KNIGHT,com.kt.game.Color.WHITE),resizeImage(WhiteKnight,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.QUEEN,com.kt.game.Color.WHITE),resizeImage(WhiteQueen,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.ROOK,com.kt.game.Color.WHITE),resizeImage(WhiteRook,this.TAKEN_PIECE_SIZE));
        TakenChessPieceImages.put(new Pair<Byte, com.kt.game.Color>(ChessPiece.Tag.BISHOP,com.kt.game.Color.WHITE),resizeImage(WhiteBishop,this.TAKEN_PIECE_SIZE));

//        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Pawn,ChessColor.White),WhitePawn);
//        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Pawn,ChessColor.White),WhitePawn);

    }

    private BufferedImage resizeImage(BufferedImage imageToResize, int intPieceSize) {
        BufferedImage result = new BufferedImage(intPieceSize, intPieceSize, imageToResize.getType());
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(imageToResize, 0, 0, intPieceSize, intPieceSize, 0, 0, imageToResize.getWidth(), imageToResize.getHeight(), null);
        g.dispose();
        return result;
    }

    private void handleFlipBoard() {
        this.setIsBoardReversed(!getIsBoardReversed());
        this.chessBoardPanel.setIsBoardReversed(!this.chessBoardPanel.getIsBoardReversed());
        RearrangeLayout();
        //this.chessBoardPanel.testShit();
    }

    public void AddEventListenersToFields(){
        for (int i = 0; i<8;i++)
        {
            for (int j = 0; j<8;j++)
            {
                this.chessBoardPanel.chessBoardFields[i][j].addMouseListener(this);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        if(e.getButton() == 3){
//            ChessBoardFieldPanel component = (ChessBoardFieldPanel)e.getSource();
//            Position position = component.fieldPosition;
//            Position oldPosition = null;
//            if (selectedField != null)
//            {
//                oldPosition = selectedField.getPosition();
//                ChessBoardFieldPanel selectedFieldView = chessBoardPanel.getField(selectedField.getPosition());
//                if(selectedFieldView.getIsSelected() == true) selectedFieldView.toggleIsSelected();
//
//                selectedField = null;
//            }
//
//            if(oldPosition == null ||
//                    oldPosition.getX() != position.getX() ||
//                    oldPosition.getY() != position.getY())
//            {
//                if (isSelectionValid(gameController.game.getField(position),ClientMain.player))
//                {
//                    this.selectedField = gameController.game.getField(position);
//                    if(component.getIsHighlighted() == true) component.toggleIsHighlighted();
//                    component.toggleIsSelected();
//                }
//            }
//
//        }
        SLog.write("clicked at: " + ((ChessBoardFieldPanel) e.getSource()).fieldPosition);
    }


    private void selectField (ChessBoardFieldPanel fieldViewToSelect)
    {
        BoardField correspondingField  = gameController.game.getField(fieldViewToSelect.fieldPosition);
        selectField(correspondingField, fieldViewToSelect);

    }
    private void selectField (BoardField fieldToSelect)
    {
        ChessBoardFieldPanel correspondingFieldView = this.chessBoardPanel.getField(fieldToSelect.getPosition());
        selectField(fieldToSelect, correspondingFieldView);
    }
    private void selectField (BoardField fieldToSelect, ChessBoardFieldPanel fieldViewToSelect)
    {
        fieldViewToSelect.setIsHighlighted(false);
        fieldViewToSelect.setIsSelected(true);
        this.selectedField = fieldToSelect;
    }
    private void highLightField (BoardField fieldToSelect)
    {
        ChessBoardFieldPanel correspondingFieldView = this.chessBoardPanel.getField(fieldToSelect.getPosition());

        if (correspondingFieldView.getIsSelected() == false)
        {
            correspondingFieldView.setIsHighlighted(true);
        }
    }
    private void highLightField (ChessBoardFieldPanel fieldToSelect)
    {

        if (fieldToSelect.getIsSelected() == false)
        {
            fieldToSelect.setIsHighlighted(true);
        }
    }

    private void deselectField ()
    {
        ChessBoardFieldPanel selectedFieldView = chessBoardPanel.getField(selectedField.getPosition());
        selectedFieldView.setIsHighlighted(false);
        selectedFieldView.setIsSelected(false);
        selectedField = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == 1 )//&& (e.getWhen() - new Date().getTime()) > 100
        {
            inMousePressedEvent = true;
            if (selectedField != null)
            {
                deselectField();
            }

            ChessBoardFieldPanel component = (ChessBoardFieldPanel)e.getSource();
            Position position = component.fieldPosition;
            BoardField correspondingField  = gameController.game.getField(position);
            if (isSelectionValid(correspondingField, ClientMain.player))
            {
                selectField(correspondingField, component);

                setCursorToChessPiece(component);

                SLog.write("source pos = " + position);
                SLog.write("source field = " + selectedField);
            }
            else
            {
                inMousePressedEvent = false;
            }
        }
        else
        {
            inMousePressedEvent = false;
        }
    }

    private void setCursorToChessPiece(ChessBoardFieldPanel boardFieldView)
    {
        BufferedImage image = boardFieldView.getFiledImage();
        if(image != null)
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Point cursorHotSpot = new Point(0,0);
            Cursor customCursor = toolkit.createCustomCursor( boardFieldView.getFiledImage(), cursorHotSpot, "Cursor");
            this.chessBoardPanel.setCursor(customCursor);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (inMousePressedEvent)
        {
            inMousePressedEvent = false;

            if(selectedField != null)
            {
                ChessBoardFieldPanel component = (ChessBoardFieldPanel)e.getSource();

                component.setIsHighlighted(false);
                component.setIsSelected(false);

                Position position = selectedField.getPosition();

                SLog.write("source pos = " + position);
                SLog.write("source field = " + selectedField);

                int posX = component.getX() + e.getX();
                int posY = component.getY() + e.getY();

                Component targetComponent = this.chessBoardPanel.getComponentAt(posX, posY);

                if (targetComponent != null)
                {
                    ChessBoardFieldPanel targetComponentAsChessBoardField = (ChessBoardFieldPanel)targetComponent;

                    Position targetPosition = targetComponentAsChessBoardField.fieldPosition;
                    if(isTargetMoveValid(ClientMain.player,selectedField,gameController.game.getField(targetPosition)))
                    {
                        targetField = gameController.game.getField(targetPosition);

                        SLog.write("target pos = " + targetPosition);
                        SLog.write("target field = " + targetField);

                        targetComponentAsChessBoardField.setIsSelected(false);
                        targetComponentAsChessBoardField.setIsHighlighted(true);

                        this.currentMoveString = createMoveStringFromPositions(
                                selectedField.getPosition(),
                                targetField.getPosition()
                        );

                        Request request = new Request(com.kt.api.Action.MOVE);
                        request.addParameter("move",this.currentMoveString);

                        this.getConnection().sendRequest(request);

                    }
                }

                deselectField();
                this.targetField = null;

                this.chessBoardPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            else
            {
                SLog.write("Error selected field shouldn't be null here");
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ChessBoardFieldPanel component = (ChessBoardFieldPanel)e.getSource();
//        if(selectedField != null)
//        {
        if(component.getIsSelected() == false)
        {
            component.setIsHighlighted(true);
        }
//        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ChessBoardFieldPanel component = (ChessBoardFieldPanel)e.getSource();
//        if(selectedField != null)
//        {
        if(component.getIsSelected() == false)
        {
            component.setIsHighlighted(false);
        }
//        }
    }

    private boolean isSelectionValid(BoardField selectedField, Player me)
    {
        boolean result = false;
        if (this.gameController.game.getTurn().equals(me))
        {
            ChessPiece chessPiece = selectedField.getChessPiece();
            if (chessPiece != null)
            {
                result = chessPiece.getOwner().equals(me);
                if(result == true)
                {
                    errorLabel.setText("  ");
                }
                else
                {
                    SLog.write("Not your chess piece (client)");
                    errorLabel.setText("Not your chess piece");
                }
            }
            else
            {
                SLog.write("No chess piece to select (client)");
                errorLabel.setText("No chess piece to select");
            }
        }
        else
        {
            SLog.write("Not your turn (client)");
            errorLabel.setText("Not your turn");
        }
        return result;
    }



    private boolean isTargetMoveValid(Player executor, BoardField from, BoardField to)
    {
        ChessPiece movedPiece = from.getChessPiece();
        if(movedPiece != null)
        {
            int resCode = gameController.validateMove(
                    executor,
                    movedPiece,
                    from.getPosition(),
                    to.getPosition(),
                    this.gameController.getNewMoveContext()
            );

            if (resCode == Result.WRONG_MOVE)
            {
                errorLabel.setText("Invalid move - your king will be in chess!");
                return true;
            }
            else if (resCode == Result.INVALID_MOVE)
            {
                errorLabel.setText("Invalid move");
            }
            else if (resCode == Result.OK)
            {
//                if (gameController.game.getIsInCheck(ClientMain.player) == false)
//                {
                    errorLabel.setText("Valid move");
//                }
//                else
//                {
//                    errorLabel.setText("Invalid move - your king will be in chess!");
//                    return false;
//                }
            }

            return resCode == 0 ? true : false;
        }
        else
        {
            return false;
        }
    }

    private String createMoveStringFromPositions(Position from, Position to)
    {
        return Position.boardPositionFromPosition(from) + Position.boardPositionFromPosition(to);
    }

    @Override
    public void didReceiveMessage(HashMap<String, Object> message)
    {
        super.didReceiveMessage(message);
        SLog.write("In Push Event Play Game");

        int code = (Integer)message.get("event");

        switch(code)
        {
            case Push.GAME_MOVE:
                String opponentMove = (String)message.get("move");
                this.gameController.execute(this.gameController.game.getPlayer2(), opponentMove);

                if (message.containsKey("attackers"))
                {
                    ArrayList<Object> attackersList = (ArrayList<Object>)message.get("attackers");
                    SLog.write(attackersList.size());
                    if (attackersList.size() > 0)
                    {
                        for (Object piece : attackersList)
                        {
                            Position piecePosition =  Position.positionFromBoardPosition(piece.toString());
                            highLightField(this.chessBoardPanel.getField(piecePosition));
                        }
                        if( gameController.game.getIsInCheck(ClientMain.player))
                        {
                            errorLabel.setText("You are in Check!");
                        }
                        else
                        {
                            errorLabel.setText("Error in in check state!");
                        }
                    }

                }

                this.chessBoardPanel.updateBoard(gameController.board);
                this.RearrangeLayout();
                break;
            case Push.GAME_END:
                break;
            case Push.GAME_SAVE:
                break;
        }
    }

    @Override
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response)
    {
        super.requestDidComplete(success, request, response);
        int resultCode = (Integer)response.get("result");

        switch (resultCode)
        {
            case Result.OK :
                this.errorLabel.setText("move successfully executed");
                this.gameController.execute(ClientMain.player, currentMoveString);
                if(this.gameController.game.getIsInCheck(gameController.game.getPlayer2()) == true)
                {
                    errorLabel.setText("Check!");
                }
                this.chessBoardPanel.updateBoard(gameController.board);
                this.RearrangeLayout();
                break;
            case Result.MOVE_NA :
                errorLabel.setText("Not applicable move");
                break;
            case Result.INVALID_MOVE_FORMAT :
                errorLabel.setText("Invalid move format");
                break;
            case Result.NOT_YOUR_TURN :
                errorLabel.setText("It is not your turn");
                break;
            case Result.NO_CHESSPIECE :
                errorLabel.setText("No chess piece at that position");
                break;
            case Result.NOT_YOUR_CHESSPIECE :
                errorLabel.setText("Attempting to move your opponent's chess piece");
                break;
            case Result.INVALID_MOVE :
                errorLabel.setText("Invalid move");
                break;
            case Result.WRONG_MOVE :
                errorLabel.setText("Invalid move - your king will be in chess!");
                break;
            case Result.MISSING_PROMOTION :
                errorLabel.setText("You are moving a pawn to its highest rank but you have not specified promotion.");
                break;
        }
    }
}
