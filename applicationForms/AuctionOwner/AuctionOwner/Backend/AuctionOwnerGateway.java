package AuctionOwner.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Classes.ISubscriberGateway;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.IMessageReceiver;
import messaging.MessageReceiver;
import messaging.MessageSender;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public class AuctionOwnerGateway implements ISubscriberGateway, IMessageReceiver {

    private AuctionOwner auctionOwner;
    private AuctionSerializationHandler auctionSerializationHandler;
    private AuctionRoomSerializationHandler auctionRoomSerializationHandler;

    private RPCCreateAuctionRoomClient rpcCreateAuctionRoomClient;
    private MessageSubscriber messageSubscriber;
    private MessageSender messageSender;
    private List<MessageReceiver> messageReceivers;

    public AuctionOwnerGateway(AuctionOwner auctionOwner){
        this.auctionOwner = auctionOwner;
        auctionSerializationHandler = new AuctionSerializationHandler();
        auctionRoomSerializationHandler = new AuctionRoomSerializationHandler();
        setupChannels();
    }

    private void setupChannels(){
        messageSubscriber = new MessageSubscriber(this);
        messageSubscriber.createNewChannel(ChannelNames.TIMEPASSEDCHANNEL);
        rpcCreateAuctionRoomClient = new RPCCreateAuctionRoomClient(ChannelNames.RPC_CREATEAUCTIONROOM, this);
        messageSender = new MessageSender();
        messageSender.createQueue(ChannelNames.OWNERTOBROKERNEWAUCTION);
        messageReceivers = new ArrayList<MessageReceiver>();
    }

    public void RPC_RequestNewAuctionRoom(String name){
        rpcCreateAuctionRoomClient.call(name);
    }

    public void AddToAuction(Auction auction){
        try {
            String serializedAuction = auctionSerializationHandler.serialize(auction);
            messageSender.sendMessage(ChannelNames.OWNERTOBROKERNEWAUCTION, serializedAuction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void auctionRoomCreated(String message) {
        try {
            AuctionRoom auctionRoom = auctionRoomSerializationHandler.deserialize(message);
            auctionOwner.addNewAuctionRoom(auctionRoom);
            MessageReceiver messageReceiver = new MessageReceiver(auctionRoom.getOwnerReplyChannel(), this);
            messageReceivers.add(messageReceiver);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void timeReceived(String message) {
        int newTime = Integer.parseInt(message);
        auctionOwner.timePassed(newTime);
    }

    @Override
    public void auctionReceived(String message) {
        try{
            Auction auction = auctionSerializationHandler.deserialize(message);
            auctionOwner.addAuctionTo(auction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void messageReceived(String message) {
        auctionReceived(message);
    }
}
