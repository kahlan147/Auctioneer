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

    /**
     * Selects the given auctionroom and shows it's data on the GUI.
     * @param auctionRoom
     */
    public void SelectAuctionRoom(AuctionRoom auctionRoom){
        this.selectedAuctionRoom = auctionRoom;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                auctionBrokerController.showAuctionData(selectedAuctionRoom.getCurrentAuction());
            }
        });
    }

    /**
     * Creates an auctionroom and fits it with unique IDs.
     * Created in the broker so all IDs are always unique, unlike the small chance of 2
     * different applications creating the exact same ID.
     * @param name
     * @return
     */
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

    /**
     * returns the current auction belonging to the room of the corresponding given auctionroom id.
     * @param auctionRoomId
     * @return
     */
    public Auction getAuctionFor(String auctionRoomId){
        return findAuctionRoom(auctionRoomId).getCurrentAuction();
    }

    /**
     * returns all auction rooms currently on-line.
     * @return
     */
    public List<AuctionRoom> getAuctionRooms(){
        return auctionRooms;
    }

    /**
     * finds the auction room based on the given ID.
     * @param auctionRoomId
     * @return
     */
    private AuctionRoom findAuctionRoom(String auctionRoomId){
        return auctionRooms.filtered(o -> o.getId().equals(auctionRoomId)).get(0);
    }

    /**
     * Adds an auction to the auctionroom it has the ID for.
     * @param auction
     */
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

    /**
     * Called when an auction was updated;
     * 1. An auction has ended.
     * 2. A new auction was placed and no auction was displayed.
     * @param auctionRoom
     */
    private void auctionUpdated(AuctionRoom auctionRoom){
        if(selectedAuctionRoom != null && selectedAuctionRoom.getId().equals(auctionRoom.getId())){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    auctionBrokerController.showAuctionData(selectedAuctionRoom.getCurrentAuction());
                }
            });

        }
    }

    /**
     * Called when a client places a bid on an auction.
     * adjusts the current corresponding auction and informs anyone involved.
     * @param auction
     */
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
