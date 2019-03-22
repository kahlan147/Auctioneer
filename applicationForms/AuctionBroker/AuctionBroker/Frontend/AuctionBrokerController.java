package AuctionBroker.Frontend;

import AuctionBroker.Backend.AuctionBroker;
import Classes.Auction;
import Classes.AuctionRoom;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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
    @FXML private ProgressBar timeRemaining;

    private AuctionBroker auctionBroker;

    private AuctionRoom selectedAuctionRoom;

    public static void main(String[] args){
        launch(args);
    }

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
        selectedAuctionRoom = auctionRooms.getSelectionModel().getSelectedItem();
        if(selectedAuctionRoom != null){
            auctionBroker.SelectAuctionRoom(selectedAuctionRoom);
        }
    }

    public void showAuctionData(Auction auction){
        if(auction != null) {
            selectedAuctionItemName.setText(auction.getName());
            selectedAuctionHighestBid.setText(Double.toString(auction.getHighestBid()));
            selectedAuctionHighestBidder.setText(auction.getNameHighestBidder());
            timeRemaining.setProgress(auction.getTimePassedPercentage());
        }
        else{
            selectedAuctionItemName.setText("");
            selectedAuctionHighestBid.setText("");
            selectedAuctionHighestBidder.setText("");
            timeRemaining.setProgress(0);
        }
    }

    public void anAuctionHasFinished(AuctionRoom auctionRoom){
        if(auctionRoom == selectedAuctionRoom){
            Auction selectedAuction = selectedAuctionRoom.getCurrentAuction();
            if(selectedAuction != null){
                showAuctionData(selectedAuction);
            }
        }
    }

    public void timePassed(){
        if(selectedAuctionRoom != null && selectedAuctionRoom.getCurrentAuction() != null) {
            timeRemaining.setProgress(selectedAuctionRoom.getCurrentAuction().getTimePassedPercentage());
        }
    }

}
