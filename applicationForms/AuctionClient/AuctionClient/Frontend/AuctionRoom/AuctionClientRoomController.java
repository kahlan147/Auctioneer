package AuctionClient.Frontend.AuctionRoom;

import AuctionClient.Backend.AuctionClientRoom;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;


/**
 * Created by Niels Verheijen on 11/03/2019.
 */
public class AuctionClientRoomController extends Application {

    @FXML private TextField currentItem;
    @FXML private TextField currentHighestBid;
    @FXML private TextField bidAmount;
    @FXML private Button disconnectButton;

    private AuctionClientRoom auctionClientRoom;

    public void initialize(){
        //auctionClientRoom = new AuctionClientRoom(this);
    }

    public void start(Stage stage){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AuctionClientRoom.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("AuctionClientRoomApp");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setAuctionClientRoom(AuctionClientRoom auctionClientRoom){
        this.auctionClientRoom = auctionClientRoom;
    }

    public void OnClick_Bid(){
        double bid;
        try {
            bid = Double.parseDouble(bidAmount.getText());
        }
        catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bid must be a number");
            alert.show();
            return;
        }
        auctionClientRoom.bid(bid);
    }

    public void OnClick_Disconnect(){
        auctionClientRoom.disconnect();
        disconnectButton.getScene().getWindow().hide();
    }

    public void ShowData(String itemName, String highestBid){
        currentItem.setText(itemName);
        currentHighestBid.setText(highestBid);
    }
}
