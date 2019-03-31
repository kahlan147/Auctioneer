package messaging.PublishSubscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class MessagePublisher {

    private Channel channel;

    public MessagePublisher(){
        setup();
    }

    /**
     * Main createQueue, create channel.
     */
    private void setup() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates an exchange to be used in the future for sending messages.
     * @param exchangeName
     */
    public void createExchange(String exchangeName){
        try {
            channel.exchangeDeclare(exchangeName, "fanout"); //declare a new exchange
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Sends a message over the exchange corresponding to the given exchangename
     * @param exchangeName
     * @param message
     */
    public void sendMessage(String exchangeName, String message){
        try {
            //send a message on the given exchange.
            channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
