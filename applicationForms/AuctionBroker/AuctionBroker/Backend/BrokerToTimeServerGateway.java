package AuctionBroker.Backend;

import Classes.CallBack;
import Classes.ChannelNames;
import messaging.PublishSubscribe.MessageSubscriber;

/**
 * Created by Niels Verheijen on 20/03/2019.
 */
public class BrokerToTimeServerGateway {

    private MessageSubscriber messageSubscriber;
    private AuctionBroker auctionBroker;

    public BrokerToTimeServerGateway(AuctionBroker auctionBroker){
        this.auctionBroker = auctionBroker;
        messageSubscriber = new MessageSubscriber();
        CallBack callBackTimeReceived = new CallBack() {
            @Override
            public String returnMessage(String message) {
                timeReceived(message);
                return "";
            }
        };
        messageSubscriber.createNewChannel(ChannelNames.TIMEPASSEDCHANNEL, callBackTimeReceived);
    }

    private void timeReceived(String message) {
        int timePassed = Integer.parseInt(message);
        auctionBroker.timePassed(timePassed);
    }
}
