package Client.Pages;

import Client.Pages.PlayGameVisualization.ChessBoardPanel;
import Client.Pages.PlayGameVisualization.ChessBoardTakenPiecesPanel;
import com.kt.game.*;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayGamePage extends ChessHeroPage{

    public GameController gameController = null;

    public int PIECE_SIZE = 50;
    public int TAKEN_PIECE_SIZE = 40;
    private boolean isBoardReversed = false;

    //Control references
    private ChessBoardPanel chessBoardPanel;
    private JPanel mainPanel = new JPanel();
    private JPanel chatPanel = new JPanel();
    private JPanel logPanel = new JPanel();
    private JPanel playerPanel = new JPanel();
    private JPanel opponentPanel = new JPanel();
    private ChessBoardTakenPiecesPanel playerTakenPanel;// = new ChessBoardTakenPiecesPanel();
    private ChessBoardTakenPiecesPanel opponentTakenPanel;// = new ChessBoardTakenPiecesPanel();
    //Menu controls
    private JPanel menuPanel = new JPanel();
    private JToggleButton flipBoardButton = new JToggleButton("Flip Board", false);
    //private JPanel opponentTakenPanel = new JPanel();

    public static Map<Pair<Byte, com.kt.game.Color>, BufferedImage> ChessPieceImages =
            new HashMap<Pair<Byte, com.kt.game.Color>,BufferedImage>();

    public static Map<Pair<Byte, com.kt.game.Color>, BufferedImage> TakenChessPieceImages =
            new HashMap<Pair<Byte, com.kt.game.Color>,BufferedImage>();

    public boolean getIsBoardReversed() {
        return isBoardReversed;
    }

    public void setIsBoardReversed(boolean isBoardReversed) {
        this.isBoardReversed = isBoardReversed;
        this.chessBoardPanel.setIsBoardReversed(isBoardReversed);
        RearrangeLayout();
    }

    public static class CHPlayGameLayoutConstants
    {
        public static final int PlayerNameLayoutTableRow = 3;
        public static final int PlayerNameLayoutTableCol = 1;
        public static final int OpponentNameLayoutTableRow = 1;
        public static final int OpponentNameLayoutTableCol = 1;
        public static final int PlayerTakenPiecesLayoutTableRow = 4;
        public static final int PlayerTakenPiecesLayoutTableCol = 0;
        public static final int OpponentTakenPiecesLayoutTableRow = 0;
        public static final int OpponentTakenPiecesLayoutTableCol = 0;

        public static final int ReversedPlayerNameLayoutTableRow = 1;
        public static final int ReversedPlayerNameLayoutTableCol = 1;
        public static final int ReversedOpponentNameLayoutTableRow = 3;
        public static final int ReversedOpponentNameLayoutTableCol = 1;
        public static final int ReversedPlayerTakenPiecesLayoutTableRow = 0;
        public static final int ReversedPlayerTakenPiecesLayoutTableCol = 0;
        public static final int ReversedOpponentTakenPiecesLayoutTableRow = 4;
        public static final int ReversedOpponentTakenPiecesLayoutTableCol = 0;
    }

    //public GameController(Game game, NetworkPlayer me, NetworkPlayer opponent){

    public PlayGamePage(GameController gameController){
        super();

        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gameController = gameController;
        this.gameController.startGame();
        //this.gameController.game.blackPlayer = new GamePlayer(ChessColor.Black);
        //this.gameController.game.whitePlayer = new GamePlayer(ChessColor.White);
        //this.gameController.game.startNewGame(
        //        this.gameController.game.getWhitePlayer(),
        //        this.gameController.game.getBlackPlayer()
        //);
        chessBoardPanel = new ChessBoardPanel(this.gameController.board, PIECE_SIZE);
        playerTakenPanel = new ChessBoardTakenPiecesPanel(
               this.gameController.game, this.gameController.game.getPlayer2().getColor(),TAKEN_PIECE_SIZE);
        opponentTakenPanel = new ChessBoardTakenPiecesPanel(
                this.gameController.game, this.gameController.game.getPlayer1().getColor(),TAKEN_PIECE_SIZE);
        this.setIsBoardReversed((this.gameController.game.getPlayer1().getColor() == com.kt.game.Color.WHITE) ? false: true);
        //chessBoardPanel.setIsBoardReversed(this.isBoardReversed);
        RearrangeLayout();
        //mainPanel.setLayout(new GridBagLayout());
        flipBoardButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                handleFlipBoard();
            }
        });
    }

    private void RearrangeLayout() {

        mainPanel.setLayout(new GridBagLayout());

        //mainPanel.setBackground(new Color(255,0,0));
        chatPanel.setBackground(new Color(255,0,0));
        logPanel.setBackground(new Color(0,255,0));
        playerPanel.setBackground(new Color(0,0,255));
        opponentPanel.setBackground(new Color(255,255,0));
        //playerTakenPanel.setBackground(new Color(255,0,255));
        //opponentTakenPanel.setBackground(new Color(0,255,255));
        //chessBoardPanel.setBackground(new Color(255,0,0));

        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.gridy = 2;
        gridOpt.gridx = 1;
        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        //mainPanel.add(pageTitle,gridOpt);

        mainPanel.add(chessBoardPanel, gridOpt);

        gridOpt.fill = GridBagConstraints.HORIZONTAL;

//        gridOpt.gridy = 0;
//        gridOpt.gridx = 0;
        gridOpt.gridy = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedOpponentTakenPiecesLayoutTableRow :
                CHPlayGameLayoutConstants.OpponentTakenPiecesLayoutTableRow;
        gridOpt.gridx = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedOpponentTakenPiecesLayoutTableCol :
                CHPlayGameLayoutConstants.OpponentTakenPiecesLayoutTableCol;
        gridOpt.weighty = 1;
        gridOpt.weightx = 3;
        gridOpt.gridwidth = 3;
        gridOpt.gridheight = 1;

        mainPanel.add(opponentTakenPanel, gridOpt);

//        gridOpt.gridx = 0;
//        gridOpt.gridy = 4;
        gridOpt.gridy = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedPlayerTakenPiecesLayoutTableRow :
                CHPlayGameLayoutConstants.PlayerTakenPiecesLayoutTableRow;
        gridOpt.gridx = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedPlayerTakenPiecesLayoutTableCol :
                CHPlayGameLayoutConstants.PlayerTakenPiecesLayoutTableCol;
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

        gridOpt.gridy = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedPlayerNameLayoutTableRow :
                CHPlayGameLayoutConstants.PlayerNameLayoutTableRow;
        gridOpt.gridx = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedPlayerNameLayoutTableCol :
                CHPlayGameLayoutConstants.PlayerNameLayoutTableCol;
        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        mainPanel.add(playerPanel, gridOpt);

//        gridOpt.gridy = 3;
//        gridOpt.gridx = 1;
        gridOpt.gridy = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedOpponentNameLayoutTableRow :
                CHPlayGameLayoutConstants.OpponentNameLayoutTableRow;
        gridOpt.gridx = this.isBoardReversed ?
                CHPlayGameLayoutConstants.ReversedOpponentNameLayoutTableCol :
                CHPlayGameLayoutConstants.OpponentNameLayoutTableCol;
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
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
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
        this.setIsBoardReversed(!this.isBoardReversed);
    }
}

