package AuctionOwner.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionOwner.Frontend.AuctionOwnerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import messaging.MessagePublisher;

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionOwner {
    private AuctionOwnerController auctionOwnerController;

    private ObservableList<AuctionRoom> auctionRooms;
    private AuctionRoom selectedAuctionRoom;

    private MessagePublisher messagePublisher;

    public AuctionOwner(AuctionOwnerController auctionOwnerController){
        this.auctionOwnerController = auctionOwnerController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionOwnerController.SetObservableList(auctionRooms);
        messagePublisher = new MessagePublisher("Owner");
    }

    public void CreateNewAuctionRoom(String text){
        auctionRooms.add(new AuctionRoom(text));
    }

    public void AddToAuction(String name, double startPrice){
        if(selectedAuctionRoom == null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an auction room first");
            alert.show();
            return;
        }
        Auction auction = new Auction(name);
        auction.newBid(startPrice, "StartPrice");
        selectedAuctionRoom.NewAuction(auction);
        SelectAuctionRoom(selectedAuctionRoom);
        messagePublisher.SendMessage(name);
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
