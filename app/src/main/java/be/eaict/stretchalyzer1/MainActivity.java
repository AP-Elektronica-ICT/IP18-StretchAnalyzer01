package be.eaict.stretchalyzer1;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LineChart lineChart;

    private String line;
    private ArrayList<String> x = new ArrayList<>();
    private ArrayList<String> y = new ArrayList<>();
    private ArrayList<String> z = new ArrayList<>();
    private ArrayList<String> mSec = new ArrayList<>();
    private ArrayList<Double> aX = new ArrayList<>();
    private ArrayList<Double> aY = new ArrayList<>();
    private ArrayList<Double> aZ = new ArrayList<>();
    private ArrayList<Double> sec = new ArrayList<>();
    private ArrayList<Double> angle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            InputStream streamX = getAssets().open("Ax.txt");
            InputStream streamY = getAssets().open("Ay.txt");
            InputStream streamZ = getAssets().open("Az.txt");
            InputStream streamSec = getAssets().open("sec.txt");

            BufferedReader readerX = new BufferedReader(new InputStreamReader(streamX));
            BufferedReader readerY = new BufferedReader(new InputStreamReader(streamY));
            BufferedReader readerZ = new BufferedReader(new InputStreamReader(streamZ));
            BufferedReader readerSec = new BufferedReader(new InputStreamReader(streamSec));

            while((line = readerX.readLine()) != null)
                x.add(line);
            while((line = readerY.readLine()) != null)
                y.add(line);
            while((line = readerZ.readLine()) != null)
                z.add(line);
            while((line = readerSec.readLine()) != null)
                mSec.add(line);
            CalculateData();
        }
        catch (IOException e){
            e.printStackTrace();
        }


        lineChart = (LineChart) findViewById(R.id.lineChart);

        //ArrayList<String> xAXES = new ArrayList<>();
        //ArrayList<Entry> yAXESsin = new ArrayList<>();
        ArrayList<Entry> yAXES = new ArrayList<>();
        double x = 0 ;
        int numDataPoints = 1000;

        for(int i=0;i<angle.size();i++){
            //float sinFunction = Float.parseFloat(String.valueOf(Math.sin(x)));
            //float cosFunction = Float.parseFloat(String.valueOf(Math.cos(x)));
            //x = x + 0.1;
            //yAXESsin.add(new Entry(sinFunction,i));
            yAXES.add(new Entry(Float.parseFloat(String.valueOf(angle.get(i))),i));
            //xAXES.add(i, String.valueOf(x));
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

    public void CalculateData(){

        for (String data: x) {
            Double tempX = Math.round((0.1475*(Double.parseDouble(data)) - 48.729) * 10000d) / 10000d;
            Double minLimit;
            if(tempX > -9.81)
                minLimit = tempX;
            else
                minLimit = -9.81;
            aX.add(minLimit);

            tempX = Math.round((Math.asin(minLimit/9.81)/Math.PI * 180) * 1000000d) / 1000000d;
            angle.add(tempX);
        }
        for (String data: y) {
            Double tempY = Math.round((0.1474*(Double.parseDouble(data)) - 48.462) * 10000d) / 10000d;
            aY.add(tempY);
        }
        for (String data: z) {
            Double tempZ = Math.round((0.1486*(Double.parseDouble(data)) - 49.591) * 10000d) / 10000d;
            aZ.add(tempZ);
        }
        for (String data: mSec) {
            Double tempSec = (Double.parseDouble(data))/1000;
            sec.add(tempSec);
        }

    }
}
