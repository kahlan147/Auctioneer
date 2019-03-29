package messaging;

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

    private IMessageReceiver messageReceiver;

    private static String QUEUE_NAME;


    public MessageReceiver(String queueName, IMessageReceiver messageReceiver){
        QUEUE_NAME = queueName;
        this.messageReceiver = messageReceiver;
        setup();
    }

    private void setup(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody());
                System.out.println(" [x] Received '" + message + "'");
                messageReceiver.messageReceived(message);
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }
        catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }
}
