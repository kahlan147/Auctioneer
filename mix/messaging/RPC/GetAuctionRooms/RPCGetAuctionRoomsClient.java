package messaging.RPC.GetAuctionRooms;

import AuctionClient.Backend.AuctionClientGateway;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class RPCGetAuctionRoomsClient {

    private Channel channel;

    private AuctionClientGateway auctionClientGateway;

    public RPCGetAuctionRoomsClient(AuctionClientGateway auctionClientGateway){
        this.auctionClientGateway = auctionClientGateway;
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

    public void requestAuctionRooms(String requestQueueName){
        try {
            final String corrId = UUID.randomUUID().toString();

            String replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            channel.basicPublish("", requestQueueName, props, "".getBytes("UTF-8"));

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String serializedRooms = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + serializedRooms + "'");
                auctionClientGateway.roomsReceived(serializedRooms);
            };
            channel.basicConsume(replyQueueName, true, deliverCallback, consumerTag -> {
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void requestAuction(String requestQueueName, String auctionRoomId){
        try {
            final String corrId = UUID.randomUUID().toString();

            String replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            channel.basicPublish("", requestQueueName, props, auctionRoomId.getBytes("UTF-8"));

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String serializedAuction = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + serializedAuction + "'");
                auctionClientGateway.auctionReceived(serializedAuction);
            };
            channel.basicConsume(replyQueueName, true, deliverCallback, consumerTag -> {
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
