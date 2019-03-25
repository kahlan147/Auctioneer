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

    private Connection connection;
    private BrokerToClientGateway brokerToClientGateway;

    public RPCGetAuctionRoomsServer(BrokerToClientGateway brokerToClientGateway){
        this.brokerToClientGateway = brokerToClientGateway;
        setup();
    }

    private void setup(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try{
            connection = factory.newConnection();
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setupRequestAuctionRooms(String queueName){
        try{
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queuePurge(queueName);

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
            channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> {
            }));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupRequestAuction(String queueName){
        try{
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queuePurge(queueName);

            System.out.println(" [x] Awaiting RPC requests for auctionrooms");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    System.out.println("received");
                    String auctionRoomId = new String(delivery.getBody(), "UTF-8");
                    String serializedAuction = getAuction(auctionRoomId);
                    System.out.println(serializedAuction);
                    //Return the auction rooms to the requesting client
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), null, serializedAuction.getBytes());
                }
                catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                }
            };
            //Await calls and run a callback on arrival.
            channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> {
            }));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAuction(String auctionRoomId){
        return brokerToClientGateway.requestAuction(auctionRoomId);
    }

    private String getAuctionRooms() {
        return brokerToClientGateway.requestAuctionRooms();
    }
}
