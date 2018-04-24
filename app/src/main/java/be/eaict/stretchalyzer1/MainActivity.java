package be.eaict.stretchalyzer1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private List<String> sec = new ArrayList<String>();
    private List<String> angle = new ArrayList<String>();
    private List<List<String>> allAngles = new ArrayList<List<String>>();
    private int day;
    private int month;
    private String monthString;
    private int year;
    private String prevExerciseDate;
    private String query;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference queryRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static final int NOTIFICATION_ID = 1;
    TextView timer;
    ImageView profileSettings;
    ImageView applicationSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        final Intent intent = getIntent();
        if(intent.getStringExtra("flag") != null && intent.getStringExtra("flag") == "A"){
            day = intent.getIntExtra("dayOfMonth", 0);
            month = (intent.getIntExtra("monthOfYear", 0));
            switch (month){
                case 0:
                    monthString = "january";
                    break;
                case 1:
                    monthString = "february";
                    break;
                case 2:
                    monthString = "march";
                    break;
                case 3:
                    monthString = "april";
                    break;
                case 4:
                    monthString = "may";
                    break;
                case 5:
                    monthString = "june";
                    break;
                case 6:
                    monthString = "july";
                    break;
                case 7:
                    monthString = "august";
                    break;
                case 8:
                    monthString = "september";
                    break;
                case 9:
                    monthString = "october";
                    break;
                case 10:
                    monthString = "november";
                    break;
                case 11:
                    monthString = "december";
                    break;
            }
            year = intent.getIntExtra("year", 0);
            query = day+"-"+monthString+"-"+year;
            Log.d("query", query);
            myRef = database.getReference(currentUser.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            for (DataSnapshot d: dataSnapshot.getChildren()){
                                String key = d.getKey();
                                if(key.contains(query)){
                                    allAngles.add((List<String>)d.child("Values").getValue());
                                }
                                if(allAngles.size() > 0)
                                Log.d("valueueu", allAngles.get(0).get(0));
                            }
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

//        if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag") == "B"){
//            prevExerciseDate = intent.getStringExtra("exerDatum");
//        }

        queryRef = database.getReference();
        Query lastQuery = queryRef.child(currentUser.getUid()).orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                sec = (List<String>) d.child("TimeStamps").getValue();
                angle = (List<String>) d.child("Values").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        myRef=database.getReference("kv88idO4A1Sjw7ejKz0b48A9z3F2").child("23-april-2018 14:23");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                angle = (List<String>)(dataSnapshot.child("Values").getValue());
//                sec = (List<String>) dataSnapshot.child("TimeStamps").getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        final CountDownTimer countDownTimer = new CountDownTimer(12000, 1000){

            public void onTick(long millisUntilFinished){
                timer.setText(String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish(){
                ShowNot();

            }
        }.start();

        final Button startNow = (Button) this.findViewById(R.id.ButtonStart);
        startNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                startActivity(intent);
                countDownTimer.cancel();
            }
        });
        
        profileSettings = findViewById(R.id.imgUserSettings);
        timer = findViewById(R.id.TimeLeft);

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        applicationSettings = (ImageView) findViewById(R.id.imgApplicationSettings);
        applicationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppsettingsActivity.class);
                startActivity(intent);
            }
        });



//        try{
//            InputStream streamX = getAssets().open("Ax.txt");
//            InputStream streamY = getAssets().open("Ay.txt");
//            InputStream streamZ = getAssets().open("Az.txt");
//            InputStream streamSec = getAssets().open("sec.txt");

//            BufferedReader readerX = new BufferedReader(new InputStreamReader(streamX));
//            BufferedReader readerY = new BufferedReader(new InputStreamReader(streamY));
//            BufferedReader readerZ = new BufferedReader(new InputStreamReader(streamZ));
//            BufferedReader readerSec = new BufferedReader(new InputStreamReader(streamSec));
//
//            while((line = readerX.readLine()) != null)
//                x.add(line);
//            while((line = readerY.readLine()) != null)
//                y.add(line);
//            while((line = readerZ.readLine()) != null)
//                z.add(line);
//            while((line = readerSec.readLine()) != null)
//                mSec.add(line);
//            //CalculateData();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }


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


//    public void CalculateData(){
//
//        for (String data: x) {
//            Double tempX = Math.round((0.1475*(Double.parseDouble(data)) - 48.729) * 10000d) / 10000d;
//            Double minLimit;
//            if(tempX > -9.81)
//                minLimit = tempX;
//            else
//                minLimit = -9.81;
//            aX.add(minLimit);
//
//            tempX = Math.round((Math.asin(minLimit/9.81)/Math.PI * 180) * 1000000d) / 1000000d;
//            angle.add(tempX);
//        }
//        for (String data: y) {
//            Double tempY = Math.round((0.1474*(Double.parseDouble(data)) - 48.462) * 10000d) / 10000d;
//            aY.add(tempY);
//        }
//        for (String data: z) {
//            Double tempZ = Math.round((0.1486*(Double.parseDouble(data)) - 49.591) * 10000d) / 10000d;
//            aZ.add(tempZ);
//        }
//        for (String data: mSec) {
//            Double tempSec = (Double.parseDouble(data))/1000;
//            sec.add(tempSec);
//        }
//
//    }

    public void ShowNot(){
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder notBuilder = null;
        NotificationManager notMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel name", NotificationManager.IMPORTANCE_HIGH);
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
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX);
        Notification not = notBuilder.build();
        notMgr.notify(NOTIFICATION_ID, not);

    }
}
