package cse110.ucsd.team20_personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Observable;
import java.util.Observer;

import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class GraphMessageActivity extends AppCompatActivity implements Observer {

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

        //TODO make sure when this activity is called the email is passed in
        String email = getIntent().getStringExtra("email");

        historyDownloader = new HistoryDownloader(email);
        arrayToHistoryConverter = new ArrayToHistoryConverter(historyDownloader);
        arrayToHistoryConverter.addObserver(this);
        historyDownloader.requestData();
    }

    @Override
    public void update(Observable observable, Object o) {
        //Will update when arraytohistoryconverter gets the data back from historydownloader
        graphManager.updateUnintendedData(arrayToHistoryConverter.getUnintendedSteps());
        graphManager.updateIntendedData(arrayToHistoryConverter.getIntendedSteps());
        graphManager.draw();
    }
}
