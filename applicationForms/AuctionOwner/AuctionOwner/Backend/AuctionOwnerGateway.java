package AuctionOwner.Backend;

import Classes.*;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.RequestReply.MessageReceiver;
import messaging.RequestReply.MessageSender;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.RPCClient;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public class AuctionOwnerGateway {

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
        messageSubscriber = new MessageSubscriber();
        CallBack callBackTimeReceived = new CallBack() {
            @Override
            public String returnMessage(String message) {
                timeReceived(message);
                return "";
            }
        };
        messageSubscriber.createExchange(ChannelNames.TIMEPASSEDCHANNEL, callBackTimeReceived);
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

    private void auctionRoomCreated(String message) {
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
            messageReceiver.createQueue(auctionRoom.getOwnerReplyChannel(), callBackAuctionReceived);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void timeReceived(String message) {
        int newTime = Integer.parseInt(message);
        auctionOwner.timePassed(newTime);
    }

    private void auctionReceived(String message) {
        try{
            Auction auction = auctionSerializationHandler.deserialize(message);
            auctionOwner.addAuctionTo(auction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
