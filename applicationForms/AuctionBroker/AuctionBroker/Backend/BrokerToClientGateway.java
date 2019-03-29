package AuctionBroker.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.CallBack;
import Classes.ChannelNames;
import Serializer.AuctionRoomListSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.IMessageReceiver;
import messaging.MessageReceiver;
import messaging.PublishSubscribe.MessagePublisher;
import messaging.RPC.GetAuctionRooms.RPCServer;

import java.io.IOException;
import java.util.List;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class BrokerToClientGateway implements IMessageReceiver {

    private AuctionBroker auctionBroker;

    private AuctionRoomListSerializationHandler auctionRoomListSerializationHandler;
    private AuctionSerializationHandler auctionSerializationHandler;
    private RPCServer rpcServer;
    private MessagePublisher messagePublisher;
    private MessageReceiver messageReceiver;

    public BrokerToClientGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        setupSerializers();
        setupConnections();
    }

    /**
     * Sets up the serializers.
     */
    private void setupSerializers(){
        this.auctionRoomListSerializationHandler = new AuctionRoomListSerializationHandler();
        this.auctionSerializationHandler = new AuctionSerializationHandler();
    }

    /**
     * Sets up the various connections
     */
    private void setupConnections(){
        rpcServer = new RPCServer();

        CallBack callBackRequestAuctionRooms = message -> RPC_requestAuctionRooms();
        rpcServer.setup(ChannelNames.RPC_REQUESTAUCTIONROOMS, callBackRequestAuctionRooms);

        CallBack callBackRequestAuction = message -> RPC_requestAuction(message);
        rpcServer.setup(ChannelNames.RPC_REQUESTAUCTION, callBackRequestAuction);

        messagePublisher = new MessagePublisher();
        messagePublisher.createChannel(ChannelNames.TIMEPASSEDCHANNEL);
    }

    /**
     * Called when a new auctionroom has been created.
     * Create a publisher for a given auctionroom for sending out auctions.
     * @param auctionRoom
     */
    public void addPublisherToAuctionRoom(AuctionRoom auctionRoom){
        messagePublisher.createChannel(auctionRoom.getSubscribeChannel());
        messageReceiver = new MessageReceiver(auctionRoom.getClientReplyChannel(), this);
    }

    /**
     * Publish a new auction for the given room.
     * Sends an auction to all subscribers of the given room.
     * @param auctionRoom
     */
    public void publishNewAuction(AuctionRoom auctionRoom){
        try{
            String serializedAuction = auctionSerializationHandler.serialize(auctionRoom.getCurrentAuction());
            messagePublisher.SendMessage(auctionRoom.getSubscribeChannel(),serializedAuction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * returns the current auction being held at the room corresponding to the given ID.
     * @param auctionRoomId
     * @return
     */
    private String RPC_requestAuction(String auctionRoomId){
        try{
            Auction auction = auctionBroker.getAuctionFor(auctionRoomId);
            return auctionSerializationHandler.serialize(auction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * returns all available auction rooms.
     * @return
     */
    private String RPC_requestAuctionRooms(){
        try{
            List<AuctionRoom> auctionRooms = auctionBroker.getAuctionRooms();
            return auctionRoomListSerializationHandler.serialize(auctionRooms);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void messageReceived(String message) {
        try {
            Auction auction = auctionSerializationHandler.deserialize(message);
            auctionBroker.bidReceived(auction);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
