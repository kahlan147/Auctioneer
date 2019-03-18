package messaging;

import Classes.Auction;
import Serializer.AuctionSerializationHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class MessageSubscriber {

    private String exchangeName;
    private Channel channel;

    public MessageSubscriber(String exchangeName){
        this.exchangeName = exchangeName;
        setup();
    }

    private void setup(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(exchangeName, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(message);
                newReceived(message);

            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    private void newReceived(String message){
        try {
            AuctionSerializationHandler auctionSerializer = new AuctionSerializationHandler();
            Auction auction = auctionSerializer.deserialize(message);
            System.out.println(" [x] Received '" + auction.getName() + "', '" + auction.getHighestBid() + "'");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
