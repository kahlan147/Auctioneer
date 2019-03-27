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

    public void createChannel(String exchangeName){
        try {
            channel.exchangeDeclare(exchangeName, "fanout");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void SendMessage(String exchangeName, String message){
        try {
            channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
