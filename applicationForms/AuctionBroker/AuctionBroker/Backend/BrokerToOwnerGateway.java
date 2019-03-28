package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Serializer.AuctionSerializationHandler;
import messaging.IMessageReceiver;
import messaging.MessageReceiver;
import messaging.MessageSender;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomServer;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class BrokerToOwnerGateway implements IMessageReceiver {

    private AuctionBroker auctionBroker;

    private RPCCreateAuctionRoomServer rpcCreateAuctionRoomServer;
    private MessageReceiver messageReceiver;

    private MessageSender messageSender;

    private AuctionSerializationHandler auctionSerializationHandler;


    public BrokerToOwnerGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        setupConnections();
        setupSerializers();
    }

    /**
     * Sets up the connections
     */
    private void setupConnections(){
        rpcCreateAuctionRoomServer = new RPCCreateAuctionRoomServer(ChannelNames.RPC_CREATEAUCTIONROOM, this);
        messageReceiver = new MessageReceiver(ChannelNames.OWNERTOBROKERNEWAUCTION, this);
        messageSender = new MessageSender();
    }

    /**
     * Sets up the serializers
     */
    private void setupSerializers(){
        auctionSerializationHandler = new AuctionSerializationHandler();
    }

    /**
     * Asks the Broker to create an auction room, creates a queue to address the room with, then returns the room.
     * @param name
     * @return
     */
    public AuctionRoom RPC_createAuctionRoom(String name){
        AuctionRoom createdRoom = auctionBroker.createAuctionRoom(name);
        messageSender.createQueue(createdRoom.getOwnerReplyChannel());
        return createdRoom;
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

    /**
     * Informs the owner of an auctionroom of a change in their auction;
     * 1. A new auction has started
     * 2. A new bid was placed.
     * @param auctionRoom
     */
    public void publishNewAuction(AuctionRoom auctionRoom){
        try {
            String serializedAuction = auctionSerializationHandler.serialize(auctionRoom.getCurrentAuction());
            messageSender.sendMessage(auctionRoom.getOwnerReplyChannel(), serializedAuction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
