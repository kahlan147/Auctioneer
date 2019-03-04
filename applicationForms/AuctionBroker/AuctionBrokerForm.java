import messaging.MessageReceiver;

import javax.swing.*;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class AuctionBrokerForm {
    private JPanel AuctionBrokerFormFrame;

    public static void main(String[] args) {
        JFrame frame = new JFrame("AuctionBrokerForm");
        frame.setContentPane(new AuctionBrokerForm().AuctionBrokerFormFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public AuctionBrokerForm(){
        MessageReceiver messageReceiver = new MessageReceiver();
    }
}
