package AuctionClient.Backend;

import Classes.*;
import Serializer.AuctionRoomListSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.RequestReply.MessageSender;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.RPCClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class AuctionClientGateway implements ISubscriberGateway {

    private AuctionClient auctionClient;

    private AuctionRoomListSerializationHandler auctionRoomListSerializationHandler;
    private AuctionSerializationHandler auctionSerializationHandler;

    private RPCClient rpcClient;

    private AuctionRoom connectedAuctionRoom;
    private MessageSubscriber messageSubscriber;
    private MessageSender messageSender;


    public AuctionClientGateway(AuctionClient auctionClient){
        this.auctionClient = auctionClient;
        setupSerializers();
        setupConnections();
        requestAuctionRooms();
    }

    private void setupConnections(){
        rpcClient = new RPCClient();
        messageSubscriber = new MessageSubscriber(this);
        messageSubscriber.createNewChannel(ChannelNames.TIMEPASSEDCHANNEL);
        messageSender = new MessageSender();
    }

    private void setupSerializers(){
        auctionRoomListSerializationHandler = new AuctionRoomListSerializationHandler();
        auctionSerializationHandler = new AuctionSerializationHandler();
    }

    public void requestAuctionRooms(){
        CallBack callBackRoomsReceived = new CallBack() {
            @Override
            public String returnMessage(String message) {
                roomsReceived(message);
                return "";
            }
        };
        rpcClient.call(ChannelNames.RPC_REQUESTAUCTIONROOMS, callBackRoomsReceived);
    }

    public void requestAuction(String auctionRoomId){
        CallBack callBackAuctionReceived = new CallBack() {
            @Override
            public String returnMessage(String message) {
                auctionReceived(message);
                return "";
            }
        };
        rpcClient.call(ChannelNames.RPC_REQUESTAUCTION, callBackAuctionReceived, auctionRoomId);
    }

    public void roomsReceived(String serializedRooms){
        try{
            List<AuctionRoom> auctionRooms = auctionRoomListSerializationHandler.deserialize(serializedRooms);
            auctionClient.addAuctionRooms(auctionRooms);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void connectToAuctionRoom(AuctionRoom auctionRoom){
        messageSubscriber.createNewChannel(auctionRoom.getSubscribeChannel());
        messageSender.createQueue(auctionRoom.getClientReplyChannel());
        requestAuction(auctionRoom.getId());
    }

    public void disconnectFromAuctionRoom(){
        this.connectedAuctionRoom = null;
    }

    public void placeBid(String clientReplyChannel, Auction auction){
        try {
            String serializedAuction = auctionSerializationHandler.serialize(auction);
            messageSender.sendMessage(clientReplyChannel, serializedAuction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void timeReceived(String message) {
        int newTime = Integer.parseInt(message);
        auctionClient.timePassed(newTime);
    }

    @Override
    public void auctionReceived(String message) {
        try {
            Auction auction = auctionSerializationHandler.deserialize(message);
            if(auction != null) {
                auctionClient.newAuctionReceived(auction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
