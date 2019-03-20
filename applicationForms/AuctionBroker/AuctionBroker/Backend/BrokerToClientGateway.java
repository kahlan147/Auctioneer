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

    private AuctionRoomListSerializationHandler auctionRoomListSerializationHandler;
    private AuctionSerializationHandler auctionSerializationHandler;
    private RPCGetAuctionRoomsServer rpcGetAuctionRoomsServer;
    private MessagePublisher messagePublisher;

    public BrokerToClientGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
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
        messagePublisher.createChannel(ChannelNames.TIMEPASSEDCHANNEL);
    }

    public void addPublisherToAuctionRoom(AuctionRoom auctionRoom){
        messagePublisher.createChannel(auctionRoom.getSubscribeChannel());
    }

    public void publishNewAuction(AuctionRoom auctionRoom){
        try{
            String serializedAuction = auctionSerializationHandler.serialize(auctionRoom.getCurrentAuction());
            messagePublisher.SendMessage(auctionRoom.getSubscribeChannel(),serializedAuction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public String requestAuctionRooms(){
        try{
            List<AuctionRoom> auctionRooms = auctionBroker.getAuctionRooms();
            return auctionRoomListSerializationHandler.serialize(auctionRooms);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    public void timePassed(int seconds){
        messagePublisher.SendMessage(ChannelNames.TIMEPASSEDCHANNEL, Integer.toString(seconds));
    }
}
