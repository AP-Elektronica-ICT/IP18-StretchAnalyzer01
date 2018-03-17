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

    String line;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<String> aX = new ArrayList<>();
        ArrayList<String> aY = new ArrayList<>();
        ArrayList<String> aZ = new ArrayList<>();
        ArrayList<String> sec = new ArrayList<>();

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
                aX.add(line);
            while((line = readerY.readLine()) != null)
                aY.add(line);
            while((line = readerZ.readLine()) != null)
                aZ.add(line);
            while((line = readerSec.readLine()) != null)
                sec.add(line);
        }
        catch (IOException e){
            e.printStackTrace();
        }



    }
}
