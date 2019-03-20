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

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("subscribeChannel")
    private String subscribeChannel;

    @JsonProperty("clientReplyChannel")
    private String clientReplyChannel;

    @JsonProperty("ownerReplyChannel")
    private String ownerReplyChannel;

    private List<Auction> upcomingAuctions;
    private Auction currentAuction;

    public void setChannels(String subscribeChannel, String clientReplyChannel, String ownerReplyChannel){
        this.subscribeChannel = subscribeChannel;
        this.clientReplyChannel = clientReplyChannel;
        this.ownerReplyChannel = ownerReplyChannel;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public String getSubscribeChannel(){
        return subscribeChannel;
    }

    public String getClientReplyChannel(){
        return clientReplyChannel;
    }

    public String getOwnerReplyChannel(){return ownerReplyChannel;}

    public AuctionRoom(String name){
        this.name = name;
        upcomingAuctions = new ArrayList<>();
    }


    public Auction getCurrentAuction(){
        return currentAuction;
    }

    public void newAuction(Auction auction){
        upcomingAuctions.add(auction);
        if(currentAuction == null){
            currentAuction = upcomingAuctions.get(0);
        }
    }

    public boolean timePassed(int newTime){
        if(currentAuction != null && currentAuction.timePassed(newTime)){
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
