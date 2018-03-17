package be.eaict.stretchalyzer1;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        Log.d("data", Double.toString(angle.get(0)));
        Log.d("data", Double.toString(angle.get(10)));
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
