package AuctionBroker.Backend;

import Classes.ChannelNames;
import Classes.ISubscriberGateway;
import messaging.PublishSubscribe.MessageSubscriber;

/**
 * Created by Niels Verheijen on 20/03/2019.
 */
public class BrokerToTimeServerGateway implements ISubscriberGateway {

    private MessageSubscriber messageSubscriber;
    private AuctionBroker auctionBroker;

    public BrokerToTimeServerGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        messageSubscriber = new MessageSubscriber(this);
        messageSubscriber.createNewChannel(ChannelNames.TIMEPASSEDCHANNEL);
    }

    @Override
    public void timeReceived(String message) {
        int timePassed = Integer.parseInt(message);
        auctionBroker.timePassed(timePassed);
    }

    @Override
    public void auctionReceived(String message) {
        //Not needed.
    }
}
