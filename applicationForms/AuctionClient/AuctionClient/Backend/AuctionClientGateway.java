package AuctionClient.Backend;

import AuctionClient.Backend.AuctionClient;
import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Classes.ISubscriberGateway;
import Serializer.AuctionRoomListSerializationHandler;
import Serializer.AuctionSerializationHandler;
import javafx.application.Platform;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.GetAuctionRooms.RPCGetAuctionRoomsClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class AuctionClientGateway implements ISubscriberGateway {

    private AuctionClient auctionClient;

    private AuctionRoomListSerializationHandler auctionRoomListSerializationHandler;
    private AuctionSerializationHandler auctionSerializationHandler;

    private RPCGetAuctionRoomsClient rpcGetAuctionRoomsClient;

    private AuctionRoom connectedAuctionRoom;
    private MessageSubscriber messageSubscriber;

    public AuctionClientGateway(AuctionClient auctionClient){
        this.auctionClient = auctionClient;
        setupSerializers();
        setupConnections();
        requestAuctionRooms();
    }

    private void setupConnections(){
        rpcGetAuctionRoomsClient = new RPCGetAuctionRoomsClient(ChannelNames.RPC_REQUESTAUCTIONROOMS, this);
        messageSubscriber = new MessageSubscriber(this);
        messageSubscriber.createNewChannel(ChannelNames.TIMEPASSEDCHANNEL);
    }

    private void setupSerializers(){
        auctionRoomListSerializationHandler = new AuctionRoomListSerializationHandler();
        auctionSerializationHandler = new AuctionSerializationHandler();
    }

    public void requestAuctionRooms(){
        rpcGetAuctionRoomsClient.call();
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
    }

    public void disconnectFromAuctionRoom(){
        this.connectedAuctionRoom = null;
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
            auctionClient.newAuctionReceived(auction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
