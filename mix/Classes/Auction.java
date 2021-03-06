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

    @JsonProperty("auctionStartTime")
    private int auctionStartTime;
    @JsonProperty("auctionDuration")
    private int auctionDuration;

    private int currentAuctionTime;

    public Auction(String name){
        this.name = name;
        highestBid = -.00001;
        auctionStartTime = 0;
    }

    public Auction(String name, double highestBid, String nameHighestBidder){
        this.name = name;
        this.highestBid = highestBid;
        this.nameHighestBidder = nameHighestBidder;
        auctionStartTime = 0;
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

    public boolean newBid(double newBid, String nameBidder){
        if(highestBid < newBid){
            highestBid = newBid;
            nameHighestBidder = nameBidder;
            auctionStartTime = currentAuctionTime;
            return true;
        }
        return false;
    }

    public void setAuctionStartTime(int auctionStartTime){
        this.auctionStartTime = auctionStartTime;
    }

    public void setAuctionDuration(int auctionDuration){
        if(auctionDuration <= 0){
            auctionDuration = 1;
        }
        this.auctionDuration = auctionDuration;
    }

    public boolean timePassed(int currentAuctionTime){
        this.currentAuctionTime = currentAuctionTime;
        return(this.auctionStartTime + this.auctionDuration < this.currentAuctionTime);
    }

    public float getTimePassedPercentage(){
        return  ((1f / (float)auctionDuration) * ((float)currentAuctionTime - (float)auctionStartTime));
    }
}
