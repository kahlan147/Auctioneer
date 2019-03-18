package messaging.RPC.GetAuctionRooms;

import AuctionBroker.Backend.AuctionBroker;
import AuctionBroker.Backend.BrokerToClientGateway;
import Classes.AuctionRoom;
import Serializer.AuctionRoomSerializationHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class RPCGetAuctionRoomsServer {

    private String RPC_QUEUE_NAME;

    private BrokerToClientGateway brokerToClientGateway;

    public RPCGetAuctionRoomsServer(String queueName, BrokerToClientGateway brokerToClientGateway){
        this.RPC_QUEUE_NAME = queueName;
        this.brokerToClientGateway = brokerToClientGateway;
        setup();
    }

    private void setup(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try{
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            ///channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests for auctionrooms");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    System.out.println("received");
                    String serializedAuctionRooms = getAuctionRooms();
                    System.out.println(serializedAuctionRooms);
                    //Return the auction rooms to the requesting client
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), null, serializedAuctionRooms.getBytes());
                }
                catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                }
            };
            //Await calls and run a callback on arrival.
            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
            }));
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getAuctionRooms() {
        return brokerToClientGateway.requestAuctionRooms();
    }
}
