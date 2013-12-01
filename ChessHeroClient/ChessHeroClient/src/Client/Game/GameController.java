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

    public NetworkPlayer me = null;
    public NetworkPlayer opponent = null;
    public Game game = null;

//    public GameController(){
//        this.game = new Game();
//        //this.game.board = Game.generateEmptyBoard();
//
//    }

    public GameController(Game game, NetworkPlayer me, NetworkPlayer opponent){

        this.game = game;
        this.me = me;
        this.opponent = opponent;
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