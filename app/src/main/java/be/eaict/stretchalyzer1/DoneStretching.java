package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DoneStretching extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_stretching);

        Button closeFinal = (Button) this.findViewById(R.id.bttnClose);
        closeFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoneStretching.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
