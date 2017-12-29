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

import ee490g.epfl.ch.dwarfsleepy.models.AccelerometerData;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.plotting.XYPlotSeriesList;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.averagedHeartRateDataList;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.totalCaloriesBurnedDuringDay;

public class DayMonitoringActivity extends AppCompatActivity implements View.OnClickListener {

    private final int MIN_HR = 40;
    private final int MAX_HR = 160;
    private final int NUM_OF_POINTS_HR = 180;

    private final int MIN_ACC = -35;
    private final int MAX_ACC = 35;
    private final int NUM_OF_POINTS_ACC = 600;

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

        xyPlotSeriesList = new XYPlotSeriesList();

        heartRateButton.setOnClickListener(this);
        accelerometerButton.setOnClickListener(this);
        physicalActivityButton.setOnClickListener(this);

        updateViewsAndPlots();
        configureHeartRatePlot();
        configureAccelerometerPlot();
    }

    private void configureHeartRatePlot() {
        heartRatePlot.setRangeBoundaries(MIN_HR, MAX_HR, BoundaryMode.FIXED);
        heartRatePlot.setDomainBoundaries(0, NUM_OF_POINTS_HR - 1, BoundaryMode.FIXED);
        heartRatePlot.setRangeStepValue(9);
        heartRatePlot.setDomainStepValue(9);

        heartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        heartRatePlot.setRangeLabel("Heart Rate (bpm)");

        LineAndPointFormatter formatterHeartRate = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterHeartRate.getLinePaint().setStrokeWidth(8);

        xyPlotSeriesList.initializeSeriesAndAddToList("Heart Rate", MIN_HR, NUM_OF_POINTS_HR, formatterHeartRate);

        XYSeries heartRateSeries = new SimpleXYSeries(
                xyPlotSeriesList.getSeriesFromList("Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Heart Rate");

        heartRatePlot.clear();
        heartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        heartRatePlot.redraw();
    }

    private void configureAccelerometerPlot() {
        accelerometerPlot.setRangeBoundaries(MIN_ACC, MAX_ACC, BoundaryMode.FIXED);
        accelerometerPlot.setDomainBoundaries(0, NUM_OF_POINTS_ACC - 1, BoundaryMode.FIXED);
        accelerometerPlot.setRangeStepValue(9);
        accelerometerPlot.setDomainStepValue(9);

        accelerometerPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        accelerometerPlot.setRangeLabel("Accelerometer Value (m/s2)");

        LineAndPointFormatter formatterXAxis = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterXAxis.getLinePaint().setStrokeWidth(8);

        LineAndPointFormatter formatterYAxis = new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterYAxis.getLinePaint().setStrokeWidth(8);

        LineAndPointFormatter formatterZAxis = new LineAndPointFormatter(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterZAxis.getLinePaint().setStrokeWidth(8);

        xyPlotSeriesList.initializeSeriesAndAddToList("X Axis", MIN_ACC, NUM_OF_POINTS_ACC, formatterXAxis);
        xyPlotSeriesList.initializeSeriesAndAddToList("Y Axis", MIN_ACC, NUM_OF_POINTS_ACC, formatterYAxis);
        xyPlotSeriesList.initializeSeriesAndAddToList("Z Axis", MIN_ACC, NUM_OF_POINTS_ACC, formatterZAxis);

        XYSeries xAxisSeries =
                new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("X Axis"),
                        SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "X Axis");

        XYSeries yAxisSeries =
                new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Y Axis"),
                        SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Y Axis");

        XYSeries zAxisSeries =
                new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Z Axis"),
                        SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Z Axis");

        accelerometerPlot.clear();
        accelerometerPlot.addSeries(xAxisSeries, formatterXAxis);
        accelerometerPlot.addSeries(yAxisSeries, formatterYAxis);
        accelerometerPlot.addSeries(zAxisSeries, formatterZAxis);
        accelerometerPlot.redraw();
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

    private void updateAccelerometerPlot(int xValue, int yValue, int zValue) {
        xyPlotSeriesList.updateSeries("X Axis", xValue);
        xyPlotSeriesList.updateSeries("Y Axis", yValue);
        xyPlotSeriesList.updateSeries("Z Axis", zValue);

        XYSeries xAxisSeries =
                new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("X Axis"),
                        SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "X Axis");

        XYSeries yAxisSeries =
                new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Y Axis"),
                        SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Y Axis");

        XYSeries zAxisSeries =
                new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Z Axis"),
                        SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Z Axis");

        LineAndPointFormatter formatterXAxis = xyPlotSeriesList.getFormatterFromList("X Axis");
        LineAndPointFormatter formatterYAxis = xyPlotSeriesList.getFormatterFromList("Y Axis");
        LineAndPointFormatter formatterZAxis = xyPlotSeriesList.getFormatterFromList("Z Axis");

        accelerometerPlot.clear();
        accelerometerPlot.addSeries(xAxisSeries, formatterXAxis);
        accelerometerPlot.addSeries(yAxisSeries, formatterYAxis);
        accelerometerPlot.addSeries(zAxisSeries, formatterZAxis);
        accelerometerPlot.redraw();
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
                caloriesBurntTextView.setText(String.valueOf(totalCaloriesBurnedDuringDay));

                // Accelerometer

                if (!averagedAccelerometerData.isEmpty()) {
                    AccelerometerData lastAccelerometerData = averagedAccelerometerData.get(averagedAccelerometerData.size() - 1);
                    String xAxisValue = lastAccelerometerData.getXAxisValue().toString().substring(0, 4);
                    String yAxisValue = lastAccelerometerData.getYAxisValue().toString().substring(0, 4);
                    String zAxisValue = lastAccelerometerData.getZAxisValue().toString().substring(0, 4);

                    String resultingText =
                            "x: " + xAxisValue + "\n" +
                                    "y: " + yAxisValue + "\n" +
                                    "z: " + zAxisValue + "\n";

                    accelerometerTextView.setText(resultingText);
                }

                for (int i = averagedAccelerometerData.size() - 600; i < averagedAccelerometerData.size(); i++) {
                    int xValue = averagedAccelerometerData.get(i).getXAxisValue().intValue();
                    int yValue = averagedAccelerometerData.get(i).getYAxisValue().intValue();
                    int zValue = averagedAccelerometerData.get(i).getZAxisValue().intValue();
                    updateAccelerometerPlot(xValue, yValue, zValue);
                }

            }
        };
        handler.postDelayed(r, 0);
    }

    private void initializeViews() {
        heartRateTextView = findViewById(R.id.heartRateTextView);
        accelerometerTextView = findViewById(R.id.accelerometerTextView);
        caloriesBurntTextView = findViewById(R.id.caloriesBurntTextView);
        caloriesBurntTextView.setText(String.valueOf(totalCaloriesBurnedDuringDay));

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
                break;
            default:
                break;
        }
    }
}
