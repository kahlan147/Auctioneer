package AuctionClient.Backend;

import AuctionClient.Backend.AuctionClient;
import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Serializer.AuctionRoomListSerializationHandler;
import messaging.PublishSubscribe.MessageSubscriber;
import messaging.RPC.GetAuctionRooms.RPCGetAuctionRoomsClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class AuctionClientGateway {

    private AuctionClient auctionClient;

    private AuctionRoomListSerializationHandler auctionRoomListSerializationHandler;

    private RPCGetAuctionRoomsClient rpcGetAuctionRoomsClient;

    private AuctionRoom connectedAuctionRoom;
    private MessageSubscriber connectedSubscriber;

    public AuctionClientGateway(AuctionClient auctionClient){
        this.auctionClient = auctionClient;
        setupSerializers();
        setupConnections();
        requestAuctionRooms();
    }

    private void setupConnections(){
        //rpcGetAuctionRoomsClient = new RPCGetAuctionRoomsClient(ChannelNames.RPC_REQUESTAUCTIONROOMS, this);
    }

    private void setupSerializers(){
        auctionRoomListSerializationHandler = new AuctionRoomListSerializationHandler();
    }

    public void requestAuctionRooms(){
        rpcGetAuctionRoomsClient = new RPCGetAuctionRoomsClient(ChannelNames.RPC_REQUESTAUCTIONROOMS, this);
        rpcGetAuctionRoomsClient.call();
    }

    public void roomsReceived(String serializedRooms){
        try{
            List<AuctionRoom> auctionRooms = auctionRoomListSerializationHandler.deserialize(serializedRooms);
            auctionClient.AddAuctionRooms(auctionRooms);
            rpcGetAuctionRoomsClient.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void connectToAuctionRoom(AuctionRoom auctionRoom){
        this.connectedAuctionRoom = auctionRoom;
    }

    public void disconnectFromAuctionRoom(){
        this.connectedAuctionRoom = null;
        this.connectedSubscriber = null;
    }
}
