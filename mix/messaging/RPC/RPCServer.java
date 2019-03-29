package messaging.RPC;

import AuctionBroker.Backend.AuctionBroker;
import AuctionBroker.Backend.BrokerToClientGateway;
import Classes.AuctionRoom;
import Classes.CallBack;
import Serializer.AuctionRoomSerializationHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class RPCServer {

    private Connection connection;

    public RPCServer(){
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

    public void setup(String queueName, CallBack callBack){
        try{
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queuePurge(queueName);

            System.out.println(" [x] Awaiting RPC requests for auctionrooms");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    System.out.println("received");
                    String serializedData = callBack.returnMessage(new String(delivery.getBody(), "UTF-8"));
                    System.out.println(serializedData);
                    //Return the auction rooms to the requesting client
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), null, serializedData.getBytes());
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
}
