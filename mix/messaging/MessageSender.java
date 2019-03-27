package messaging;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class MessageSender {

    private Connection connection;
    private Map<String, Channel> channelMap;
    private Channel channel;

    public MessageSender(){
        channelMap = new HashMap<>();
        setup();
    }

    private void setup(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public void createChannel(String queueName){
        try {
            // channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channelMap.put(queueName,channel);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String queueName, String message){
        try {
            //Channel channel = channelMap.get(queueName);
            channel.basicPublish("", queueName, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
