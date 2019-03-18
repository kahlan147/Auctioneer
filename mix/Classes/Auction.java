package Classes;

import Serializer.JacksonDeserializers.AuctionDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by Niels Verheijen on 08/03/2019.
 */
@JsonDeserialize(using = AuctionDeserializer.class)
public class Auction {

    @JsonProperty("name")
    private String name;
    @JsonProperty("highestBid")
    private double highestBid;
    @JsonProperty("nameHighestBidder")
    private String nameHighestBidder;
    @JsonProperty("auctionRoomId")
    private String auctionRoomId;

    public Auction(String name){
        this.name = name;
        highestBid = -.00001;
    }

    public Auction(String name, double highestBid, String nameHighestBidder){
        this.name = name;
        this.highestBid = highestBid;
        this.nameHighestBidder = nameHighestBidder;
    }

    public void setAuctionRoomId(String auctionRoomId){
        this.auctionRoomId = auctionRoomId;
    }

    public String getAuctionRoomId(){
        return auctionRoomId;
    }

    public String getName(){
        return name;
    }

    public double getHighestBid(){
        return highestBid;
    }

    public String getNameHighestBidder(){
        return nameHighestBidder;
    }

    public void newBid(double newBid, String nameBidder){
        if(highestBid < newBid){
            highestBid = newBid;
            nameHighestBidder = nameBidder;
        }
    }
}
