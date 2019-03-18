package AuctionClient.Backend;

import AuctionClient.Frontend.AuctionClient.AuctionClientController;
import Classes.AuctionRoom;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Verheijen on 12/03/2019.
 */
public class AuctionClient {

    private AuctionClientController auctionClientController;
    private ObservableList<AuctionRoom> auctionRooms;

    private AuctionClientGateway auctionClientGateway;
    private List<AuctionClientRoom> auctionClientRooms;

    public AuctionClient(AuctionClientController auctionClientController){
        this.auctionClientController = auctionClientController;
        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        this.auctionClientController.setObservableList(auctionRooms);
        auctionClientGateway = new AuctionClientGateway(this);
        auctionClientRooms = new ArrayList<>();
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

    public void connectTo(AuctionRoom auctionRoom){
        createAuctionClientRoom(auctionRoom);
    }

    private void createAuctionClientRoom(AuctionRoom auctionRoom){
        AuctionClientRoom auctionClientRoom = new AuctionClientRoom(auctionRoom, this);
        auctionClientRooms.add(auctionClientRoom);
    }

    public void disconnect(AuctionClientRoom auctionClientRoom){
        auctionClientRooms.remove(auctionClientRoom);
    }

}
