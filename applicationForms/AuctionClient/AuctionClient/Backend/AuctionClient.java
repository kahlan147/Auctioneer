package AuctionClient.Backend;

import AuctionClient.Frontend.AuctionClientController;
import Classes.Auction;
import Classes.AuctionRoom;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by Niels Verheijen on 12/03/2019.
 */
public class AuctionClient {

    private AuctionClientController auctionClientController;
    private ObservableList<AuctionRoom> auctionRooms;

    private AuctionClientGateway auctionClientGateway;
    private AuctionRoom connectedAuctionRoom;

    private String username;
    private int currentTime;

    public AuctionClient(AuctionClientController auctionClientController){
        this.auctionClientController = auctionClientController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        this.auctionClientController.setObservableList(auctionRooms);
        auctionClientGateway = new AuctionClientGateway(this);
        currentTime = 0;
    }

    public void getAuctionRoomsFromBroker(){
        auctionClientGateway.requestAuctionRooms();
    }

    public void addAuctionRooms(List<AuctionRoom> newAuctionRooms){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionRooms.clear();
                auctionRooms.addAll(newAuctionRooms);
            }
        });
    }

    public void timePassed(int newTime){
        this.currentTime = newTime;
        if(connectedAuctionRoom != null){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Auction auction = connectedAuctionRoom.getCurrentAuction();
                    if(auction != null) {
                        auction.timePassed(currentTime);
                        auctionClientController.timePassed();
                    }
                }
            });
        }
    }

    public void setUserName(String username){
        this.username = username;
    }

    public void connectTo(AuctionRoom auctionRoom){
        this.connectedAuctionRoom = auctionRoom;
        auctionClientGateway.connectToAuctionRoom(auctionRoom);
        auctionClientController.connected();
    }

    public void disconnect(){
        this.connectedAuctionRoom = null;
        auctionClientGateway.disconnectFromAuctionRoom();
    }

    public void newAuctionReceived(Auction auction){
        if(connectedAuctionRoom != null){
            connectedAuctionRoom.overrideCurrentAuction(auction);
            auction.timePassed(currentTime);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    auctionClientController.showAuction(connectedAuctionRoom.getCurrentAuction());
                }
            });
        }
    }

    public void bid(double price){
        Auction auction = connectedAuctionRoom.getCurrentAuction();
        if(!auction.newBid(price, username)){
            return;
        }
        auctionClientGateway.placeBid(connectedAuctionRoom.getClientReplyChannel(), auction);
    }

}
