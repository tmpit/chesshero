package Client.Pages;

import Client.Game.*;
import Client.Game.ChessPieces.ChessPiece;
import Client.Game.ChessPieces.ChessPieceType;
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

    public Map<Pair<ChessPieceType, ChessColor>, BufferedImage> chessPieceImages =
            new HashMap<Pair<ChessPieceType, ChessColor>,BufferedImage>();
    public Game game = null;

    public int intPieceSize = 70;
    public int pieceWidth = 70;
    public int pieceHeight = 70;

    public boolean isBoardReversed = false;
    //public GameController(Game game, NetworkPlayer me, NetworkPlayer opponent){

    public PlayGamePage(){
        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.game = new Game();
        this.game.blackPlayer = new GamePlayer(ChessColor.Black);
        this.game.whitePlayer = new GamePlayer(ChessColor.White);
        this.game.startNewGame(this.game.getWhitePlayer(), this.game.getBlackPlayer());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,1));

//        GridBagConstraints gridOpt = new GridBagConstraints();
//        gridOpt.fill = GridBagConstraints.BOTH;
//        gridOpt.insets = new Insets(20,200,20,200);
//        gridOpt.gridx = 0;
//        gridOpt.gridy = 0;
//        //gridOpt.gridheight = 1;
//        //gridOpt.ipady = 20;
//        //gridOpt.ipadx = 100;
//        gridOpt.weightx = 1;
//        gridOpt.weighty = 0;
//        mainPanel.add(pageTitle,gridOpt);


        //JPanel menuPanel = new JPanel();
        ChessBoardPanel chessBoardPanel = new ChessBoardPanel(this.game);

        mainPanel.add(chessBoardPanel);
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
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Pawn,ChessColor.Black),resizeImage(BlackPawn,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.King,ChessColor.Black),resizeImage(BlackKing,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Knight,ChessColor.Black),resizeImage(BlackKnight,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Queen,ChessColor.Black),resizeImage(BlackQueen,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Rook,ChessColor.Black),resizeImage(BlackRook,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Bishop,ChessColor.Black),resizeImage(BlackBishop,this.intPieceSize));

        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Pawn,ChessColor.White),resizeImage(WhitePawn,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.King,ChessColor.White),resizeImage(WhiteKing,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Knight,ChessColor.White),resizeImage(WhiteKnight,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Queen,ChessColor.White),resizeImage(WhiteQueen,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Rook,ChessColor.White),resizeImage(WhiteRook,this.intPieceSize));
        this.chessPieceImages.put(new Pair<ChessPieceType, ChessColor>(ChessPieceType.Bishop,ChessColor.White),resizeImage(WhiteBishop,this.intPieceSize));

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

    class ChessBoardPanel extends JPanel{

        ChessBoardFieldPanel [][] chessBoardFields  = new ChessBoardFieldPanel[8][8];
        String[] colLabels = new String[]{"A","B","C","D","E","F","G","H"};
        String[] rowLabels = new String[]{"1","2","3","4","5","6","7","8"};

        public ChessBoardPanel(Game game){

            for (int i =0; i<game.board.length;i++){
                for (int j =0; j<game.board[i].length;j++){
                    ChessBoardFieldPanel currentGameBoardFieldPanel = this.chessBoardFields[i][j];
                    BoardField currentGameBoardField = game.board[i][j];
                    BoardPosition currFieldPos = currentGameBoardField.getFieldPosition();
                    ChessPiece currOccupyingPiece = currentGameBoardField.getOccupyingPiece();

                    if (currOccupyingPiece != null){
                        this.chessBoardFields[currFieldPos.row][currFieldPos.col] =
                                new ChessBoardFieldPanel(
                                        currentGameBoardField.getFieldColor(),
                                        currFieldPos,
                                        chessPieceImages.get(new Pair<ChessPieceType, ChessColor> (
                                                currOccupyingPiece.type, currOccupyingPiece.owner.getPlayerColor()
                                        )));
                    }
                    else{
                        this.chessBoardFields[currFieldPos.row][currFieldPos.col] =
                                new ChessBoardFieldPanel(
                                        currentGameBoardField.getFieldColor(),
                                        currFieldPos
                                        );
                    }
                    //mainPanel.add(this.chessBoardFields[i][j]);
//                    GridOpt.gridx = j;
//                    GridOpt.gridy = i;
//                    this.add(this.chessBoardFields[i][j],GridOpt);
                    //chessBoardFields[i][j] = currentGameBoardField;
                }

            }

            boolean isReversed = false;
            this.redrawBoard(isReversed);
        }

        private void redrawBoard(boolean reversed) {
            clearBoard();
            if (reversed == true){
                drawBoardTopDown();
            }
            else {
                drawBoardDownTop();
            }
        }

        private void drawBoardDownTop() {
            //GridBagConstraints GridOpt = new GridBagConstraints();

            this.setLayout(new GridLayout(10,10));

            for(int row = 8; row >= -1; row--){
                for (int col = -1; col <= 8; col++){
                    if ((row == -1 || row == 8) || (col == -1 || col == 8)){
                        JLabel label = new JLabel();
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        if ((col == -1 || col == 8) && (row != -1 && row != 8)){
                            label.setText(rowLabels[row]);
                            this.add(label);
                        }
                        else if ((row == -1 || row == 8) && (col != -1 && col != 8)){
                            label.setText(colLabels[col]);
                            this.add(label);
                        }
                        else {
                            this.add(label);
                        }
                    }
                    else {
                        this.add(this.chessBoardFields[row][col]);

                    }
                }
            }
        }


        private void drawBoardTopDown() {
            //GridBagConstraints GridOpt = new GridBagConstraints();
//            GridOpt.gridwidth = 50;
//            GridOpt.gridheight = 50;
//            GridOpt.weightx = 1;
//            GridOpt.weighty = 1;
//            GridOpt.fill = GridBagConstraints.RELATIVE;


            this.setLayout(new GridLayout(10,10));

            for(int row = -1; row <= 8; row++){
                for (int col = -1; col <= 8; col++){
//                    GridOpt.gridx = col;
//                    GridOpt.gridy = row;
                    //this.add(this.chessBoardFields[row][col],GridOpt);
                    if ((row == -1 || row == 8) || (col == -1 || col == 8)){
                        JLabel label = new JLabel();
                        label.setHorizontalTextPosition(SwingConstants.CENTER);

                        if ((col == -1 || col == 8) && (row != -1 && row != 8)){
                            label.setText(rowLabels[row]);
                            this.add(label);
                        }
                        else if ((row == -1 || row == 8) && (col != -1 && col != 8)){
                            label.setText(colLabels[col]);
                            this.add(label);
                        }
                        else {
                            this.add(label);
                        }
                    }
                    else {
                        this.add(this.chessBoardFields[row][col]);

                    }
                }
            }
            //
//                    this.add(this.chessBoardFields[i][j],GridOpt);
        }

        private void clearBoard() {
            this.removeAll();
        }
    }
    class ChessBoardFieldPanel extends JLabel{
        public ChessColor fieldColor = null;
        public BoardPosition fieldPosition = null;
        public BufferedImage fieldImage = null;

        public Color getDisplayColor(){
            return this.fieldColor == ChessColor.White ? new Color(230,198,167): new Color(90,45,45);
        }

        public ChessBoardFieldPanel(ChessColor fieldColor, BoardPosition fieldPosition){
            this(fieldColor, fieldPosition, null);
        }

        public ChessBoardFieldPanel(ChessColor fieldColor, BoardPosition fieldPosition, BufferedImage fieldImage){
            //super(new ImageIcon (fieldImage));
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setOpaque(true);
            if(fieldImage != null){
                this.setIcon(new ImageIcon(fieldImage));
            }
            else{
                this.setIcon(new ImageIcon());
            }
            this.fieldColor = fieldColor;
            this.fieldPosition = fieldPosition;
            this.fieldImage = fieldImage;
            this.setBackground(this.getDisplayColor());
            //this.setBounds(new Rectangle(0, 0, 50, 50));

        }
    }
}

//        @Override
//        public void paint(Graphics g) {
//            super.paint(g);
//            //super.paint(g);
//            //g.setColor(getBackground());
//            //g.se(0, 0, getWidth(), getHeight());
//        }
//
//        @Override
//        public void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            //this.getGraphics().setColor(setBackground(Color.black););
//
////            g.setColor(getBackground());
////            g.fillRect(0, 0, getWidth(), getHeight());
//        }


//
//        }
////        BufferedImage myPicture = ImageIO.read(new File("path-to-file"));
//        //JLabel picLabel = new JLabel(new ImageIcon(myPicture));
////        add(picLabel);
//    }
    //this.setPageTitle("Play Game Page");

    //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
    //mainPanel.setLayout(new BoxLayout(mainPanel, FlowLayout.CENTER));

//
//BufferedImage myPicture = ImageIO.read(new File("path-to-file"));
//JLabel picLabel = new JLabel(new ImageIcon(myPicture));
//    add(picLabel);
    //public JTextField usernameTextBox;
    //public JPasswordField passwordTextBox;

//    class ChessBoardField extends JComponent{
//        public int ZeroCol = 0;
//        public int A = 1;
//        public int B = 2;
//        public int C = 3;
//        public int D = 4;
//        public int E = 5;
//        public int F = 6;
//        public int G = 7;
//        public int H = 8;
//
//        public int row = 0;
//        public int col = 0;

        //public BufferedImage fieldImage = null;

//        public ChessPiece
//
//        ChessBoardField(int row, int col){
//            this.row
//
//        }
    //}


//    public  PlayGamePage{
//
//        class ChessBoard extends JPanel{
//
//
//            JFrame [][] chessFields  = new JFrame[8][8];
//
//        }
//        //this.setPageTitle("Play Game Page");
//        JPanel mainPanel = new JPanel();
//        JPanel menuPanel = new JPanel();
//        JPanel chessBoard = new JPanel();
//        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
//        //mainPanel.setLayout(new BoxLayout(mainPanel, FlowLayout.CENTER));
//        mainPanel.setLayout(new GridBagLayout());
//
//
//        menuPanel.setLayout(new GridLayout(6,1));
//
//        JLabel pageTitle = new JLabel(MAIN_TITLE);
//
//        pageTitle.setHorizontalAlignment(JLabel.CENTER);
//        pageTitle.setHorizontalTextPosition(JLabel.CENTER);
//        pageTitle.setFont(new Font("Serif", Font.BOLD, 48));
//
//        JLabel pageSubTitle = new JLabel(this.getPageTitle());
//
//        pageSubTitle.setHorizontalAlignment(JLabel.CENTER);
//        pageSubTitle.setHorizontalTextPosition(JLabel.CENTER);
//        pageSubTitle.setFont(new Font("Serif", Font.BOLD, 32));
//
//        JLabel usernameLabel = new JLabel("Username");
//        JLabel passwordLabel = new JLabel("Password");
//
//        this.usernameTextBox = new JTextField();
//        this.passwordTextBox = new JPasswordField();
//
//        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
//        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        passwordTextBox.setHorizontalAlignment(SwingConstants.CENTER);
//        usernameTextBox.setHorizontalAlignment(SwingConstants.CENTER);
//
//        JButton loginButton = new JButton("Login");
//        JButton registerButton = new JButton("Register");
//
//        //Add Components
//        menuPanel.add(usernameLabel);
//        menuPanel.add(usernameTextBox);
//
//        menuPanel.add(passwordLabel);
//        menuPanel.add(passwordTextBox);
//
//        new GridBagConstraints();
//        GridBagConstraints gridOpt = new GridBagConstraints();
//        gridOpt.fill = GridBagConstraints.BOTH;
//        gridOpt.insets = new Insets(20,200,20,200);
//        gridOpt.gridx = 0;
//        gridOpt.gridy = 0;
//        //gridOpt.gridheight = 1;
//        //gridOpt.ipady = 20;
//        //gridOpt.ipadx = 100;
//        gridOpt.weightx = 1;
//        gridOpt.weighty = 0;
//        mainPanel.add(pageTitle,gridOpt);
//
//        gridOpt.insets = new Insets(0,200,20,200);
//        gridOpt.gridy = 1;
//        //gridOpt.weighty = 0;
//        mainPanel.add(pageSubTitle, gridOpt);
//
//        //gridOpt.fill = GridBagConstraints.BOTH;
//        gridOpt.insets = new Insets(0,200,40,200);
//        //gridOpt.ipadx = 10;
//        gridOpt.gridx = 0;
//        gridOpt.gridy = 2;
//        //gridOpt.gridheight = 4;
//        //gridOpt.weightx = 1;
//        gridOpt.weighty = 6;
//        //gridOpt.fill = GridBagConstraints.HORIZONTAL;
//        mainPanel.add(menuPanel, gridOpt);
//
//        gridOpt.insets = new Insets(0,200,20,200);
//        gridOpt.gridy = 4;
//        gridOpt.weighty = 0.5;
//        mainPanel.add(loginButton, gridOpt);
//
//        gridOpt.gridy = 5;
//        gridOpt.weighty = 0.5;
//        mainPanel.add(registerButton, gridOpt);
//
//
//        this.setPagePanel(mainPanel);
//    }
//}
