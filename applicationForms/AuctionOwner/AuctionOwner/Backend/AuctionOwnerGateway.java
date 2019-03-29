package AuctionOwner.Backend;

import Classes.*;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.RequestReply.MessageReceiver;
import messaging.RequestReply.MessageSender;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.RPCClient;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public class AuctionOwnerGateway implements ISubscriberGateway {

    private AuctionOwner auctionOwner;
    private AuctionSerializationHandler auctionSerializationHandler;
    private AuctionRoomSerializationHandler auctionRoomSerializationHandler;

    private RPCClient rpcCreateAuctionRoomClient;
    private MessageSubscriber messageSubscriber;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;

    public AuctionOwnerGateway(AuctionOwner auctionOwner){
        this.auctionOwner = auctionOwner;
        auctionSerializationHandler = new AuctionSerializationHandler();
        auctionRoomSerializationHandler = new AuctionRoomSerializationHandler();
        setupChannels();
    }

    private void setupChannels(){
        messageSubscriber = new MessageSubscriber(this);
        messageSubscriber.createNewChannel(ChannelNames.TIMEPASSEDCHANNEL);
        rpcCreateAuctionRoomClient = new RPCClient();
        messageSender = new MessageSender();
        messageSender.createQueue(ChannelNames.OWNERTOBROKERNEWAUCTION);
        messageReceiver = new MessageReceiver();
    }

    public void RPC_RequestNewAuctionRoom(String name){
        CallBack callBackAuctionRoomCreated = new CallBack() {
            @Override
            public String returnMessage(String message) {
                auctionRoomCreated(message);
                return "";
            }
        };
        rpcCreateAuctionRoomClient.call(ChannelNames.RPC_CREATEAUCTIONROOM, callBackAuctionRoomCreated, name);
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
            CallBack callBackAuctionReceived = new CallBack() {
                @Override
                public String returnMessage(String message) {
                    auctionReceived(message);
                    return "";
                }
            };
            messageReceiver.setup(auctionRoom.getOwnerReplyChannel(), callBackAuctionReceived);
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
}
