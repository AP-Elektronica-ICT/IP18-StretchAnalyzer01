package be.eaict.stretchalyzer1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ExerciseActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    private Button stopExercise;
    private Button btnConnect;
    private ImageView profileSettings;
    private StringBuilder message = new StringBuilder();
    private String userID;
    private Handler mHandler;
    private TextView txt_repsRemaining;
    private String exerciseDate;
    private String timeStamp, value;
    private static int REQUEST_ENABLE_BT = 1;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address;
    BluetoothSocket btSocket;
    BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Map<String , List<String>> bluetoothMapData = new HashMap<String  , List<String>>();

    private int repsRemaining = 5;
    List<String> timeStamps = new ArrayList<String>();
    List<String> values = new ArrayList<String>();
    private StringBuilder dataString = new StringBuilder();
    Thread t1;

    @Override
    public void onStart() {
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userID = currentuser.getUid();
        //WriteToDatabase();
        //ReadDatabase();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        profileSettings = findViewById(R.id.imgSettings);
        stopExercise = (Button) findViewById(R.id.bttnStop);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        txt_repsRemaining = (TextView) findViewById(R.id.txt_Remaining);
        txt_repsRemaining.setText(String.valueOf(repsRemaining));

//        for (int i = 0; i < 10; i++) {
//            timeStamps.add(String.valueOf(i));
//
//        }
//        for (int j = 0; j < 10; j++){
//            values.add(String.valueOf((j+5)));
//        }
//

//        bluetoothData.add(timeStamps);
//        bluetoothData.add(values);

        final MediaPlayer instructionSound = MediaPlayer.create(this, R.raw.stretch);
        final ImageView instructionPlay = (ImageView) this.findViewById(R.id.playSound);
        instructionPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionSound.start();
            }
        });

        stopExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!myBluetoothAdapter.isEnabled()){
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_ENABLE_BT);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnConnect.setVisibility(View.INVISIBLE);
                        btnConnect.setClickable(false);
                    }
                });
                Connect();
            }
        });

        t1 = new Thread(){
            public void run(){
                byte[] buffer = new byte[256];
                int bytes;
                while (repsRemaining > 0) {
                    try {
                        bytes = btSocket.getInputStream().available();
                        if (bytes != 0) {
                            SystemClock.sleep(100);
                            bytes = btSocket.getInputStream().available();
                            bytes = btSocket.getInputStream().read(buffer);

                            String readMessage = new String(buffer, 0, bytes);
                            timeStamp = readMessage;
                            timeStamp = timeStamp.substring(timeStamp.lastIndexOf("#") + 1);
                            timeStamp = timeStamp.substring(0, timeStamp.lastIndexOf("@"));
                            value = readMessage;
                            value = value.substring(value.lastIndexOf("@") + 1);
                            value = value.substring(0, value.lastIndexOf("~"));
                            dataString.delete(0, dataString.length());
                            Log.d("receive", timeStamp);
                            Log.d("receive", value);
                            timeStamps.add(timeStamp);
                            values.add(value);
                            repsRemaining --;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt_repsRemaining.setText(String.valueOf(repsRemaining));
                                }
                            });
                        }
                    } catch (IOException e) {

                    }
                }
                bluetoothMapData.put("TimeStamps", timeStamps);
                bluetoothMapData.put("Values", values);
                WriteToDatabase();

                try {
                    Intent intent = new Intent(ExerciseActivity.this, DoneStretchingActivity.class);
                    intent.putExtra("data", (HashMap<String, List<String>>) bluetoothMapData);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Log.d("Error", e.getMessage());
                }
            }
        };

    }


    protected  void WriteToDatabase(){
        long epochDate = System.currentTimeMillis();
        Date date = new Date(epochDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy HH:mm");
        exerciseDate = formatter.format(date);
        myRef = database.getReference(userID).child(exerciseDate);
        myRef.setValue(bluetoothMapData);
    }

//    protected void ReadDatabase(){
//        long epochDate = System.currentTimeMillis();
//        Date date = new Date(epochDate);
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy HH:mm");
//        myRef = database.getReference(userID).child(formatter.format(date));
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<String> graphValues = (List<String>)dataSnapshot.child("Values").getValue();
//                Log.d("log", graphValues.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void Connect(){
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                if(device.getName().equals("HC-05")){
                    address = device.getAddress();
                    Log.d("Address", address);
                }
            }
        }

        BluetoothDevice HC05 = myBluetoothAdapter.getRemoteDevice(address);
        try {
            btSocket = HC05.createInsecureRfcommSocketToServiceRecord(myUUID);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();//start connection
            Toast toast = Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT );
            Log.d("receive","receive");
            toast.show();
            new CountDownTimer(2000, 100){
                public void onFinish(){
                    //GetData();
                    t1.start();
                }
                public void onTick(long millisUntilFinished){

                }
            }.start();

        } catch (IOException e) {
            //Log.d("Connection", e.toString());
            Toast toast = Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT);
            toast.show();
            btnConnect.setVisibility(View.VISIBLE);
            btnConnect.setClickable(true);
        }
    }

    /*public void GetData(){
        byte[] buffer = new byte[256];
        int bytes;
        while (repsRemaining > 0) {
            try {
                bytes = btSocket.getInputStream().available();
                if (bytes != 0) {
                    SystemClock.sleep(100);
                    bytes = btSocket.getInputStream().available();
                    bytes = btSocket.getInputStream().read(buffer);

                    String readMessage = new String(buffer, 0, bytes);
                    timeStamp = readMessage;
                    timeStamp = timeStamp.substring(timeStamp.lastIndexOf("#") + 1);
                    timeStamp = timeStamp.substring(0, timeStamp.lastIndexOf("@"));
                    value = readMessage;
                    value = value.substring(value.lastIndexOf("@") + 1);
                    value = value.substring(0, value.lastIndexOf("~"));
                    dataString.delete(0, dataString.length());
                    Log.d("receive", timeStamp);
                    Log.d("receive", value);
                    timeStamps.add(timeStamp);
                    values.add(value);
                    repsRemaining --;
                    txt_repsRemaining.setText(String.valueOf(repsRemaining));
                }
            } catch (IOException e) {

            }
        }
        bluetoothMapData.put("TimeStamps", timeStamps);
        bluetoothMapData.put("Values", values);
        WriteToDatabase();

        try {
            Intent intent = new Intent(ExerciseActivity.this, DoneStretchingActivity.class);
            intent.putExtra("data", (HashMap<String, List<String>>) bluetoothMapData);
            startActivity(intent);
        }
        catch (Exception e)
        {
            Log.d("Error", e.getMessage());
        }
    }*/
}

