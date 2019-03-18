package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Serializer.AuctionSerializationHandler;
import messaging.IMessageReceiver;
import messaging.MessageReceiver;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomServer;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class BrokerToOwnerGateway implements IMessageReceiver {

    private AuctionBroker auctionBroker;

    private RPCCreateAuctionRoomServer rpcCreateAuctionRoomServer;
    private MessageReceiver messageReceiver;

    private AuctionSerializationHandler auctionSerializationHandler;


    public BrokerToOwnerGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        setupConnections();
        setupSerializers();
    }

    private void setupConnections(){
        rpcCreateAuctionRoomServer = new RPCCreateAuctionRoomServer(ChannelNames.RPC_CREATEAUCTIONROOM, this);
        messageReceiver = new MessageReceiver(ChannelNames.OWNERTOBROKERNEWAUCTION, this);
    }

    private void setupSerializers(){
        auctionSerializationHandler = new AuctionSerializationHandler();
    }

    public AuctionRoom createAuctionRoom(String name){
        return auctionBroker.createAuctionRoom(name);
    }

    @Override
    public void messageReceived(String serializedAuction) {
        try {
            Auction auction = auctionSerializationHandler.deserialize(serializedAuction);
            auctionBroker.addAuction(auction);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
