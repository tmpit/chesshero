package Client.Game;

import Client.Game.ChessPieces.ChessPiece;

import java.awt.print.PrinterAbortException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class GamePlayer {

    //Fields
    //public int playerID = 0;
    private ChessColor playerColor = null;
    private List<ChessPiece> activePieces = null;
    private List<ChessPiece> takenPieces = null;

    private NetworkPlayer connectedPlayer = null;

    //Properties

    public boolean isConnected(){
        return connectedPlayer == null;
    }

    public NetworkPlayer getConnectedPlayer() {
        return connectedPlayer;
    }

    public void setConnectedPlayer(NetworkPlayer connectedPlayer) {
        this.connectedPlayer = connectedPlayer;
    }

    public void addActivePiece(ChessPiece newPiece){
        //newPiece.isTaken = false;
        //newPiece.owner = this;
        this.activePieces.add(newPiece);
    }

    public void addActivePiece(ChessPiece newPiece, BoardPosition newPosition){
        //newPiece.isTaken = false;
        //newPiece.owner = this;
        //newPiece.occupiedPosition = newPosition;
        this.activePieces.add(newPiece);
    }

    public void addTakenPiece(ChessPiece newPiece){
        //newPiece.isTaken = true;
        //newPiece.owner = this;
        //newPiece.occupiedPosition = null;
        this.takenPieces.add(newPiece);
    }

    public List<ChessPiece> getActivePieces() {
        return activePieces;
    }

//    public void setActivePieces(List<ChessPiece> activePieces) {
//        if (activePieces != null){
//            if(this.activePieces == null){
//                this.activePieces = new ArrayList<ChessPiece>();
//            }
//            for(int i = 0; i < activePieces.size(); i++){
//                ChessPiece currentPiece = activePieces.get(i);
//                //currentPiece.owner = this;
//                //currentPiece.isTaken = false;
//                this.addActivePiece(currentPiece);
//            }
//        }

        //this.activePieces = activePieces;
//    }

    public List<ChessPiece> getTakenPieces() {
        return takenPieces;
    }

//    public void setTakenPieces(List<ChessPiece> takenPieces) {
//        if (takenPieces != null){
//            if (this.takenPieces != null){
//                this.takenPieces = new ArrayList<ChessPiece>();
//            }
//            for(int i = 0; i < takenPieces.size(); i++){
//                ChessPiece currentPiece = takenPieces.get(i);
//                this.addTakenPiece(currentPiece);
////                currentPiece.owner.playerColor = this.playerColor;
////                currentPiece.isTaken = true;
////                currentPiece.occupiedPosition = null;
//            }
//        }

        //this.takenPieces = takenPieces;
    //}

    public ChessColor getPlayerColor() {
        return playerColor;
    }

    private void setPlayerColor(ChessColor playerColor) {
        this.playerColor = playerColor;
    }

    //Constructors

    public GamePlayer(ChessColor playerColor){
        this.playerColor = playerColor;
        activePieces = new ArrayList<ChessPiece>();
        takenPieces = new ArrayList<ChessPiece>();
    }
    public GamePlayer(ChessColor playerColor, NetworkPlayer connectedPlayer){
        this.playerColor = playerColor;
        activePieces = new ArrayList<ChessPiece>();
        takenPieces = new ArrayList<ChessPiece>();
        this.setConnectedPlayer(connectedPlayer);
    }

//    public GamePlayer(
//            ChessColor playerColor,
//            ArrayList<ChessPiece> activePieces,
//            ArrayList<ChessPiece> takenPieces){
//        //this.playerID = playerID;
//        this.setPlayerColor(playerColor);
//        this.setActivePieces(activePieces);
//        this.setTakenPieces(takenPieces);
//
//    }

}
