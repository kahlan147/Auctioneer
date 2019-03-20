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

    public AuctionClient(AuctionClientController auctionClientController){
        this.auctionClientController = auctionClientController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        this.auctionClientController.setObservableList(auctionRooms);
        auctionClientGateway = new AuctionClientGateway(this);
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

    public void timePassed(int seconds){
        if(connectedAuctionRoom != null){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Auction auction = connectedAuctionRoom.getCurrentAuction();
                    if(auction != null) {
                        auction.timePassed(seconds);
                        auctionClientController.timePassed(auction);
                    }
                }
            });
        }
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
            connectedAuctionRoom.newAuction(auction);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    auctionClientController.showAuction(connectedAuctionRoom.getCurrentAuction());
                }
            });
        }
    }

}
