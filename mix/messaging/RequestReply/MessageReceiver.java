package messaging.RequestReply;

import Classes.CallBack;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class MessageReceiver {

    private Channel channel;

    public MessageReceiver(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates a queue and listens on this queue for any incoming messages.
     * @param queueName queue name to listen on.
     * @param callBack interface to call when a messaage was received.
     */
    public void createQueue(String queueName, CallBack callBack){
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody());
                System.out.println(" [x] Received '" + message + "'");
                callBack.returnMessage(message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
