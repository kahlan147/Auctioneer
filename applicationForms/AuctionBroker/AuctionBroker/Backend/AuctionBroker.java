package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionBroker.Frontend.AuctionBrokerController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import messaging.PublishSubscribe.MessageSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Niels Verheijen on 11/03/2019.
 */
public class AuctionBroker {

    private AuctionBrokerController auctionBrokerController;

    private ObservableList<AuctionRoom> auctionRooms;

    private BrokerToOwnerGateway brokerToOwnerGateway;
    private BrokerToClientGateway brokerToClientGateway;

    public AuctionBroker(AuctionBrokerController auctionBrokerController){
        this.auctionBrokerController = auctionBrokerController;

        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionBrokerController.SetObservableList(auctionRooms);

        brokerToOwnerGateway = new BrokerToOwnerGateway(this);
        brokerToClientGateway = new BrokerToClientGateway(this);
    }

    public void SelectAuctionRoom(AuctionRoom auctionRoom){
        AuctionRoom selectedAuctionRoom = auctionRoom;
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
        String subscribeChannel = UUID.randomUUID().toString();
        String replyChannel = UUID.randomUUID().toString();
        auctionRoom.setChannels(subscribeChannel, replyChannel);
        brokerToClientGateway.addPublisherToAuctionRoom(auctionRoom);
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

    private AuctionRoom findAuctionRoom(String subscriberChannel){
        return auctionRooms.filtered(o -> o.getSubscribeChannel().equals(subscriberChannel)).get(0);
    }

    public void addAuction(Auction auction){
        findAuctionRoom(auction.getAuctionRoomId()).NewAuction(auction);
    }


}
