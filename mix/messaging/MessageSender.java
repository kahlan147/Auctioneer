package messaging;

import Classes.AuctionRoom;
import Classes.ChannelNames;
import Serializer.AuctionRoomSerializationHandler;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class MessageSender {

    private Connection connection;
    private Map<String, Channel> channelMap;

    public MessageSender(){
        channelMap = new HashMap<>();
        setup();
    }

    private void setup(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public void createChannel(String channelName){
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(channelName, false, false, false, null);
            channelMap.put(channelName,channel);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String channelName, String message){
        try {
            Channel channel = channelMap.get(channelName);
            channel.basicPublish("", channelName, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
