package ee490g.epfl.ch.dwarfsleepy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ee490g.epfl.ch.dwarfsleepy.R;
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalAccelerometerEvent;

public class AbnormalAccelerometerAdapter extends RecyclerView.Adapter {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss aa", Locale.getDefault());
    private List<AbnormalAccelerometerEvent> abnormalAccelerometerEvents;

    public AbnormalAccelerometerAdapter(List<AbnormalAccelerometerEvent> abnormalAccelerometerEvents) {
        this.abnormalAccelerometerEvents = abnormalAccelerometerEvents;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.abnormal_accelerometer_layout, parent, false);
        return new AbnormalAccelerometersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AbnormalAccelerometersViewHolder abnormalAccelerometersViewHolder = (AbnormalAccelerometersViewHolder) holder;
        final AbnormalAccelerometerEvent abnormalAccelerometerEvent = abnormalAccelerometerEvents.get(abnormalAccelerometerEvents.size() - 1 - position);

        String accelerometerValue = String.valueOf(abnormalAccelerometerEvent.getAccelerometerValue()).substring(0, 4);
        String duration = "Duration: " +
                abnormalAccelerometerEvent.getDurationHours() + "h " +
                abnormalAccelerometerEvent.getDurationMinutes() + "m " +
                abnormalAccelerometerEvent.getDurationSeconds() + "s " +
                abnormalAccelerometerEvent.getDurationMilliseconds() + "ms";

        String beginTime = "Begin: " + simpleDateFormat.format(abnormalAccelerometerEvent.getBeginTime());
        String endTime = "End: " + simpleDateFormat.format(abnormalAccelerometerEvent.getEndTime());

        abnormalAccelerometersViewHolder.accelerometerValue.setText(accelerometerValue);
        abnormalAccelerometersViewHolder.duration.setText(duration);
        abnormalAccelerometersViewHolder.beginTime.setText(beginTime);
        abnormalAccelerometersViewHolder.endTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return abnormalAccelerometerEvents.size();
    }

    class AbnormalAccelerometersViewHolder extends RecyclerView.ViewHolder {

        private final TextView accelerometerValue;
        private final TextView duration;
        private final TextView beginTime;
        private final TextView endTime;

        AbnormalAccelerometersViewHolder(View itemView) {
            super(itemView);

            accelerometerValue = itemView.findViewById(R.id.abnormal_accelerometer_value);
            duration = itemView.findViewById(R.id.abnormal_accelerometer_duration);
            beginTime = itemView.findViewById(R.id.abnormal_accelerometer_begin_time);
            endTime = itemView.findViewById(R.id.abnormal_accelerometer_end_time);
        }
    }
}
