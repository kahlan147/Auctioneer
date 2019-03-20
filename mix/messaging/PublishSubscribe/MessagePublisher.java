package messaging.PublishSubscribe;

import Classes.Auction;
import Serializer.AuctionSerializationHandler;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class MessagePublisher {

    private Map<String, Channel> channelMap;

    private Connection connection;

    public MessagePublisher(){
        channelMap = new HashMap<>();
        setup();
    }

    private void setup() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    public void createChannel(String exchangeName){
        try {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "fanout");
            channelMap.put(exchangeName, channel);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void SendMessage(String exchangeName, String message){
        try {
            Channel channel = channelMap.get(exchangeName);
            channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
