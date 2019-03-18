package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Serializer.AuctionRoomListSerializationHandler;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.MessageReceiver;
import messaging.PublishSubscribe.MessagePublisher;
import messaging.RPC.GetAuctionRooms.RPCGetAuctionRoomsServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class BrokerToClientGateway {

    private AuctionBroker auctionBroker;

    private Map<AuctionRoom, MessagePublisher> auctionRoomPublishers;
    private AuctionRoomListSerializationHandler auctionRoomListSerializationHandler;
    private AuctionSerializationHandler auctionSerializationHandler;
    private RPCGetAuctionRoomsServer rpcGetAuctionRoomsServer;
    private MessagePublisher messagePublisher;

    public BrokerToClientGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        this.auctionRoomPublishers = new HashMap<>();
        setupSerializers();
        setupConnections();
    }

    private void setupSerializers(){
        this.auctionRoomListSerializationHandler = new AuctionRoomListSerializationHandler();
        this.auctionSerializationHandler = new AuctionSerializationHandler();
    }

    private void setupConnections(){
        rpcGetAuctionRoomsServer = new RPCGetAuctionRoomsServer(ChannelNames.RPC_REQUESTAUCTIONROOMS, this);
        messagePublisher = new MessagePublisher();
    }

    public void addPublisherToAuctionRoom(AuctionRoom auctionRoom){
        messagePublisher.createChannel(auctionRoom.getSubscribeChannel());
    }

    public void publishAuction(AuctionRoom auctionRoom){
        try{
            String serializedAuction = auctionSerializationHandler.serialize(auctionRoom.getCurrentAuction());
            auctionRoomPublishers.get(auctionRoom).SendMessage(auctionRoom.getSubscribeChannel(), serializedAuction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public String requestAuctionRooms(){
        try{
            List<AuctionRoom> auctionRooms = auctionBroker.getAuctionRooms();
            String serializedRooms = auctionRoomListSerializationHandler.serialize(auctionRooms);
            return serializedRooms;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }
}
