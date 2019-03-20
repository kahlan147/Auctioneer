package TimeServer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Niels Verheijen on 20/03/2019.
 */
public class TimeServer {

    public static void main(String[] args){
        TimeServer timeServer = new TimeServer();
    }

    private TimeServerGateway timeServerGateway;
    private int currentTime;

    public TimeServer(){
        timeServerGateway = new TimeServerGateway();
        currentTime = 0;
        timer();
    }


    private void timer(){
        int secondsTillPing = 1;
        int delay = secondsTillPing*1000;
        int period = secondsTillPing*1000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timePassed(secondsTillPing);
            }
        }, delay, period);
    }

    private void timePassed(int seconds){
        currentTime += seconds;
        System.out.println("Current time is: " + currentTime);
        timeServerGateway.timeUpdated(currentTime);
    }
}
