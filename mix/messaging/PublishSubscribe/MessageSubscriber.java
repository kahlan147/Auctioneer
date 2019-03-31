package messaging.PublishSubscribe;

import Classes.CallBack;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class MessageSubscriber {

    private Channel channel;

    public MessageSubscriber(){
        setup();
    }

    /**
     * main createQueue, creates a channel.
     */
    private void setup(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates an exchange and listens on this.
     * @param channelName
     * @param callBack
     */
    public void createExchange(String channelName, CallBack callBack){
        try {
            channel.exchangeDeclare(channelName, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, channelName, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                callBack.returnMessage(message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
