package TimeServer;

import Classes.ChannelNames;
import messaging.PublishSubscribe.MessagePublisher;

/**
 * Created by Niels Verheijen on 20/03/2019.
 */
public class TimeServerGateway {

    private MessagePublisher messagePublisher;

    public TimeServerGateway(){
        messagePublisher = new MessagePublisher();
        messagePublisher.createChannel(ChannelNames.TIMEPASSEDCHANNEL);
    }

    public void timeUpdated(int newTime){
        messagePublisher.SendMessage(ChannelNames.TIMEPASSEDCHANNEL, Integer.toString(newTime));
    }


}
