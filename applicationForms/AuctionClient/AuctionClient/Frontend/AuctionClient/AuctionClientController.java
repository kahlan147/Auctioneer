package AuctionClient.Frontend.AuctionClient;

import AuctionClient.Backend.AuctionClient;
import AuctionClient.Frontend.AuctionRoom.AuctionClientRoomController;
import Classes.AuctionRoom;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionClientController extends Application{

    @FXML private TextField userName;
    @FXML private ListView<AuctionRoom> rooms;

    private AuctionClient auctionClient;

    public void initialize(){
        auctionClient = new AuctionClient(this);
    }

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AuctionClient.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("AuctionClientApp");
        stage.setScene(scene);
        stage.show();
    }

    private void createAuctionClientRoom(){
        AuctionClientRoomController auctionClientRoomController = new AuctionClientRoomController();
        auctionClientRoomController.start(new Stage());
    }

    public void OnClick_Connect(){
        //todo get information from list on what channels to use/subscribe to.
        createAuctionClientRoom();
    }

    public void OnClick_Refresh(){
        auctionClient.GetAuctionRoomsFromBroker();
    }

    public void SetObservableList(ObservableList<AuctionRoom> auctionRooms){
        rooms.setItems(auctionRooms);
    }

}
