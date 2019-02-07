package cse110.ucsd.team20_personalbest;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;

public class IntendedSession {

    private String fitnessServiceKey = "GOOGLE_FIT";
    public static final String TAG = "BasicSessions";
    public static final String SESSION_NAME = "Intended Walk";
    public static final String SESSION_DESC = "Intended Walk description";
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private FitnessService fitnessService;

    // start tracking steps

    public boolean startSession() {

//       Session session = new Session.Builder()
//                .setName(SESSION_NAME)
//                .setIdentifier("identifier")
//                .setDescription(SESSION_DESC)
//                .setStartTime(startTime.getMillis(), TimeUnit.MILLISECONDS)
//                .build();
//
//        Task<Void> response = Fitness.getSessionsClient(this, googleSigninAccount)
//                .startSession(session);

        return true;
    }

    public boolean endSession() {

//        Task<List<Session>> response = Fitness.getSessionsClient(this, googleSigninAccount)
//                .stopSession(session.getIdentifier());

        return true;
    }


} // end class