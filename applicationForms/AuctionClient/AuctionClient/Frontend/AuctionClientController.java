package AuctionClient.Frontend;

import AuctionClient.Backend.AuctionClient;
import Classes.Auction;
import Classes.AuctionRoom;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class AuctionClientController extends Application{

    @FXML private Pane noConnectionPane;
    @FXML private TextField userName;
    @FXML private ListView<AuctionRoom> rooms;

    @FXML private Pane connectedPane;
    @FXML private TextField itemName;
    @FXML private TextField highestBid;
    @FXML private ProgressBar timeRemaining;
    @FXML private TextField myBid;

    private AuctionClient auctionClient;
    private Auction shownAuction;

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

    public void connected(){
        noConnectionPane.setOpacity(0);
        noConnectionPane.setDisable(true);
        connectedPane.setOpacity(1);
        connectedPane.setDisable(false);
    }

    public void showAuction(Auction auction){
        this.shownAuction = auction;
        if(auction != null){
            itemName.setText(auction.getName());
            highestBid.setText(Double.toString(auction.getHighestBid()));
            timeRemaining.setProgress(auction.getTimePassedPercentage());
        }
        else{
            itemName.setText("");
            highestBid.setText("");
            timeRemaining.setProgress(0);
        }
    }

    public void timePassed(){
        if(shownAuction != null){
            timeRemaining.setProgress(shownAuction.getTimePassedPercentage());
        }
    }

    public void OnClick_Connect(){
        String username = userName.getText();
        if(username.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a name");
            alert.show();
            return;
        }

        AuctionRoom auctionRoom = rooms.getSelectionModel().getSelectedItem();
        if(auctionRoom == null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an auction room first.");
            alert.show();
            return;
        }
        auctionClient.setUserName(username);
        auctionClient.connectTo(auctionRoom);
    }

    public void OnClick_Refresh(){
        auctionClient.getAuctionRoomsFromBroker();
    }

    public void OnClick_Bid(){
        double bid = 0;
        try{
            bid = Double.parseDouble(myBid.getText());
        }
        catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Start price must be a number");
            alert.show();
            return;
        }
        auctionClient.bid(bid);
    }

    public void OnClick_Disconnect(){
        noConnectionPane.setOpacity(1);
        noConnectionPane.setDisable(false);
        connectedPane.setOpacity(0);
        connectedPane.setDisable(true);
        auctionClient.disconnect();
    }

    public void setObservableList(ObservableList<AuctionRoom> auctionRooms){
        rooms.setItems(auctionRooms);
    }

}
