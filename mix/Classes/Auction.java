package Classes;
/**
 * Created by Niels Verheijen on 08/03/2019.
 */
public class Auction {
    private String name;

    public Auction(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
