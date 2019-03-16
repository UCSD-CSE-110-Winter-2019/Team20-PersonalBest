package cse110.ucsd.team20_personalbest.history;

import android.util.Log;

import java.util.ArrayList;

public class HistoryStructure {

    ArrayList<Long> data;

    public HistoryStructure() {
        this.data = new ArrayList<Long>();
    }

    public HistoryStructure setData(long[] d) {
        for(long i : d){
            data.add(i);
        }
        Log.d("HistoryStructure", "History structure built, returning: " + data.toString());
        return this;
    }
}
