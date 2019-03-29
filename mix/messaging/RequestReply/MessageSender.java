package messaging.RequestReply;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class MessageSender {

    //The channel holding the queues for messaging
    private Channel channel;

    public MessageSender(){
        setup();
    }

    /**
     * Sets up the connection and channel
     */
    private void setup(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a queue on the existing channel.
     * Used so multiple queues can be created with a single messageSender class/channel.
     * @param queueName the name of the to be added queue.
     */
    public void createQueue(String queueName){
        try {
            channel.queueDeclare(queueName, false, false, false, null);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Sends a message over the queue corresponding to the given queuename.
     * @param queueName the name of the queue a message should be send over.
     * @param message the message to be send.
     */
    public void sendMessage(String queueName, String message){
        try {
            channel.basicPublish("", queueName, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
