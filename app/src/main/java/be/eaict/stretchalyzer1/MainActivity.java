package be.eaict.stretchalyzer1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final int NOTIFICATION_ID = 1;
    TextView timer;
    ImageView profileSettings;
    Notification not;
    Notification.Builder notBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileSettings = findViewById(R.id.imgApplicationSettings);
        timer = findViewById(R.id.TimeLeft);

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        notBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("StretchAlyzer")
                .setContentText("Time to stretch!")
                .setPriority(Notification.PRIORITY_MAX);


        CountDownTimer countDownTimer = new CountDownTimer(12000, 1000){

            public void onTick(long millisUntilFinished){
                timer.setText(String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish(){
                ShowNot();

            }
        }.start();

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

    public void ShowNot(){
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder notBuilder = null;
        NotificationManager notMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel name",
                    NotificationManager.IMPORTANCE_HIGH);
            notMgr.createNotificationChannel(channel);
            notBuilder = new Notification.Builder(this, "channel_id");
        } else {
            notBuilder = new Notification.Builder(this);
        }
        notBuilder
                .setContentTitle("StretchAlyzer")
                .setContentText("Time to stretch !")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);
        Notification not = notBuilder.build();
        notMgr.notify(NOTIFICATION_ID, not);

    }
}
