package Classes;

import Serializer.JacksonDeserializers.AuctionRoomDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

/**
 * Created by Niels Verheijen on 10/03/2019.
 */
@JsonDeserialize(using = AuctionRoomDeserializer.class)
public class AuctionRoom{

    @JsonProperty("name")
    private String name;

    @JsonProperty("subscribeChannel")
    private String subscribeChannel;

    @JsonProperty("replyChannel")
    private String replyChannel;

    private List<Auction> upcomingAuctions;
    private Auction currentAuction;

    public void setChannels(String subscribeChannel, String replyChannel){
        this.subscribeChannel = subscribeChannel;
        this.replyChannel = replyChannel;
    }

    public String getSubscribeChannel(){
        return subscribeChannel;
    }

    public String getReplyChannel(){
        return replyChannel;
    }

    public AuctionRoom(String name){
        this.name = name;
        upcomingAuctions = new ArrayList<>();
    }


    public Auction getCurrentAuction(){
        return currentAuction;
    }

    public void newAuction(Auction auction){
        auction.setMaxAuctionTime(30);
        upcomingAuctions.add(auction);
        if(currentAuction == null){
            currentAuction = upcomingAuctions.get(0);
        }
    }

    public boolean timePassed(int seconds){
        if(currentAuction != null && currentAuction.timePassed(seconds)){
            currentAuction = null;
            if(upcomingAuctions.size() > 0){
                currentAuction = upcomingAuctions.remove(0);
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return name;
    }

}
