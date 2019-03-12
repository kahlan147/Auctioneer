package AuctionClient.Backend;

import AuctionClient.Frontend.AuctionClient.AuctionClientController;
import Classes.AuctionRoom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Niels Verheijen on 12/03/2019.
 */
public class AuctionClient {

    private AuctionClientController auctionClientController;
    private ObservableList<AuctionRoom> auctionRooms;

    public AuctionClient(AuctionClientController auctionClientController){
        this.auctionClientController = auctionClientController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        this.auctionClientController.SetObservableList(auctionRooms);
        GetAuctionRoomsFromBroker();
    }

    public void GetAuctionRoomsFromBroker(){

    }
}
