package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoneStretchingActivity extends AppCompatActivity {

    List<String> graphValues;
    List<String> graphTimeStamps;

    LineChart lineChart;

    private Map<String , List<String>> bluetoothData = new HashMap<String  , List<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_stretching);

        Intent intent = getIntent();
        bluetoothData = (HashMap<String  , List<String>>)intent.getSerializableExtra("data");
        graphValues = bluetoothData.get("Values");
        graphTimeStamps = bluetoothData.get("TimeStamps");
        Log.d("Values", graphValues.toString());
        Log.d("TimeStamps", graphTimeStamps.toString());


    }
}
