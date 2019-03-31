package messaging.RPC;

import Classes.CallBack;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class RPCClient {

    private Channel channel;

    public RPCClient(){
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

    /**
     * calls a method on the server
     * @param requestQueueName
     * @param callBack
     */
    public void call(String requestQueueName, CallBack callBack){
        call(requestQueueName, callBack, "");
    }

    /**
     * calls a method on the server.
     * @param requestQueueName
     * @param callBack
     * @param message
     */
    public void call(String requestQueueName, CallBack callBack, String message){
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
                String serializedRooms = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + serializedRooms + "'");
                callBack.returnMessage(serializedRooms);
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
