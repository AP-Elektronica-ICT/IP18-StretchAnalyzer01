package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    private Button stopExercise;
    private ImageView profileSettings;
    private String userID;
    //Hier komt iets met een bluetooth connectie
//    private List<List<String>> bluetoothData = new ArrayList<List<String>>();
    private Map<String , List<String>> bluetoothMapData = new HashMap<String  , List<String>>();

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
//        List<String> timeStamps = new ArrayList<String>();
//        List<String> values = new ArrayList<String>();
//        for (int i = 0; i < 10; i++) {
//            timeStamps.add(String.valueOf(i));
//
//        }
//        for (int j = 0; j < 10; j++){
//            values.add(String.valueOf((j+5)));
//        }
//
//        bluetoothMapData.put("TimeStamps", timeStamps);
//        bluetoothMapData.put("Values", values);
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

    }

    protected  void WriteToDatabase(){
        long epochDate = System.currentTimeMillis();
        Date date = new Date(epochDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy HH:mm");
        myRef = database.getReference(userID).child(formatter.format(date));
        myRef.setValue(bluetoothMapData);
    }

//    protected void ReadDatabase(){
//        long epochDate = System.currentTimeMillis();
//        Date date = new Date(epochDate);
//        myRef = database.getReference(userID).child(date.toString());
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
}
