package ee490g.epfl.ch.dwarfsleepy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ee490g.epfl.ch.dwarfsleepy.R;
import ee490g.epfl.ch.dwarfsleepy.models.HeartRateData;

/**
 * Created by Dell on 12/31/2017.
 */

public class SleepAnalysesAdapter extends RecyclerView.Adapter{

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private ArrayList<List<HeartRateData>> nightHeartRatesData;

    public SleepAnalysesAdapter(ArrayList<List<HeartRateData>> nightHeartRatesData) {
        this.nightHeartRatesData = nightHeartRatesData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sleep_analyses_layout, parent, false);
        return new SleepAnalysesAdapter.SleepAnaysesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SleepAnalysesAdapter.SleepAnaysesViewHolder sleepAnalysesViewHolder = (SleepAnalysesAdapter.SleepAnaysesViewHolder) holder;
        final List<HeartRateData> heartRates = nightHeartRatesData.get(nightHeartRatesData.size() - 1 - position);
        int averageHeartRate = 0;

        for(HeartRateData heartRate: heartRates) {
            averageHeartRate += heartRate.getValue();
        }

        averageHeartRate /= heartRates.size();

        if (averageHeartRate > 50) {
            sleepAnalysesViewHolder.sleepTypeImageView.setImageResource(R.drawable.awake);
        } else if (averageHeartRate > 43 && averageHeartRate <= 50) {
            sleepAnalysesViewHolder.sleepTypeImageView.setImageResource(R.drawable.lightsleep);
        } else {
            sleepAnalysesViewHolder.sleepTypeImageView.setImageResource(R.drawable.deepsleep);
        }

        sleepAnalysesViewHolder.dateTextView.setText(simpleDateFormat.format(heartRates.get(0).getDate()));
    }

    @Override
    public int getItemCount() {
        return nightHeartRatesData.size();
    }

    class SleepAnaysesViewHolder extends RecyclerView.ViewHolder {

        private final ImageView sleepTypeImageView;
        private final TextView dateTextView;

        SleepAnaysesViewHolder(View itemView) {
            super(itemView);

            sleepTypeImageView = itemView.findViewById(R.id.sleepTypeImage);
            dateTextView = itemView.findViewById(R.id.sleepDateTextView);
        }
    }
}
