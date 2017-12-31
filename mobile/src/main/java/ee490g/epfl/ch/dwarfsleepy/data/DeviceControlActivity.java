/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ee490g.epfl.ch.dwarfsleepy.data;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import com.example.android.sporttrackerwatch.R;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends FragmentActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String HR_POLAR = "HR from Polar Belt";
    public static final String HR_WATCH = "HR from SmartWatch";
    //public static final String AC = "AC from SmartWatch";
    public static final Integer MAX_HR = 200;
    public static final Integer MIN_HR = 40;
    public static final String ACTION_SEND_HEART_RATE = "ACTION_SEND_HEART_RATE";
    public static final String INT_HEART_RATE = "INT_HEART_RATE";
    public static final String INT_AC = "INT_ACCELERATION";


    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    private ImageView mHeartImage;

    private final static int NUMBER_OF_POINTS = 50;


    private XYPlot HeartRatePlot;
    private XYPlot ACPlot;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0));
            }
        }
    };
    private XYplotSeriesList xyPlotSeriesList;

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    private int heartRateWatch = 0;
    private BroadcastReceiver mHeartRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            heartRateWatch = intent.getIntExtra(INT_HEART_RATE,-1);
            Log.d(TAG,"Got intent heartRateWatch: " + heartRateWatch);
            TextView textView = findViewById(R.id.HRwatch_value);
            String textString = String.valueOf(heartRateWatch);
            textView.setText(textString);
        }
    };

    private int ACWatch = 0;
    private BroadcastReceiver mHeartRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ACWatch = intent.getIntExtra(INT_AC,-1);
            Log.d(TAG,"Got intent ACWatch: " + ACWatch);
            TextView textView = findViewById(R.id.ACwatch_value);
            String textString = String.valueOf(ACWatch);
            textView.setText(textString);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        setContentView(R.layout.gatt_services_characteristics);

        //Attach fragment to activity
        MapsFragment mapsFragment = new MapsFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapsFragment);
        fragmentTransaction.commit();

        xyPlotSeriesList = new XYplotSeriesList();


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mHeartImage = findViewById(R.id.heartImage);
            mHeartImage.setImageDrawable(getDrawable(R.mipmap.heart_icon));
        }else {
            HeartRatePlot = findViewById(R.id.HRplot);
            configurePlot();
            HeartRatePlot.setVisibility(View.VISIBLE);


            LineAndPointFormatter formatterPolar = new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT, null);
            formatterPolar.getLinePaint().setStrokeWidth(8);
            xyPlotSeriesList.initializeSeriesAndAddToList(HR_POLAR,MIN_HR,NUMBER_OF_POINTS,formatterPolar);

            LineAndPointFormatter formatterWatch = new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT, null);
            formatterWatch.getLinePaint().setStrokeWidth(8);
            xyPlotSeriesList.initializeSeriesAndAddToList(HR_WATCH,MIN_HR,NUMBER_OF_POINTS,formatterWatch);

            XYSeries HRseries = new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList(HR_POLAR), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, HR_POLAR);

            HeartRatePlot.clear();
            HeartRatePlot.addSeries(HRseries, formatterPolar);
            HeartRatePlot.redraw();
        }

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        //Receive HR from watch
        LocalBroadcastManager.getInstance(this).registerReceiver(mHeartRateReceiver,
                new IntentFilter(ACTION_SEND_HEART_RATE));

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(Integer data) {
        if (data != null) {
            mDataField.setText(data.toString());
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                runAnimation();
            }else{

                xyPlotSeriesList.updateSeries(HR_POLAR,data);
                XYSeries HRseries = new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList(HR_POLAR), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, HR_POLAR);
                LineAndPointFormatter formatterPolar = xyPlotSeriesList.getFormatterFromList(HR_POLAR);

                xyPlotSeriesList.updateSeries(HR_WATCH,heartRateWatch);
                XYSeries HRwatchSeries = new SimpleXYSeries(xyPlotSeriesList.getSeriesFromList(HR_WATCH), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, HR_WATCH);
                LineAndPointFormatter formatterWatch = xyPlotSeriesList.getFormatterFromList(HR_WATCH);

                HeartRatePlot.clear();
                HeartRatePlot.addSeries(HRseries, formatterPolar);
                HeartRatePlot.addSeries(HRwatchSeries,formatterWatch);
                HeartRatePlot.redraw();
            }
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();

            //find heart rate service (0x180D)
            if(SampleGattAttributes.lookup(uuid,"unknown").equals("Heart Rate Service")){
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                //loops through available Characteristics
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();

                    //find heart rate measurement (0x2A37)
                    if(SampleGattAttributes.lookup(uuid,"unknown").equals("Heart Rate Measurement")){
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
                    }
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void runAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale);
        mHeartImage.clearAnimation();
        mHeartImage.startAnimation(animation);
    }

    private void configurePlot() {
        // Set background colors
        HeartRatePlot.setBorderPaint(null);
        HeartRatePlot.setBackgroundPaint(null);
        HeartRatePlot.getGraph().getBackgroundPaint().setColor(Color.BLACK);
        HeartRatePlot.getGraph().setGridBackgroundPaint(null);

        // Set the grid and subgrid color
        HeartRatePlot.getGraph().getRangeGridLinePaint().setColor(Color.GRAY);
        HeartRatePlot.getGraph().getDomainGridLinePaint().setColor(Color.GRAY);

        // Set the axes colors
        HeartRatePlot.getGraph().getRangeOriginLinePaint().setColor(Color.GRAY);
        HeartRatePlot.getGraph().getDomainOriginLinePaint().setColor(Color.GRAY);

        // Set the XY axis
        HeartRatePlot.setRangeBoundaries(MIN_HR,MAX_HR, BoundaryMode.FIXED);
        HeartRatePlot.setDomainBoundaries(0,NUMBER_OF_POINTS-1,BoundaryMode.FIXED);
        HeartRatePlot.setRangeStepValue(9); // 9 values 40 60 ... 200
        HeartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#")); //This line is to force the Axis to be integer
        HeartRatePlot.setRangeLabel(getString(R.string.heart_rate));
    }
    
}
