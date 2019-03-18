package AuctionClient.Backend;

import AuctionClient.Frontend.AuctionRoom.AuctionClientRoomController;
import Classes.AuctionRoom;
import javafx.stage.Stage;

/**
 * Created by Niels Verheijen on 12/03/2019.
 */
public class AuctionClientRoom {

    private AuctionClientRoomController auctionClientRoomController;

    private AuctionRoom connectedRoom;
    private AuctionClient auctionClient;

    public AuctionClientRoom(AuctionRoom auctionRoom, AuctionClient auctionClient){
        auctionClientRoomController = new AuctionClientRoomController();
        auctionClientRoomController.start(new Stage());
        auctionClientRoomController.setAuctionClientRoom(this);
        this.connectedRoom = auctionRoom;
        this.auctionClient = auctionClient;
    }

    public void bid(double bid){

    }

    public void disconnect(){
        try {
            auctionClientRoomController.stop();
            auctionClient.disconnect(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
