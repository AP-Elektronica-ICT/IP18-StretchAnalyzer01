package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class HistoryActivity extends AppCompatActivity {

    BarChart barChart;
    ArrayList<String> dates;
    Random random;
    ArrayList<String> tempBarEntries;
    ArrayList<BarEntry> barEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        tempBarEntries = intent.getStringArrayListExtra("historyData");
        Log.d("arraylist",  tempBarEntries.toString());
        barChart = (BarChart) findViewById(R.id.bargraph);
        createRandomBarGraph("2016/05/05", "2016/06/01");


        Button bttnBack = (Button) findViewById(R.id.buttonBack);
        bttnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHistory = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intentHistory);
            }
        });

    }





    public void createRandomBarGraph(String Date1, String Date2){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date1 = simpleDateFormat.parse(Date1);
            Date date2 = simpleDateFormat.parse(Date2);

            Calendar mDate1 = Calendar.getInstance();
            Calendar mDate2 = Calendar.getInstance();
            mDate1.clear();
            mDate2.clear();

            mDate1.setTime(date1);
            mDate2.setTime(date2);

            dates = new ArrayList<>();
            //dates = getList(mDate1,mDate2);

            barEntries = new ArrayList<>();


            /*barEntries.add(new BarEntry(-50,1));
            barEntries.add(new BarEntry(-170,2));
            barEntries.add(new BarEntry(175,3));
            barEntries.add(new BarEntry(-78,4));
            barEntries.add(new BarEntry(169,5));
            barEntries.add(new BarEntry(139,6));
            barEntries.add(new BarEntry(-84,7));
            */
            for(int j = 0; j< tempBarEntries.size();j++){
                double tempDoubles = Double.parseDouble(tempBarEntries.get(j));
                int tempAngle = (int) tempDoubles;
                barEntries.add(new BarEntry(tempAngle,j));
                dates.add(String.valueOf(j));
            }
        }catch(ParseException e){
            e.printStackTrace();
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Values");
        BarData barData = new BarData(dates,barDataSet);
        barChart.setData(barData);

    }

    public ArrayList<String> getList(Calendar startDate, Calendar endDate){
        ArrayList<String> list = new ArrayList<String>();
        while(startDate.compareTo(endDate)<=0){
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_MONTH,1);
        }
        return list;
    }

    public String getDate(Calendar cld){
        String curDate = cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                +cld.get(Calendar.DAY_OF_MONTH);
        try{
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(curDate);
            curDate =  new SimpleDateFormat("yyy/MM/dd").format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return curDate;
    }
}
