package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import AuctionBroker.Frontend.AuctionBrokerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomServer;
import messaging.PublishSubscribe.MessageSubscriber;

/**
 * Created by Niels Verheijen on 11/03/2019.
 */
public class AuctionBroker {

    private AuctionBrokerController auctionBrokerController;

    private ObservableList<AuctionRoom> auctionRooms;
    private AuctionRoom selectedAuctionRoom;

    private MessageSubscriber messageSubscriber;
    private RPCCreateAuctionRoomServer RPCCreateAuctionRoomServer;

    public AuctionBroker(AuctionBrokerController auctionBrokerController){
        this.auctionBrokerController = auctionBrokerController;

        auctionRooms = FXCollections.<AuctionRoom>observableArrayList();
        auctionBrokerController.SetObservableList(auctionRooms);

        messageSubscriber = new MessageSubscriber("Owner");
        RPCCreateAuctionRoomServer = new RPCCreateAuctionRoomServer("CreateAuctionRoom", this);
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

    public AuctionRoom createAuctionRoom(String name, String replyQueue){
        AuctionRoom auctionRoom = new AuctionRoom(name);
        auctionRooms.add(auctionRoom);
        return auctionRoom;
    }


}
