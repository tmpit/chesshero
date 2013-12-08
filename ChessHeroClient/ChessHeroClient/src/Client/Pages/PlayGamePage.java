package Client.Pages;

import Client.Game.*;
import Client.Game.ChessPieces.ChessPiece;
import Client.Game.ChessPieces.ChessPieceType;
import Client.Pages.PlayGameVisualization.ChessBoardPanel;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Struct;
import java.util.Dictionary;
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

    public static Map<Pair<ChessPieceType, ChessColor>, BufferedImage> ChessPieceImages =
            new HashMap<Pair<ChessPieceType, ChessColor>,BufferedImage>();
    public Game game = null;

    public int PIECE_SIZE = 50;

    public boolean isBoardReversed = false;
    //public GameController(Game game, NetworkPlayer me, NetworkPlayer opponent){

    public PlayGamePage(){
        super();

        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.game = new Game();
        this.game.blackPlayer = new GamePlayer(ChessColor.Black);
        this.game.whitePlayer = new GamePlayer(ChessColor.White);
        this.game.startNewGame(this.game.getWhitePlayer(), this.game.getBlackPlayer());
        JPanel mainPanel = new JPanel();
        JPanel chatPanel = new JPanel();
        JPanel logPanel = new JPanel();
        JPanel playerPanel = new JPanel();
        JPanel opponentPanel = new JPanel();
        JPanel playerTakenPanel = new JPanel();
        JPanel opponentTakenPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        //mainPanel.setBackground(new Color(255,0,0));
        chatPanel.setBackground(new Color(255,0,0));
        logPanel.setBackground(new Color(0,255,0));
        playerPanel.setBackground(new Color(0,0,255));
        opponentPanel.setBackground(new Color(255,255,0));
        playerTakenPanel.setBackground(new Color(255,0,255));
        opponentTakenPanel.setBackground(new Color(0,255,255));

        ChessBoardPanel chessBoardPanel = new ChessBoardPanel(this.game, PIECE_SIZE);
        //chessBoardPanel.setBackground(new Color(255,0,0));

        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.BOTH;
        //gridOpt.insets = new Insets(20,200,20,200);
        gridOpt.gridy = 2;
        gridOpt.gridx = 1;
        //gridOpt.gridheight = 1;
        //gridOpt.ipady = 20;
        //gridOpt.ipadx = 100;
        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        //mainPanel.add(pageTitle,gridOpt);


        //JPanel menuPanel = new JPanel();

        mainPanel.add(chessBoardPanel, gridOpt);

        gridOpt.fill = GridBagConstraints.HORIZONTAL;

        gridOpt.gridy = 0;
        gridOpt.gridx = 0;
        gridOpt.weighty = 1;
        gridOpt.weightx = 3;
        gridOpt.gridwidth = 3;
        gridOpt.gridheight = 1;

        mainPanel.add(opponentPanel, gridOpt);

        gridOpt.gridy = 4;
        gridOpt.gridx = 0;
        gridOpt.weighty = 1;
        gridOpt.weightx = 3;
        gridOpt.gridwidth = 3;
        gridOpt.gridheight = 1;
        mainPanel.add(playerPanel, gridOpt);

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

        gridOpt.gridy = 1;
        gridOpt.gridx = 1;
        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        mainPanel.add(playerTakenPanel, gridOpt);

        gridOpt.gridy = 3;
        gridOpt.gridx = 1;
        gridOpt.weighty = 1;
        gridOpt.weightx = 1;
        gridOpt.gridwidth = 1;
        gridOpt.gridheight = 1;
        mainPanel.add(opponentTakenPanel, gridOpt);


        this.setPagePanel(mainPanel);
        mainPanel.setVisible(true);

        //mainPanel.setLayout(new GridBagLayout());
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
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Pawn,ChessColor.Black),resizeImage(BlackPawn,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.King,ChessColor.Black),resizeImage(BlackKing,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Knight,ChessColor.Black),resizeImage(BlackKnight,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Queen,ChessColor.Black),resizeImage(BlackQueen,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Rook,ChessColor.Black),resizeImage(BlackRook,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Bishop,ChessColor.Black),resizeImage(BlackBishop,this.PIECE_SIZE));

        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Pawn,ChessColor.White),resizeImage(WhitePawn,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.King,ChessColor.White),resizeImage(WhiteKing,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Knight,ChessColor.White),resizeImage(WhiteKnight,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Queen,ChessColor.White),resizeImage(WhiteQueen,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Rook,ChessColor.White),resizeImage(WhiteRook,this.PIECE_SIZE));
        ChessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Bishop,ChessColor.White),resizeImage(WhiteBishop,this.PIECE_SIZE));

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
}

