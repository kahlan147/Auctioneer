package AuctionOwner.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionOwner.Frontend.AuctionOwnerController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionOwner {
    private AuctionOwnerController auctionOwnerController;

    private ObservableList<AuctionRoom> auctionRooms;
    private AuctionRoom selectedAuctionRoom;

    private AuctionOwnerGateway auctionOwnerGateway;
    private int currentTime;

    public AuctionOwner(AuctionOwnerController auctionOwnerController){
        this.auctionOwnerController = auctionOwnerController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionOwnerController.SetObservableList(auctionRooms);
        auctionOwnerGateway = new AuctionOwnerGateway(this);
        currentTime = 0;
    }

    public void createNewAuctionRoom(String name){
        auctionOwnerGateway.RPC_RequestNewAuctionRoom(name);
    }

    public void addNewAuctionRoom(AuctionRoom auctionRoom){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionRooms.add(auctionRoom);
            }
        });
    }

    public void addToAuction(String name, double startPrice){
        if(selectedAuctionRoom == null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an auction room first");
            alert.show();
            return;
        }
        Auction auction = new Auction(name);
        auction.setAuctionDuration(50);
        auction.setAuctionRoomId(selectedAuctionRoom.getId());
        auction.newBid(startPrice, "StartPrice");
        auctionOwnerGateway.AddToAuction(auction);
        selectAuctionRoom(selectedAuctionRoom);
    }

    public void selectAuctionRoom(AuctionRoom auctionRoom){
        selectedAuctionRoom = auctionRoom;
        Auction currentAuction = selectedAuctionRoom.getCurrentAuction();
        if(currentAuction != null){
            auctionOwnerController.showAuctionData(currentAuction);
        }
        else{
            auctionOwnerController.showAuctionData(null);
        }
    }

    public void timePassed(int newTime){
        this.currentTime = newTime;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(auctionRooms.size() > 0) {
                    for (AuctionRoom auctionRoom : auctionRooms) {
                        if (auctionRoom.getCurrentAuction() != null) {
                            auctionRoom.getCurrentAuction().timePassed(currentTime);
                        }
                    }
                }
            }
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionOwnerController.timePassed();
            }
        });
    }

    public void addAuctionTo(Auction auction){
        AuctionRoom auctionRoom = findAuctionRoom(auction.getAuctionRoomId());
        auctionRoom.newAuction(auction);
    }

    private AuctionRoom findAuctionRoom(String auctionRoomId){
        return auctionRooms.filtered(o -> o.getId().equals(auctionRoomId)).get(0);
    }

}
