package Classes;

/**
 * Created by Niels Verheijen on 10/03/2019.
 */
public class AuctionRoom {

    private String name;

    public AuctionRoom(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
