package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionBroker.Frontend.AuctionBrokerController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Niels Verheijen on 11/03/2019.
 */
public class AuctionBroker {

    private AuctionBrokerController auctionBrokerController;

    private ObservableList<AuctionRoom> auctionRooms;

    private BrokerToOwnerGateway brokerToOwnerGateway;
    private BrokerToClientGateway brokerToClientGateway;
    private BrokerToTimeServerGateway brokerToTimeServerGateway;

    private int currentTime;

    public AuctionBroker(AuctionBrokerController auctionBrokerController){
        this.auctionBrokerController = auctionBrokerController;

        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionBrokerController.SetObservableList(auctionRooms);

        brokerToOwnerGateway = new BrokerToOwnerGateway(this);
        brokerToClientGateway = new BrokerToClientGateway(this);
        brokerToTimeServerGateway = new BrokerToTimeServerGateway(this);

    }

    private void TESTING(){
        AuctionRoom auctionRoom = createAuctionRoom("TestingRoom");
        Auction auction = new Auction("auctionTest");
        auction.newBid(0.2d, "test bidder");
        auction.setAuctionRoomId(auctionRoom.getId());
        auction.setAuctionStartTime(0);
        auction.setAuctionDuration(30);
        Auction auction1 = new Auction("auctionTest1");
        auction1.newBid(0.5d, "test bidder 2");
        auction1.setAuctionRoomId(auctionRoom.getId());
        auctionRoom.newAuction(auction);
        auctionRoom.newAuction(auction1);
        auction1.setAuctionStartTime(30);
        auction1.setAuctionDuration(50);
    }

    public void timePassed(int newTime){
        currentTime = newTime;
        if(auctionRooms.size() > 0){
            for(AuctionRoom auctionRoom : auctionRooms){
                if(auctionRoom.timePassed(newTime)){
                    if(auctionRoom.getCurrentAuction() != null) {
                        auctionRoom.getCurrentAuction().setAuctionStartTime(this.currentTime);
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            auctionBrokerController.anAuctionHasFinished(auctionRoom);
                        }
                    });
                    brokerToClientGateway.publishNewAuction(auctionRoom);
                    brokerToOwnerGateway.publishNewAuction(auctionRoom);
                }
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionBrokerController.timePassed();
            }
        });
    }

    public void SelectAuctionRoom(AuctionRoom auctionRoom){
        Auction currentAuction = auctionRoom.getCurrentAuction();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionBrokerController.showAuctionData(currentAuction);
            }
        });
    }

    public AuctionRoom createAuctionRoom(String name){
        AuctionRoom auctionRoom = new AuctionRoom(name);
        String id = UUID.randomUUID().toString();
        String subscribeChannel = UUID.randomUUID().toString();
        String clientReplyChannel = UUID.randomUUID().toString();
        String ownerReplyChannel = UUID.randomUUID().toString();
        auctionRoom.setId(id);
        auctionRoom.setChannels(subscribeChannel, clientReplyChannel, ownerReplyChannel);
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

    private AuctionRoom findAuctionRoom(String auctionRoomId){
        return auctionRooms.filtered(o -> o.getId().equals(auctionRoomId)).get(0);
    }

    public void addAuction(Auction auction){
        AuctionRoom auctionRoom = findAuctionRoom(auction.getAuctionRoomId());
        boolean firstAuction = auctionRoom.newAuction(auction);
        if(firstAuction){
            auction.setAuctionStartTime(this.currentTime);
            brokerToOwnerGateway.publishNewAuction(auctionRoom);
        }
    }


}
