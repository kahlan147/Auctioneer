package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionBroker.Frontend.AuctionBrokerController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private BrokerToTimeServerGateway brokerToTimeServerGateway;

    private AuctionRoom selectedAuctionRoom;
    private int currentTime;

    public AuctionBroker(AuctionBrokerController auctionBrokerController){
        this.auctionBrokerController = auctionBrokerController;

        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionBrokerController.SetObservableList(auctionRooms);

        brokerToOwnerGateway = new BrokerToOwnerGateway(this);
        brokerToClientGateway = new BrokerToClientGateway(this);
        brokerToTimeServerGateway = new BrokerToTimeServerGateway(this);
    }

    /**
     * Handles everything that concerning updating the current time
     * @param newTime the new time.
     */
    public void timePassed(int newTime){
        currentTime = newTime;
        if(auctionRooms.size() > 0){
            for(AuctionRoom auctionRoom : auctionRooms){
                //foreach room, check if an auction has finished.
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
        this.selectedAuctionRoom = auctionRoom;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionBrokerController.showAuctionData(selectedAuctionRoom.getCurrentAuction());
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

    public Auction getAuctionFor(String auctionRoomId){
        return findAuctionRoom(auctionRoomId).getCurrentAuction();
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
            auctionUpdated(auctionRoom);
            auction.setAuctionStartTime(this.currentTime);
            brokerToClientGateway.publishNewAuction(auctionRoom);
            brokerToOwnerGateway.publishNewAuction(auctionRoom);
        }
    }

    private void auctionUpdated(AuctionRoom auctionRoom){
        if(selectedAuctionRoom.getId().equals(auctionRoom.getId())){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    auctionBrokerController.showAuctionData(selectedAuctionRoom.getCurrentAuction());
                }
            });

        }
    }

    public void bidReceived(Auction auction){
        AuctionRoom auctionRoom = findAuctionRoom(auction.getAuctionRoomId());
        if(auctionRoom.getCurrentAuction().getName().equals(auction.getName())){
            auctionRoom.getCurrentAuction().newBid(auction.getHighestBid(), auction.getNameHighestBidder());
            auctionUpdated(auctionRoom);
            brokerToClientGateway.publishNewAuction(auctionRoom);
            brokerToOwnerGateway.publishNewAuction(auctionRoom);
        }
    }


}
