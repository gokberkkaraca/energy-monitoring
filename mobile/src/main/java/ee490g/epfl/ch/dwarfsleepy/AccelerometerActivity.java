package ee490g.epfl.ch.dwarfsleepy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;

public class AccelerometerActivity extends AppCompatActivity {

    private LineChart chart;
    private LineDataSet chartDataSetForXAxis;
    private LineDataSet chartDataSetForYAxis;
    private LineDataSet chartDataSetForZAxis;


    private List<AccelerometerData> accelerometerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        chart = (LineChart) findViewById(R.id.chart);
        fetchAccelerometerData();
    }

    private void fetchAccelerometerData() {
        DatabaseHandler.getAccelerometerData(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Entry> xDataEntries = new ArrayList<>();
                List<Entry> yDataEntries = new ArrayList<>();
                List<Entry> zDataEntries = new ArrayList<>();
                List<String> xTags = new ArrayList<>();
                int i = 1;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    xTags.add(Integer.toString(i));
                    AccelerometerData accelerometer = snapshot.getValue(AccelerometerData.class);
                    xDataEntries.add(new Entry(Math.abs(accelerometer.getXAxisValue().floatValue()), i));
                    yDataEntries.add(new Entry(Math.abs(accelerometer.getYAxisValue().floatValue()), i));
                    zDataEntries.add(new Entry(Math.abs(accelerometer.getZAxisValue().floatValue()), i));
                    i += 1;
                }

                LineDataSet chartDataSetForXAxis = new LineDataSet(xDataEntries, "X AXIS");
                chartDataSetForXAxis.setDrawCircles(false);
                chartDataSetForXAxis.setColor(Color.rgb(153, 51, 255));
                LineDataSet chartDataSetForYAxis = new LineDataSet(yDataEntries, "Y AXIS");
                chartDataSetForYAxis.setColor(Color.rgb(0, 153, 51));
                LineDataSet chartDataSetForZAxis = new LineDataSet(zDataEntries, "Z AXIS");
                chartDataSetForZAxis.setColor(Color.rgb(0, 51, 153));

                List<LineDataSet> dataSets = new ArrayList<>();
                dataSets.add(chartDataSetForXAxis);
                dataSets.add(chartDataSetForYAxis);
                dataSets.add(chartDataSetForZAxis);

                LineData lineData = new LineData(xTags, dataSets);
                chart.setData(lineData);
                chart.setDescription("Accelerometer Data");
                chart.animateXY(2000, 2000);
                chart.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AccelerometerActivity.this, "Failed to fetch accelerometer data", Toast.LENGTH_LONG).show();
            }
        });
    }
}
