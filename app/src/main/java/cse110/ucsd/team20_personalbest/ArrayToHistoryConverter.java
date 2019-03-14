package cse110.ucsd.team20_personalbest;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ArrayToHistoryConverter extends Observable implements Observer {

    private HistoryDownloader historyDownloader;
    private ArrayList<Long> data;

    public ArrayToHistoryConverter(HistoryDownloader historyDownloader){
        this.historyDownloader = historyDownloader;
        historyDownloader.addObserver(this);
    }

    public ArrayList<Integer> getUnintendedSteps(){
        ArrayList<Integer> unintended = new ArrayList<>();
        for(int i = 2; i < 30; i++){
            unintended.add(data.get(i).intValue());
        }
        return unintended;
    }

    public ArrayList<Integer> getIntendedSteps(){
        ArrayList<Integer> intended = new ArrayList<>();
        for(int i = 30; i < 58; i++){
            intended.add(data.get(i).intValue());
        }
        return intended;
    }

    public long getDate(){
        return data.get(0);
    }

    public long getGoal(){
        return data.get(1);
    }

    @Override
    public void update(Observable observable, Object o) {
        this.data = (ArrayList<Long>) o;
        setChanged();
        notifyObservers();
    }
}
