package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Observable;
import java.util.Observer;

public class HistoryUploader implements Observer {

    public static boolean isRunning = false;
    private Activity activity;
    private HistoryToArrayConverter historyToArrayConverter;

    private boolean receivedIntended = false;
    private boolean receivedUnintended = false;

    private boolean canGetData = false;

    //TODO add firebase to this
    //Set some firebase stuff in here
    public HistoryUploader(Activity activity) {
        this.activity = activity;
        historyToArrayConverter = new HistoryToArrayConverter(activity);
    }

    @Override
    public void update(Observable observable, Object o) {

        if(o.equals("intended")){
            receivedIntended = true;
        }
        if(o.equals("unintended")){
            receivedUnintended = true;
        }

        if(receivedIntended && receivedUnintended){
            //UPLOAD HERE
            long[] data = historyToArrayConverter.getData();
        }

    }

}
