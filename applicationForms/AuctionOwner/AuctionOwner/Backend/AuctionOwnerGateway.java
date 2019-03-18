package AuctionOwner.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Classes.ChannelNames;
import Serializer.AuctionRoomListSerializationHandler;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import messaging.IMessageReceiver;
import messaging.MessageReceiver;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomClient;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public class AuctionOwnerGateway{

    private AuctionOwner auctionOwner;
    private AuctionSerializationHandler auctionSerializationHandler;
    private AuctionRoomSerializationHandler auctionRoomSerializationHandler;

    private RPCCreateAuctionRoomClient rpcCreateAuctionRoomClient;
    private MessageReceiver messageReceiver;

    public AuctionOwnerGateway(AuctionOwner auctionOwner){
        this.auctionOwner = auctionOwner;
        auctionSerializationHandler = new AuctionSerializationHandler();
        auctionRoomSerializationHandler = new AuctionRoomSerializationHandler();
    }

    public void RPC_RequestNewAuctionRoom(String name){
        rpcCreateAuctionRoomClient = new RPCCreateAuctionRoomClient(ChannelNames.RPC_CREATEAUCTIONROOM, this);
        rpcCreateAuctionRoomClient.call(name);
    }

    public void AddToAuction(Auction auction){

    }

    public void auctionRoomCreated(String message) {
        try {
            AuctionRoom auctionRoom = auctionRoomSerializationHandler.deserialize(message);
            auctionOwner.addNewAuctionRoom(auctionRoom);
            rpcCreateAuctionRoomClient.close();
            rpcCreateAuctionRoomClient = null;
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
