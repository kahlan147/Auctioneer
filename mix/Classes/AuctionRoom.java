package Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Verheijen on 10/03/2019.
 */
public class AuctionRoom {

    private String name;

    private List<Auction> upcomingAuctions;

    private Auction currentAuction;

    public AuctionRoom(String name){
        this.name = name;
        upcomingAuctions = new ArrayList<>();
    }

    public Auction getCurrentAuction(){
        return currentAuction;
    }

    public void NewAuction(Auction auction){
        upcomingAuctions.add(auction);
        if(currentAuction == null){
            currentAuction = upcomingAuctions.get(0);
        }
    }

    @Override
    public String toString(){
        return name;
    }
}
