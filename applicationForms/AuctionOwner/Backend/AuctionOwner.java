package Backend;

import Classes.AuctionRoom;
import Frontend.AuctionOwnerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionOwner {
    private AuctionOwnerController auctionOwnerController;

    private ObservableList<AuctionRoom> auctionRooms;

    public AuctionOwner(AuctionOwnerController auctionOwnerController){
        this.auctionOwnerController = auctionOwnerController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        //auctionOwnerController.SetObservableList(auctionRooms);
    }

    public ObservableList<AuctionRoom> getAuctionRooms(){
        return auctionRooms;
    }

    public void CreateNewAuctionRoom(String text){
        auctionRooms.add(new AuctionRoom(text));
    }

    public void AddToAuction(String name, double startPrice){

    }

}
