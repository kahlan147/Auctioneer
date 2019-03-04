import messaging.MessageSender;

import javax.swing.*;

/**
 * Created by Niels Verheijen on 04/03/2019.
 */
public class AuctionOwnerForm {
    private JPanel AuctionOwnerFormFrame;
    private JButton button1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("AuctionOwnerForm");
        frame.setContentPane(new AuctionOwnerForm().AuctionOwnerFormFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public AuctionOwnerForm(){
        MessageSender messageSender = new MessageSender();
    }
}
