package AuctionOwner.Backend;

import Classes.Auction;
import Classes.AuctionRoom;
import Serializer.AuctionRoomSerializationHandler;
import Serializer.AuctionSerializationHandler;
import messaging.IMessageReceiver;
import messaging.MessageReceiver;
import messaging.RPC.CreateAuctionRoom.RPCCreateAuctionRoomClient;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public class AuctionOwnerGateway implements IMessageReceiver {

    private AuctionOwner auctionOwner;
    private AuctionSerializationHandler auctionSerializationHandler;
    private AuctionRoomSerializationHandler auctionRoomSerializationHandler;

    private RPCCreateAuctionRoomClient RPCCreateAuctionRoomClient;
    private MessageReceiver messageReceiver;

    public AuctionOwnerGateway(AuctionOwner auctionOwner){
        this.auctionOwner = auctionOwner;
        auctionSerializationHandler = new AuctionSerializationHandler();
        auctionRoomSerializationHandler = new AuctionRoomSerializationHandler();
        RPCCreateAuctionRoomClient = new RPCCreateAuctionRoomClient("CreateAuctionRoom", this);
    }

    public void RPC_RequestNewAuctionRoom(String name){
        RPCCreateAuctionRoomClient.call(name);
    }

    public void AddToAuction(Auction auction){

    }

    @Override
    public void messageReceived(String message) {
        try {
            AuctionRoom auctionRoom = auctionRoomSerializationHandler.deserialize(message);
            auctionOwner.addNewAuctionRoom(auctionRoom);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
