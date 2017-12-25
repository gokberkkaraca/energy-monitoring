package ee490g.epfl.ch.dwarfsleepy;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class DayMonitoringActivity extends AppCompatActivity {

    private User user;
    private TextView heartRateTextView;
    private TextView caloriesBurntTextView;

    private XYPlot heartRatePlot;
    private XYPlot accelerometerPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_monitoring);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        setHeartRateView();
        plotHeartRates();
        plotAccelerometer();
    }

    private void plotAccelerometer() {

    }

    private void plotHeartRates() {
        final List<Integer> heartRates = new ArrayList<>();
        final List<Number> timeStamps = new ArrayList<>();

        if (averagedHeartRateDataList.size() < 600)
            for (int i = 0; i < averagedHeartRateDataList.size(); i++) {
                heartRates.add(averagedHeartRateDataList.get(i).getValue().intValue());
                timeStamps.add(i);
            }
        else {
            for (int i = 0; i < 600; i++) {
                heartRates.add(averagedHeartRateDataList.get(i).getValue().intValue());
            }
        }


        XYSeries heartRateSeries =
                new SimpleXYSeries(heartRates, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Heart Rates");

        LineAndPointFormatter heartRateSeriesFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);

        heartRatePlot.addSeries(heartRateSeries, heartRateSeriesFormat);

        heartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(timeStamps.toArray()[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

    }

    private void setHeartRateView() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                if(!averagedHeartRateDataList.isEmpty()) {
                    HeartRateData lastHeartRateData = averagedHeartRateDataList.get(averagedHeartRateDataList.size() - 1);
                    int heartRateValue = (int) lastHeartRateData.getValue().floatValue();
                    heartRateTextView.setText(String.valueOf(heartRateValue));
                }
            }
        };
        handler.postDelayed(r, 0);
    }

    private void initializeViews() {
        heartRateTextView = findViewById(R.id.heartRateTextView);
        caloriesBurntTextView = findViewById(R.id.caloriesBurntTextView);
        heartRatePlot = findViewById(R.id.heartRatePlot);
        accelerometerPlot = findViewById(R.id.accelerometerPlot);
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDashboardActivity(this, user);
    }
}
