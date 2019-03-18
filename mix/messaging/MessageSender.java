package messaging;

import Classes.AuctionRoom;
import Serializer.AuctionRoomSerializationHandler;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class MessageSender {

    private String QUEUE_NAME;
    private Channel channel;

    public MessageSender(String queueName){
        this.QUEUE_NAME = queueName;
        setup();
    }

    private void setup(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        try {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
