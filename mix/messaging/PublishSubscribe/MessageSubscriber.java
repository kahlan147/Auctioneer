package messaging.PublishSubscribe;

import AuctionClient.Backend.AuctionClient;
import AuctionClient.Backend.AuctionClientGateway;
import Classes.Auction;
import Classes.ChannelNames;
import Classes.ISubscriberGateway;
import Serializer.AuctionSerializationHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class MessageSubscriber {

    private ISubscriberGateway subscriberGateway;

    private Connection connection;
    private List<Channel> channels;

    public MessageSubscriber(ISubscriberGateway subscriberGateway){
        this.subscriberGateway = subscriberGateway;
        channels = new ArrayList<>();
        setup();
    }

    private void setup(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();

        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    public void createNewChannel(String channelName){
        try {
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(channelName, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, channelName, "");
            channels.add(channel);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                newAuctionReceived(message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void newAuctionReceived(String message){
        subscriberGateway.messageReceived(message);
    }
}
