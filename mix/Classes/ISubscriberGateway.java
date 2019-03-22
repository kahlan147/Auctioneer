package Classes;

/**
 * Created by Niels Verheijen on 20/03/2019.
 */
public interface ISubscriberGateway {

    void timeReceived(String message);
    void auctionReceived(String message);

}
