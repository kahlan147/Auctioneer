package AuctionBroker.Backend;

import AuctionClient.Backend.AuctionClient;
import Classes.Auction;
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
    public void messageReceived(String message) {
        int timePassed = Integer.parseInt(message);
        auctionBroker.timePassed(timePassed);
    }
}
