package ee490g.epfl.ch.dwarfsleepy;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.plotting.XYPlotSeriesList;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class DayMonitoringActivity extends AppCompatActivity {

    private final int MIN_HR = 40;
    private final int MAX_HR = 160;
    private final int NUMBER_OF_POINTS = 180;

    private User user;
    private TextView heartRateTextView;
    private TextView caloriesBurntTextView;

    private XYPlot heartRatePlot;
    private XYPlot accelerometerPlot;
    private XYPlotSeriesList xyPlotSeriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_monitoring);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        initializeViews();
        setHeartRateView();
        configureHeartRatePlot();
    }

    private void configureHeartRatePlot() {
        heartRatePlot.setRangeBoundaries(MIN_HR, MAX_HR, BoundaryMode.FIXED);
        heartRatePlot.setDomainBoundaries(0, NUMBER_OF_POINTS-1, BoundaryMode.FIXED);
        heartRatePlot.setRangeStepValue(9);
        heartRatePlot.setDomainStepValue(9);

        heartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        heartRatePlot.setRangeLabel("Heart Rate (bpm)");

        xyPlotSeriesList = new XYPlotSeriesList();
        LineAndPointFormatter formatterPolar = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterPolar.getLinePaint().setStrokeWidth(8);

        xyPlotSeriesList.initializeSeriesAndAddToList("Heart Rate" , MIN_HR, NUMBER_OF_POINTS, formatterPolar);

        XYSeries heartRateSeries = new SimpleXYSeries(
                xyPlotSeriesList.getSeriesFromList("Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Heart Rate");

        heartRatePlot.clear();
        heartRatePlot.addSeries(heartRateSeries, formatterPolar);
        heartRatePlot.redraw();
    }

    private void updateHeartRatePlot(int data) {

        xyPlotSeriesList.updateSeries("Heart Rate", data);

        XYSeries heartRateSeries = new
                SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Heart Rate");

        LineAndPointFormatter formatterPolar = xyPlotSeriesList.getFormatterFromList("Heart Rate");

        heartRatePlot.clear();
        heartRatePlot.addSeries(heartRateSeries, formatterPolar);
        heartRatePlot.redraw();
    }

    private void setHeartRateView() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10000);
                if(!averagedHeartRateDataList.isEmpty()) {
                    HeartRateData lastHeartRateData = averagedHeartRateDataList.get(averagedHeartRateDataList.size() - 1);
                    int heartRateValue = (int) lastHeartRateData.getValue().floatValue();
                    heartRateTextView.setText(String.valueOf(heartRateValue));
                }

                for (HeartRateData heartRateData: averagedHeartRateDataList) {
                    Log.v("GRAPH", "Updating graph");
                    updateHeartRatePlot(heartRateData.getValue().intValue());
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
