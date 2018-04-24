package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoneStretchingActivity extends AppCompatActivity {
    List<String> graphValues;
    List<String> graphTimeStamps;
    private Map<String , List<String>> bluetoothData = new HashMap<String  , List<String>>();
    LineChart lineChart;
    private List<Double> sec = new ArrayList<>();
    private List<Double> angle = new ArrayList<>();
    Button close;
    TextView txtReps;
    TextView txtTime;
    Double firstValue;
    Integer totalTime = 0;
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
        txtReps = (TextView) findViewById(R.id.txtReps);
        txtTime = (TextView) findViewById(R.id.txtTime);
        close = (Button) findViewById(R.id.bttnClose);

        ChangeType();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DoneStretchingActivity.this , MainActivity.class);
                startActivity(intent1);
            }
        });



    }

    public void ChangeType(){
        boolean first = true;
        for (String data: graphTimeStamps) {
            if (first) {
                firstValue = (Double.parseDouble(data));
                first = false;
            }
            Double tempSec = ((Double.parseDouble(data)));
            tempSec -= firstValue;
            tempSec = (Math.round(tempSec)/ 1000d);
            sec.add(tempSec);
            totalTime = tempSec.intValue();
        }
        for (String data: graphValues) {
            Double tempAngle = (Double.parseDouble(data));
            angle.add(tempAngle);
        }

        Integer min = totalTime / 60;
        Integer sec = totalTime - (min * 60);
        String totalTimeText = min + "m" + sec;
        txtTime.setText(totalTimeText);
        txtReps.setText(String.valueOf(angle.size()));
        CreateGraph();
    }

    public void CreateGraph(){
        lineChart = (LineChart) findViewById(R.id.lineChart);
        ArrayList<Entry> yAXES = new ArrayList<>();
        double x = 0 ;
        int numDataPoints = 1000;

        for(int i=0;i<angle.size();i++){
            yAXES.add(new Entry(Float.parseFloat(String.valueOf(angle.get(i))),i));
        }
        String[] xaxes = new String[sec.size()];
        for(int i=0; i<sec.size();i++){
            xaxes[i] = sec.get(i).toString();
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet = new LineDataSet(yAXES,"Stretching Progress");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet);

        lineChart.setData(new LineData(xaxes,lineDataSets));

        lineChart.setVisibleXRangeMaximum(2000);
    }
}

