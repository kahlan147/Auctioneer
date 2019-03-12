package AuctionBroker.Frontend;

import AuctionBroker.Backend.AuctionBroker;
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

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionBrokerController extends Application {

    @FXML private ListView<AuctionRoom> auctionRooms;
    @FXML private TextField selectedAuctionItemName;
    @FXML private TextField selectedAuctionHighestBid;
    @FXML private TextField selectedAuctionHighestBidder;

    private AuctionBroker auctionBroker;

    public void initialize(){
        auctionBroker = new AuctionBroker(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AuctionBroker.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("AuctionBrokerApp");
        stage.setScene(scene);
        stage.show();
    }

    public void SetObservableList(ObservableList<AuctionRoom> observableList){
        auctionRooms.setItems(observableList);
    }

    public void OnClick_RoomsList(){
        AuctionRoom auctionRoom = auctionRooms.getSelectionModel().getSelectedItem();
        auctionBroker.SelectAuctionRoom(auctionRoom);
    }

    public void showAuctionData(String itemName, String highestBid, String highestBidder){
        selectedAuctionItemName.setText(itemName);
        selectedAuctionHighestBid.setText(highestBid);
        selectedAuctionHighestBidder.setText(highestBidder);
    }

}
