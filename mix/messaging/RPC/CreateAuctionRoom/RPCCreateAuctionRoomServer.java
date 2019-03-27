package messaging.RPC.CreateAuctionRoom;

import AuctionBroker.Backend.BrokerToOwnerGateway;
import Classes.AuctionRoom;
import Serializer.AuctionRoomSerializationHandler;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class RPCCreateAuctionRoomServer {

    private String RPC_QUEUE_NAME;

    private BrokerToOwnerGateway auctionBroker;

    public RPCCreateAuctionRoomServer(String queueName, BrokerToOwnerGateway auctionBroker){
        this.RPC_QUEUE_NAME = queueName;
        this.auctionBroker = auctionBroker;
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

            System.out.println(" [x] Awaiting RPC requests for creating auction room");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    AuctionRoom auctionRoom = createAuctionRoom(message);
                    AuctionRoomSerializationHandler auctionRoomSerializationHandler = new AuctionRoomSerializationHandler();
                    String auctionRoomMessage = auctionRoomSerializationHandler.serialize(auctionRoom);
                    //Return the auction room to the requesting client
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), null, auctionRoomMessage.getBytes());
                }
                catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
            }));
        }
        catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    private AuctionRoom createAuctionRoom(String name){
        return auctionBroker.createAuctionRoom(name);
    }

}
