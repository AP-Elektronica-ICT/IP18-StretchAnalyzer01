package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.sax.StartElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Date;

public class AppsettingsActivity extends AppCompatActivity {
    DatePicker datePicker;
    Button btnapply;
    int day;
    int month;
    int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btnapply = (Button) findViewById(R.id.btnApplyAppSettings);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setSpinnersShown(false);

        btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = datePicker.getDayOfMonth();
                month = datePicker.getMonth();
                year = datePicker.getYear();
                Intent intent = new Intent(AppsettingsActivity.this, MainActivity.class);
                intent.putExtra("flag", "A");
                intent.putExtra("dayOfMonth", day);
                intent.putExtra("monthOfYear", month);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });


    }
}
