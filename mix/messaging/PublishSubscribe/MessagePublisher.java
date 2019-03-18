package messaging;

import Classes.Auction;
import Serializer.AuctionSerializationHandler;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class MessagePublisher {

    private String exchangeName;
    private Channel channel;

    public MessagePublisher(String exchangeName){
        this.exchangeName = exchangeName;
        setup();
    }

    private void setup() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "fanout");
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    public void SendMessage(Auction auction){
        try{
            AuctionSerializationHandler auctionSerializationHandler = new AuctionSerializationHandler();
            String message = auctionSerializationHandler.serialize(auction);
            SendMessage(message);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void SendMessage(String message){
        try {
            channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
