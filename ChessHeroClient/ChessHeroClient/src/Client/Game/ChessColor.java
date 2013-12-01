package Client.Game;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ChessColor {
    White, Black;
    public ChessColor Opposite;

    static  {
        White.Opposite = Black;
        Black.Opposite = White;
    }
}