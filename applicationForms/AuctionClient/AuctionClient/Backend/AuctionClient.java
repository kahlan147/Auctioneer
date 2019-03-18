package AuctionClient.Backend;

import AuctionClient.Frontend.AuctionClient.AuctionClientController;
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

    public AuctionClient(AuctionClientController auctionClientController){
        this.auctionClientController = auctionClientController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        this.auctionClientController.SetObservableList(auctionRooms);
        auctionClientGateway = new AuctionClientGateway(this);
    }

    public void GetAuctionRoomsFromBroker(){
        auctionClientGateway.requestAuctionRooms();
    }

    public void AddAuctionRooms(List<AuctionRoom> newAuctionRooms){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionRooms.clear();
                auctionRooms.addAll(newAuctionRooms);
            }
        });
    }
}
