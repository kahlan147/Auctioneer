package AuctionOwner.Frontend;

import AuctionOwner.Backend.AuctionOwner;
import Classes.AuctionRoom;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionOwnerController extends Application {

    @FXML private TextField newAuctionRoomName;
    @FXML private TextField newAuctionItemName;
    @FXML private TextField newAuctionStartPrice;

    @FXML private TextField selectedAuctionItemName;
    @FXML private TextField selectedAuctionHighestBid;
    @FXML private TextField selectedAuctionHighestBidder;

    @FXML private ListView<AuctionRoom> auctionRoomsList;

    private AuctionOwner auctionOwner;


    public static void main(String[] args){
        launch(args);
    }

    public void initialize(){
        auctionOwner = new AuctionOwner(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AuctionOwner.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("AuctionOwnerApp");
        stage.setScene(scene);
        stage.show();
    }

    public void SetObservableList(ObservableList<AuctionRoom> auctionRooms){
        auctionRoomsList.setItems(auctionRooms);
    }

    public void OnClick_AddToAuction(){
        String itemName = newAuctionItemName.getText();
        double startPrice;
        try{
             startPrice = Double.parseDouble(newAuctionStartPrice.getText());
        }
        catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Start price must be a number");
            alert.show();
            return;
        }
        auctionOwner.AddToAuction(itemName, startPrice);
    }

    public void OnClick_CreateNewAuctionRoom(){
        auctionOwner.CreateNewAuctionRoom(newAuctionRoomName.getText());
    }

    public void OnClick_AuctionRoomClicked(){
        AuctionRoom auctionRoom = auctionRoomsList.getSelectionModel().getSelectedItem();
        auctionOwner.SelectAuctionRoom(auctionRoom);
    }

    public void showAuctionData(String itemName, String highestBid, String highestBidder){
        selectedAuctionItemName.setText(itemName);
        selectedAuctionHighestBid.setText(highestBid);
        selectedAuctionHighestBidder.setText(highestBidder);
    }
}
