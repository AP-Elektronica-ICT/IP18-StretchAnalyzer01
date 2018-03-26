package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ExerciseActivity extends AppCompatActivity {

    private Button stopExercise;
    private ImageView profileSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);


        final MediaPlayer instructionSound = MediaPlayer.create(this, R.raw.stretch);
        final ImageView instructionPlay = (ImageView) this.findViewById(R.id.playSound);
        instructionPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionSound.start();
            }
        });

        stopExercise = (Button) findViewById(R.id.bttnStop);
        stopExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        profileSettings = findViewById(R.id.imgSettings);
        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
