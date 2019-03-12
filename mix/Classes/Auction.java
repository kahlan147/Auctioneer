package Classes;
/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class Auction {
    private String name;
    private double highestBid;
    private String nameHighestBidder;

    public Auction(String name){
        this.name = name;
        highestBid = -.00001;
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
