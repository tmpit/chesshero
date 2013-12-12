package Client.Game;

import java.util.zip.CheckedInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameController {

    public NetworkPlayer player = null;
    public NetworkPlayer opponent = null;
    public Game game = null;

//    public GameController(){
//        this.game = new Game();
//        //this.game.board = Game.generateEmptyBoard();
//
//    }

    public GameController(Game game, NetworkPlayer player, NetworkPlayer opponent){

        this.game = game;
        this.player = player;
        this.opponent = opponent;
    }

    public GameController(Game game,
                          NetworkPlayer player,
                          ChessColor playerColor,
                          NetworkPlayer opponent,
                          ChessColor opponentColor){
        this(game,player,opponent);

        connectPlayers(player,playerColor,opponent,opponentColor);
    }
//    public GamePlayer createGamePlayer(ChessColor playerColor){
//        new GamePlayer(playerColor);
//    }

    public void connectPlayers(NetworkPlayer player,
                               ChessColor playerColor,
                               NetworkPlayer opponent,
                               ChessColor opponentColor){
        connectPlayer(player,playerColor);
        connectPlayer(opponent,opponentColor);
    }

    public void connectPlayer(NetworkPlayer networkPlayer, ChessColor asGamePlayer){
        if(this.game != null && networkPlayer != null){
            if (asGamePlayer == ChessColor.White){
                if(this.game.getWhitePlayer() != null){
                    this.game.getWhitePlayer().setConnectedPlayer(networkPlayer);
                }
                else{
                    this.game.setWhitePlayer(new GamePlayer(asGamePlayer,networkPlayer));
                }
                networkPlayer.setGamePlayer(this.game.getWhitePlayer());
            }
            else if (asGamePlayer == ChessColor.Black){
                if(this.game.getBlackPlayer() != null){
                    this.game.getBlackPlayer().setConnectedPlayer(networkPlayer);
                }
                else{
                    this.game.setBlackPlayer(new GamePlayer(asGamePlayer,networkPlayer));
                }
                networkPlayer.setGamePlayer(this.game.getBlackPlayer());
            }
            else{
                //Should throw exception
                return;
            }
        }
    }
}
//    private static GameController singleton = null;
//    private GameController(){};

//    private GameController(){};

//    public synchronized GameController getSingleton(){
//        if (singleton == null){
//            singleton = new GameController();
//        }
//        return singleton;
//    }