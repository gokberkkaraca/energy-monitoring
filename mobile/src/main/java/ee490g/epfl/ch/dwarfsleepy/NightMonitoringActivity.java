package ee490g.epfl.ch.dwarfsleepy;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.plotting.XYPlotSeriesList;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightAccelerometerData;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.nightHeartRates;
import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.userWeight;

public class NightMonitoringActivity extends AppCompatActivity implements View.OnClickListener {

    private final int MIN_HR = 30;
    private final int MAX_HR = 160;
    private final int NUM_OF_POINTS_HR = 180;

    private final int MIN_ACC = -35;
    private final int MAX_ACC = 35;
    private final int NUM_OF_POINTS_ACC = 600;

    private ImageButton nightSleepButton;
    private ImageButton caloriesButton;
    private ImageButton polarBeltButton;

    private User user;
    private TextView nightCaloriesBurntTextView;
    private EditText sleepDurationEditText;

    private XYPlot nightHeartRatePlot;
    private XYPlot nightPolarHeartRatePlot;
    private XYPlot nightAccelerometerPlot;
    private XYPlotSeriesList xyPlotSeriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_monitoring);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        user = (User) extras.getSerializable(NavigationHandler.USER);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initializeViews();

        sleepDurationEditText.clearFocus();
        xyPlotSeriesList = new XYPlotSeriesList();

        nightSleepButton.setOnClickListener(this);
        caloriesButton.setOnClickListener(this);
        polarBeltButton.setOnClickListener(this);

        configureHeartRatePlot();
        configureAccelerometerPlot();
        updateViewsAndPlots();
    }

    private void configureHeartRatePlot() {
        nightHeartRatePlot.setRangeBoundaries(MIN_HR, MAX_HR, BoundaryMode.FIXED);
        nightHeartRatePlot.setDomainBoundaries(0, NUM_OF_POINTS_HR - 1, BoundaryMode.FIXED);
        nightHeartRatePlot.setRangeStepValue(9);
        nightHeartRatePlot.setDomainStepValue(9);

        nightHeartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        nightHeartRatePlot.setRangeLabel("Night Heart Rate (bpm)");

        LineAndPointFormatter formatterHeartRate = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterHeartRate.getLinePaint().setStrokeWidth(8);

        xyPlotSeriesList.initializeSeriesAndAddToList("Night Heart Rate", MIN_HR, NUM_OF_POINTS_HR, formatterHeartRate);

        XYSeries heartRateSeries = new SimpleXYSeries(
                xyPlotSeriesList.getSeriesFromList("Night Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Night Heart Rate");

        nightHeartRatePlot.clear();
        nightHeartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        nightHeartRatePlot.redraw();
    }

    private void configurePolarHeartRatePlot() {
        nightPolarHeartRatePlot.setRangeBoundaries(MIN_HR, MAX_HR, BoundaryMode.FIXED);
        nightPolarHeartRatePlot.setDomainBoundaries(0, NUM_OF_POINTS_HR - 1, BoundaryMode.FIXED);
        nightPolarHeartRatePlot.setRangeStepValue(9);
        nightPolarHeartRatePlot.setDomainStepValue(9);

        nightPolarHeartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        nightPolarHeartRatePlot.setRangeLabel("Night Heart Rate (bpm)");

        LineAndPointFormatter formatterHeartRate = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
        formatterHeartRate.getLinePaint().setStrokeWidth(8);

        xyPlotSeriesList.initializeSeriesAndAddToList("Night Heart Rate", MIN_HR, NUM_OF_POINTS_HR, formatterHeartRate);

        XYSeries heartRateSeries = new SimpleXYSeries(
                xyPlotSeriesList.getSeriesFromList("Night Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Night Heart Rate");

        nightPolarHeartRatePlot.clear();
        nightPolarHeartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        nightPolarHeartRatePlot.redraw();
    }

    private void configureAccelerometerPlot() {
        nightAccelerometerPlot.setRangeBoundaries(MIN_ACC, MAX_ACC, BoundaryMode.FIXED);
        nightAccelerometerPlot.setDomainBoundaries(0, NUM_OF_POINTS_ACC - 1, BoundaryMode.FIXED);
        nightAccelerometerPlot.setRangeStepValue(9);
        nightAccelerometerPlot.setDomainStepValue(9);

        nightAccelerometerPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));
        nightAccelerometerPlot.setRangeLabel("Night Accelerometer Value (m/s2)");

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

        nightAccelerometerPlot.clear();
        nightAccelerometerPlot.addSeries(xAxisSeries, formatterXAxis);
        nightAccelerometerPlot.addSeries(yAxisSeries, formatterYAxis);
        nightAccelerometerPlot.addSeries(zAxisSeries, formatterZAxis);
        nightAccelerometerPlot.redraw();
    }

    private void updateHeartRatePlot(int data) {

        xyPlotSeriesList.updateSeries("Night Heart Rate", data);

        XYSeries heartRateSeries = new
                SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Night Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Night Heart Rate");

        LineAndPointFormatter formatterHeartRate = xyPlotSeriesList.getFormatterFromList("Night Heart Rate");

        nightHeartRatePlot.clear();
        nightHeartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        nightHeartRatePlot.redraw();
    }

    private void updatePolarHeartRatePlot(int data) {

        xyPlotSeriesList.updateSeries("Night Heart Rate", data);

        XYSeries heartRateSeries = new
                SimpleXYSeries(xyPlotSeriesList.getSeriesFromList("Night Heart Rate"),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Night Heart Rate");

        LineAndPointFormatter formatterHeartRate = xyPlotSeriesList.getFormatterFromList("Night Heart Rate");

        nightPolarHeartRatePlot.clear();
        nightPolarHeartRatePlot.addSeries(heartRateSeries, formatterHeartRate);
        nightPolarHeartRatePlot.redraw();
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

        nightAccelerometerPlot.clear();
        nightAccelerometerPlot.addSeries(xAxisSeries, formatterXAxis);
        nightAccelerometerPlot.addSeries(yAxisSeries, formatterYAxis);
        nightAccelerometerPlot.addSeries(zAxisSeries, formatterZAxis);
        nightAccelerometerPlot.redraw();
    }


    private void updateViewsAndPlots() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10000);

                //Heart rate

                for (int i = 0; i < nightHeartRates.size(); i++) {
                    for (int j = 0; j < nightHeartRates.get(i).size(); j++) {
                        updateHeartRatePlot(nightHeartRates.get(i).get(j).getValue().intValue());
                    }
                }

                //Polar heart rate
                for (int i = 0; i < nightHeartRates.size(); i++) {
                    for (int j = 0; j < nightHeartRates.get(i).size(); j++) {
                        updatePolarHeartRatePlot(nightHeartRates.get(i).get(j).getValue().intValue());
                    }
                }

                // Accelerometer
                int nr_samples = 0;

                for (int i = nightAccelerometerData.size() - 1; i >= 0; i--) {
                    for (int j = nightAccelerometerData.get(i).size() - 1; j >= 0; j--) {

                        if (nr_samples == 600) {
                            break;
                        }

                        int xValue = nightAccelerometerData.get(i).get(j).getXAxisValue().intValue();
                        int yValue = nightAccelerometerData.get(i).get(j).getYAxisValue().intValue();
                        int zValue = nightAccelerometerData.get(i).get(j).getZAxisValue().intValue();
                        updateAccelerometerPlot(xValue, yValue, zValue);

                        nr_samples += 1;
                    }
                }
            }
        };
        handler.postDelayed(r, 0);
    }

    private void initializeViews() {
        nightCaloriesBurntTextView = findViewById(R.id.nightCaloriesBurntTextView);
        sleepDurationEditText = findViewById(R.id.sleepDurationEditText);

        nightHeartRatePlot = findViewById(R.id.nightHeartRatePlot);
        nightAccelerometerPlot = findViewById(R.id.nightAccelerometerPlot);

        nightSleepButton = findViewById(R.id.nightSleepButton);
        caloriesButton = findViewById(R.id.caloriesButton);
        polarBeltButton = findViewById(R.id.polarBeltButton);
    }

    private void calculateNightBurntCalories() {
        Date currentDate = new Date();
        int lastNightAverageHeartRate = 0;
        int age = currentDate.getYear() - user.getBirthday().getYear();

        if (sleepDurationEditText.getText().toString().equals("") || userWeight == 0) {
            Toast.makeText(this, "Please enter required weight and sleep duration", Toast.LENGTH_SHORT).show();
            return;
        }

        if(nightHeartRates.size() > 0 && nightHeartRates.get(nightHeartRates.size() - 1).get(0).getDate().getDate() != currentDate.getDate()
                && nightHeartRates.get(nightHeartRates.size() - 1).get(0).getDate().getMonth() == currentDate.getMonth()
                && nightHeartRates.get(nightHeartRates.size() - 1).get(0).getDate().getYear() == currentDate.getYear()) {
            Toast.makeText(this, "No data found for the last night!!!", Toast.LENGTH_SHORT).show();
            nightCaloriesBurntTextView.setText("- . -");
            return;
        }

        try {
            int sleepDuration = Integer.parseInt(sleepDurationEditText.getText().toString());

            List<HeartRateData> lastNightHeartRates = nightHeartRates.get(nightHeartRates.size() - 1);

            for(HeartRateData heartRate: lastNightHeartRates) {
                lastNightAverageHeartRate += heartRate.getValue();
            }

            lastNightAverageHeartRate = lastNightAverageHeartRate/lastNightHeartRates.size();

            Log.v("TAG", lastNightAverageHeartRate + " ; " + userWeight + " ; " + age + " ; " + sleepDuration);

            double calories;

            if (user.getGender() == User.Gender.MALE) {
                calories = ((-55.0969 + (0.6309 * lastNightAverageHeartRate) + (0.1988 * userWeight) + (0.2017 * age))/4.184) * 60 * sleepDuration;
            } else {
                calories = ((-20.4022 + (0.4472 * lastNightAverageHeartRate) - (0.1263 * userWeight) + (0.074 * age))/4.184) * 60 * sleepDuration;
            }

            nightCaloriesBurntTextView.setText((int) calories + " cals");
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Weight and sleep duration must be numerals!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        NavigationHandler.goToDashboardActivity(this, user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nightSleepButton:
                NavigationHandler.goToSleepAnalysesActivity(this, user);
                break;
            case R.id.caloriesButton:
                calculateNightBurntCalories();
                break;
            case R.id.polarBeltButton:
                //TODO
                break;
            default:
                break;
        }
    }
}
