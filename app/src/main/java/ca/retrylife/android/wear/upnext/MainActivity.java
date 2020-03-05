package ca.retrylife.android.wear.upnext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ca.retrylife.frc.tba.nightbotparser.NextMatch;
import ca.retrylife.simplelogger.SimpleLogger;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private NextMatch match;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    SimpleLogger.log("MainApp", "Updating data");
                    match.refresh();
                    updateMatchInfo();
                }
            }).start();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        // Set up match
        match = new NextMatch(5024);
//        match.test = true;

        // Set up runner
        task.run();
        timer.scheduleAtFixedRate(task, 0, 50000);
    }




    private void updateMatchInfo(){

        if(match.hasMatch()) {

            // Get data
            String matchCode = match.getMatchString();
            int[] timeDat = match.getTime();
            String matchTimeLeft = String.format("%d:%d", (int)Math.copySign(timeDat[0], timeDat[1]), (int)Math.abs(timeDat[1]));

            // Set text
            ((TextView) findViewById(R.id.match_time_to_play)).setText(matchTimeLeft);
            ((TextView) findViewById(R.id.match_next_code)).setText(matchCode);
        }else{
            SimpleLogger.log("MainApp", "No match");

            // Set text
            ((TextView) findViewById(R.id.match_time_to_play)).setText("");
            ((TextView) findViewById(R.id.match_next_code)).setText("No Match");
        }

    }
}
