package ee490g.epfl.ch.dwarfsleepy;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DecimalFormat;

import ee490g.epfl.ch.dwarfsleepy.data.DataHolder;
import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.plotting.XYPlotSeriesList;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;

public class DayMonitoringActivity extends AppCompatActivity implements View.OnClickListener {

    private final int MIN_HR = 40;
    private final int MAX_HR = 160;
    private final int NUMBER_OF_POINTS = 180;

    private ImageButton heartRateButton;
    private ImageButton accelerometerButton;
    private ImageButton physicalActivityButton;

    private User user;
    private TextView heartRateTextView;
    private TextView accelerometerTextView;
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

        heartRateButton.setOnClickListener(this);
        accelerometerButton.setOnClickListener(this);
        physicalActivityButton.setOnClickListener(this);

        updateViewsAndPlots();
        configureHeartRatePlot();
    }

    private void configureHeartRatePlot() {
        heartRatePlot.setRangeBoundaries(MIN_HR, MAX_HR, BoundaryMode.FIXED);
        heartRatePlot.setDomainBoundaries(0, NUMBER_OF_POINTS - 1, BoundaryMode.FIXED);
        heartRatePlot.setRangeStepValue(9);
        heartRatePlot.setDomainStepValue(9);

        heartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        heartRatePlot.setRangeLabel("Heart Rate (bpm)");

        xyPlotSeriesList = new XYPlotSeriesList();
        LineAndPointFormatter formatterHeartRate = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterHeartRate.getLinePaint().setStrokeWidth(8);

        xyPlotSeriesList.initializeSeriesAndAddToList("Heart Rate", MIN_HR, NUMBER_OF_POINTS, formatterHeartRate);

        XYSeries heartRateSeries = new SimpleXYSeries(
                xyPlotSeriesList.getSeriesFromList("Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Heart Rate");

        heartRatePlot.clear();
        heartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        heartRatePlot.redraw();
    }

    private void updateHeartRatePlot(int data) {

        xyPlotSeriesList.updateSeries("Heart Rate", data);

        XYSeries heartRateSeries = new
                SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Heart Rate");

        LineAndPointFormatter formatterHeartRate = xyPlotSeriesList.getFormatterFromList("Heart Rate");

        heartRatePlot.clear();
        heartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        heartRatePlot.redraw();
    }

    private void updateViewsAndPlots() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10000);

                // Heart Rate
                if (!averagedHeartRateDataList.isEmpty()) {
                    HeartRateData lastHeartRateData = averagedHeartRateDataList.get(averagedHeartRateDataList.size() - 1);
                    int heartRateValue = (int) lastHeartRateData.getValue().floatValue();
                    heartRateTextView.setText(String.valueOf(heartRateValue));
                }

                for (int i = 0; i < averagedHeartRateDataList.size(); i++) {
                    updateHeartRatePlot(averagedHeartRateDataList.get(i).getValue().intValue());
                }

                // Calories
                caloriesBurntTextView.setText(String.valueOf(DataHolder.totalCaloriesBurnedDuringDay));

                // Accelerometer

                if(!averagedAccelerometerData.isEmpty()) {
                    AccelerometerData lastAccelerometerData = averagedAccelerometerData.get(averagedAccelerometerData.size() -1);
                    String xAxisValue = lastAccelerometerData.getXAxisValue().toString().substring(0,4);
                    String yAxisValue = lastAccelerometerData.getYAxisValue().toString().substring(0,4);
                    String zAxisValue = lastAccelerometerData.getZAxisValue().toString().substring(0,4);

                    String resultingText =
                            "x: " + xAxisValue + "\n" +
                            "y: " + yAxisValue + "\n" +
                            "z: " + zAxisValue + "\n";

                    accelerometerTextView.setText(resultingText);
                }


            }
        };
        handler.postDelayed(r, 0);
    }

    private void initializeViews() {
        heartRateTextView = findViewById(R.id.heartRateTextView);
        accelerometerTextView = findViewById(R.id.accelerometerTextView);
        caloriesBurntTextView = findViewById(R.id.caloriesBurntTextView);

        heartRatePlot = findViewById(R.id.heartRatePlot);
        accelerometerPlot = findViewById(R.id.accelerometerPlot);

        heartRateButton = findViewById(R.id.heartButton);
        accelerometerButton = findViewById(R.id.accelerometerButton);
        physicalActivityButton = findViewById(R.id.physicalActivityButton);
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDashboardActivity(this, user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.physicalActivityButton:
                NavigationHandler.goToGoogleFitActivity(this, user);
                break;
            case R.id.heartButton:
                NavigationHandler.goToAbnormalHeartRateActivity(this, user);
                break;
            case R.id.accelerometerButton:
                NavigationHandler.goToAbnormalAccelerometerActivity(this, user);
            default:
                break;
        }
    }
}
