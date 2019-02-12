package cse110.ucsd.team20_personalbest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/* Substitute for main activity in AndroidManifest to test just the graph */

// https://github.com/lecho/hellocharts-android
public class GraphTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new GraphFragment()).commit();
        }
    }

}
