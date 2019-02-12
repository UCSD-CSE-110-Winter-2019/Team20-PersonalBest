package cse110.ucsd.team20_personalbest;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;

public interface SessionInterface {

    public boolean startSession(long startTime);
    public boolean endSession(long endTime);
    public Session getSession();

}
