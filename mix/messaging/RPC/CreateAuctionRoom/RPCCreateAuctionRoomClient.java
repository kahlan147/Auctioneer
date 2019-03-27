package messaging.RPC.CreateAuctionRoom;

import AuctionOwner.Backend.AuctionOwnerGateway;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 14/03/2019.
 */
public class RPCCreateAuctionRoomClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName;

    private AuctionOwnerGateway auctionOwnerGateway;

    public RPCCreateAuctionRoomClient(String queue, AuctionOwnerGateway auctionOwnerGateway){
        this.auctionOwnerGateway = auctionOwnerGateway;
        this.requestQueueName = queue;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    public String call(String message){
        try {
            final String corrId = UUID.randomUUID().toString();

            String replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String serializedAuctionRoom = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + serializedAuctionRoom + "'");
                auctionOwnerGateway.auctionRoomCreated(serializedAuctionRoom);
            };
            channel.basicConsume(replyQueueName, true, deliverCallback, consumerTag -> {
            });
            return replyQueueName;

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void close() throws IOException {
        connection.close();
    }
}
