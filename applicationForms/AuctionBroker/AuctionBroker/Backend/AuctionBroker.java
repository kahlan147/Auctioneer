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

    public AuctionBroker(AuctionBrokerController auctionBrokerController){
        this.auctionBrokerController = auctionBrokerController;

        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionBrokerController.SetObservableList(auctionRooms);

        brokerToOwnerGateway = new BrokerToOwnerGateway(this);
        brokerToClientGateway = new BrokerToClientGateway(this);
        timer();
    }

    private void timer(){
        int secondsTillPing = 1;

        int delay = secondsTillPing*1000;
        int period = secondsTillPing*1000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                secondPassed();
            }
            }, delay, period);
    }

    private void secondPassed(){
        auctionBrokerController.timePassed();
        if(auctionRooms.size() > 0){
            for(AuctionRoom auctionRoom : auctionRooms){
                if(auctionRoom.timePassed(1)){
                    auctionBrokerController.anAuctionHasFinished(auctionRoom);
                }
            }
        }
        auctionBrokerController.timePassed();
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
        findAuctionRoom(auction.getAuctionRoomId()).newAuction(auction);
    }


}
