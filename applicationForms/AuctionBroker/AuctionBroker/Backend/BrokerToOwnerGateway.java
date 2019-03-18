package AuctionBroker.Backend;

import Classes.AuctionRoom;
import Classes.ChannelNames;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomServer;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class BrokerToOwnerGateway {

    private AuctionBroker auctionBroker;

    private RPCCreateAuctionRoomServer rpcCreateAuctionRoomServer;


    public BrokerToOwnerGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        setupConnections();
    }

    private void setupConnections(){
        rpcCreateAuctionRoomServer = new RPCCreateAuctionRoomServer(ChannelNames.RPC_CREATEAUCTIONROOM, this);
    }

    public AuctionRoom createAuctionRoom(String name){
        return auctionBroker.createAuctionRoom(name);
    }
}
