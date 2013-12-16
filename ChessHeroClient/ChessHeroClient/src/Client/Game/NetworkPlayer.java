package Client.Game;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkPlayer {
    public int playerID = 0;

    private GamePlayer gamePlayer;

    public GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public NetworkPlayer(){};

    public NetworkPlayer(int playerID){
        this.playerID = playerID;
    };

    public NetworkPlayer(int playerID, GamePlayer gamePlayer){
        this.playerID = playerID;
        this.gamePlayer = gamePlayer;
    };
}
