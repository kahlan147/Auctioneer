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

    public AuctionOwner(AuctionOwnerController auctionOwnerController){
        this.auctionOwnerController = auctionOwnerController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionOwnerController.SetObservableList(auctionRooms);
        auctionOwnerGateway = new AuctionOwnerGateway(this);
    }

    public void CreateNewAuctionRoom(String name){
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

    public void AddToAuction(String name, double startPrice){
        if(selectedAuctionRoom == null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an auction room first");
            alert.show();
            return;
        }
        Auction auction = new Auction(name);
        auction.setAuctionRoomId(selectedAuctionRoom.getSubscribeChannel());
        auction.newBid(startPrice, "StartPrice");
        selectedAuctionRoom.NewAuction(auction);
        auctionOwnerGateway.AddToAuction(auction);
        SelectAuctionRoom(selectedAuctionRoom);
    }

    public void SelectAuctionRoom(AuctionRoom auctionRoom){
        selectedAuctionRoom = auctionRoom;
        Auction currentAuction = selectedAuctionRoom.getCurrentAuction();
        if(currentAuction != null){
            auctionOwnerController.showAuctionData(currentAuction.getName(), Double.toString(currentAuction.getHighestBid()), currentAuction.getNameHighestBidder());
        }
        else{
            auctionOwnerController.showAuctionData("","","");
        }
    }

}
