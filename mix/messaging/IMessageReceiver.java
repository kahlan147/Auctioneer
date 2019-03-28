package messaging;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public interface IMessageReceiver {

    /**
     * Called whenever a message has been received in a messageReceiver class.
     * @param message
     */
    void messageReceived(String message);
}
