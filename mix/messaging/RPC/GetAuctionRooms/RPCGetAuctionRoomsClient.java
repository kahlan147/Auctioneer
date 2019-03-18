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

    private Connection connection;
    private Channel channel;
    private String requestQueueName;

    private AuctionClientGateway auctionClientGateway;

    public RPCGetAuctionRoomsClient(String queue, AuctionClientGateway auctionClientGateway){
        this.auctionClientGateway = auctionClientGateway;
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

    public String call(){
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
