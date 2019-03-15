package cse110.ucsd.team20_personalbest.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import cse110.ucsd.team20_personalbest.chat.ChatAdapter;
import cse110.ucsd.team20_personalbest.chat.ChatAdapterFactory;
import cse110.ucsd.team20_personalbest.graph.GraphManager;
import cse110.ucsd.team20_personalbest.history.ArrayToHistoryConverter;
import cse110.ucsd.team20_personalbest.history.HistoryDownloader;
import cse110.ucsd.team20_personalbest.R;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class GraphMessageActivity extends AppCompatActivity implements Observer {

    String COLLECTION_KEY = "chatlogs";
    String MESSAGES_KEY = "messages";
    String DOCUMENT_KEY;


    private ComboLineColumnChartView chart;
    private GraphManager graphManager;
    private ArrayToHistoryConverter arrayToHistoryConverter;
    private HistoryDownloader historyDownloader;
    private int numCols = 28;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_message);

        chart = findViewById(R.id.chart_month);

        graphManager = new GraphManager(chart, numCols);

        String friendEmail = getIntent().getStringExtra("friendEmail");
        SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
        String myEmail = getIntent().getStringExtra("myEmail");
        if(myEmail == null)
            myEmail = sp.getString("UE",null);
        String factoryKey = this.getIntent().getStringExtra("FACTORY_KEY");
        if(factoryKey == null) factoryKey = "";

        String friendKey = friendEmail.substring(0,friendEmail.indexOf('@'));
        if(myEmail.compareTo(friendKey) >= 0)
            DOCUMENT_KEY = myEmail + friendKey;
        else
            DOCUMENT_KEY =friendKey + myEmail ;

        ChatAdapter fb = ChatAdapterFactory.build(factoryKey, myEmail, COLLECTION_KEY, DOCUMENT_KEY, MESSAGES_KEY);

        EditText messageView = findViewById(R.id.chart_message);
        Button sendButton = findViewById(R.id.chart_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb.sendMessage(messageView.getText().toString(), messageView);
            }
        });

        historyDownloader = new HistoryDownloader(friendEmail);
        arrayToHistoryConverter = new ArrayToHistoryConverter(historyDownloader);
        arrayToHistoryConverter.addObserver(this);
        historyDownloader.requestData();




    }

    @Override
    public void update(Observable observable, Object o) {
        //Will update when arraytohistoryconverter gets the data back from historydownloader
        graphManager.updateUnintendedData(arrayToHistoryConverter.getUnintendedSteps());
        graphManager.updateIntendedData(arrayToHistoryConverter.getIntendedSteps());
        ArrayList<Integer> goalData = new ArrayList<Integer>();
        goalData.add((int)arrayToHistoryConverter.getGoal());
        graphManager.updateGoalData(goalData);
        graphManager.draw();
    }
}
