package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Observable;
import java.util.Observer;

public class HistoryUploader extends Service implements Observer {

    public static boolean isRunning = false;
    private Activity activity;
    private HistoryToArrayConverter historyToArrayConverter;

    private boolean canGetData = false;

    //TODO add firebase to this
    //Set some firebase stuff in here
    public HistoryUploader(Activity activity) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Request history here
        //TODO FIND A WAY TO PASS IN THE ACTIVITY WHEN STARTING THE SERVICE
        historyToArrayConverter = new HistoryToArrayConverter(activity);
        Thread thread = new Thread(new MyThread());
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void update(Observable observable, Object o) {
        canGetData = true;
    }

    final class MyThread implements Runnable{

        @Override
        public void run() {
            synchronized (this){
                isRunning = true;
                try{
                    historyToArrayConverter.requestHistory();
                    wait(60000 * 5);
                    if(canGetData){
                        long[] data = historyToArrayConverter.getData();
                        //TODO STORE DATA IN FIREBASE
                        canGetData = false;
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
