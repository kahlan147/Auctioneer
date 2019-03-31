package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Classes.CallBack;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import messaging.RequestReply.MessageReceiver;
import messaging.RequestReply.MessageSender;
import messaging.RPC.RPCServer;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class BrokerToOwnerGateway{

    private AuctionBroker auctionBroker;

    private RPCServer rpcServer;
    private MessageReceiver messageReceiver;

    private MessageSender messageSender;

    private AuctionSerializationHandler auctionSerializationHandler;
    private AuctionRoomSerializationHandler auctionRoomSerializationHandler;


    public BrokerToOwnerGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        setupConnections();
        setupSerializers();
    }


    /**
     * Sets up the connections
     */
    private void setupConnections(){
        rpcServer = new RPCServer();
        CallBack callBackCreateAuctionRoom = message -> RPC_createAuctionRoom(message);
        rpcServer.createQueue(ChannelNames.RPC_CREATEAUCTIONROOM,  callBackCreateAuctionRoom);
        messageReceiver = new MessageReceiver();
        CallBack callBackAuctionReceived = new CallBack() {
            @Override
            public String returnMessage(String message) {
                auctionReceived(message);
                return "";
            }
        };
        messageReceiver.createQueue(ChannelNames.OWNERTOBROKERNEWAUCTION, callBackAuctionReceived);
        messageSender = new MessageSender();
    }

    /**
     * Sets up the serializers
     */
    private void setupSerializers(){
        auctionSerializationHandler = new AuctionSerializationHandler();
        auctionRoomSerializationHandler = new AuctionRoomSerializationHandler();
    }

    /**
     * Asks the Broker to create an auction room, creates a queue to address the room with, then returns the room.
     * @param message
     * @return
     */
    public String RPC_createAuctionRoom(String message){
        try {
            AuctionRoom createdRoom = auctionBroker.createAuctionRoom(message);
            messageSender.createQueue(createdRoom.getOwnerReplyChannel());
            String serializedAuctionRoom = auctionRoomSerializationHandler.serialize(createdRoom);
            return serializedAuctionRoom;
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void auctionReceived(String serializedAuction) {
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
