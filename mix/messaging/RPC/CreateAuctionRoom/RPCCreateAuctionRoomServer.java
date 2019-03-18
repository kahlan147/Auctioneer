package messaging.RPC.CreateAuctionRoom;

import AuctionBroker.Backend.AuctionBroker;
import Classes.Auction;
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

    private AuctionBroker auctionBroker;

    public RPCCreateAuctionRoomServer(String queueName, AuctionBroker auctionBroker){
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

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    AuctionRoom auctionRoom = createAuctionRoom(message, delivery.getProperties().getReplyTo());
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

    private AuctionRoom createAuctionRoom(String name, String replyQueue){
        return auctionBroker.createAuctionRoom(name, replyQueue);
    }

}
