package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionBroker.Frontend.AuctionBrokerController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import messaging.PublishSubscribe.MessageSubscriber;

import java.util.List;

/**
 * Created by Niels Verheijen on 11/03/2019.
 */
public class AuctionBroker {

    private AuctionBrokerController auctionBrokerController;

    private ObservableList<AuctionRoom> auctionRooms;
    private AuctionRoom selectedAuctionRoom;

    private BrokerToOwnerGateway brokerToOwnerGateway;
    private BrokerToClientGateway brokerToClientGateway;

    private MessageSubscriber messageSubscriber;

    public AuctionBroker(AuctionBrokerController auctionBrokerController){
        this.auctionBrokerController = auctionBrokerController;

        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionBrokerController.SetObservableList(auctionRooms);

        brokerToOwnerGateway = new BrokerToOwnerGateway(this);
        brokerToClientGateway = new BrokerToClientGateway(this);

        //messageSubscriber = new MessageSubscriber("Owner");
        //DEBUG_ADDROOMS();
    }

    private void DEBUG_ADDROOMS(){
        auctionRooms.add(new AuctionRoom("test1"));
        auctionRooms.add(new AuctionRoom("test2"));
        auctionRooms.add(new AuctionRoom("test3"));
        auctionRooms.add(new AuctionRoom("test4"));
        auctionRooms.add(new AuctionRoom("test5"));
        auctionRooms.add(new AuctionRoom("test6"));
        auctionRooms.add(new AuctionRoom("test7"));
        auctionRooms.add(new AuctionRoom("test8"));
    }

    public void SelectAuctionRoom(AuctionRoom auctionRoom){
        selectedAuctionRoom = auctionRoom;
        Auction currentAuction = selectedAuctionRoom.getCurrentAuction();
        if(currentAuction != null){
            auctionBrokerController.showAuctionData(currentAuction.getName(), Double.toString(currentAuction.getHighestBid()), currentAuction.getNameHighestBidder());
        }
        else{
            auctionBrokerController.showAuctionData("","","");
        }
    }

    public AuctionRoom createAuctionRoom(String name){
        AuctionRoom auctionRoom = new AuctionRoom(name);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionRooms.add(auctionRoom);
            }
        });
        return auctionRoom;
    }

    public List<AuctionRoom> getAuctionRooms(){
        return auctionRooms;
    }


}
