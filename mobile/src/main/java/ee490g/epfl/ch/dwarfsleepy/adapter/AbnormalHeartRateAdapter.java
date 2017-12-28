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
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;

public class AbnormalHeartRateAdapter extends RecyclerView.Adapter {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss aa", Locale.getDefault());
    private List<AbnormalHeartRateEvent> abnormalHeartRateEvents;

    public AbnormalHeartRateAdapter(List<AbnormalHeartRateEvent> abnormalHeartRateEvents) {
        this.abnormalHeartRateEvents = abnormalHeartRateEvents;
    }

    @Override
    public AbnormalHeartRatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.abnormal_heart_rate_layout, parent, false);
        return new AbnormalHeartRatesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AbnormalHeartRatesViewHolder abnormalHeartRatesViewHolder = (AbnormalHeartRatesViewHolder) holder;
        final AbnormalHeartRateEvent abnormalHeartRateEvent = abnormalHeartRateEvents.get(abnormalHeartRateEvents.size() - 1 - position);

        String heartRateValue = String.valueOf(abnormalHeartRateEvent.getAverageHeartRateValue().intValue());
        String duration = "Duration: " +
                abnormalHeartRateEvent.getDurationHours() + "h " +
                abnormalHeartRateEvent.getDurationMinutes() + "m " +
                abnormalHeartRateEvent.getDurationSeconds() + "s " +
                abnormalHeartRateEvent.getDurationMilliseconds() + "ms";
        String beginTime = "Begin: " + simpleDateFormat.format(abnormalHeartRateEvent.getBeginTime());
        String endTime = "End: " + simpleDateFormat.format(abnormalHeartRateEvent.getEndTime());

        abnormalHeartRatesViewHolder.heartRateValue.setText(heartRateValue);
        abnormalHeartRatesViewHolder.duration.setText(duration);
        abnormalHeartRatesViewHolder.beginTime.setText(beginTime);
        abnormalHeartRatesViewHolder.endTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return abnormalHeartRateEvents.size();
    }

    class AbnormalHeartRatesViewHolder extends RecyclerView.ViewHolder {

        final TextView heartRateValue;
        final TextView duration;
        final TextView beginTime;
        final TextView endTime;

        AbnormalHeartRatesViewHolder(View itemView) {
            super(itemView);

            heartRateValue = itemView.findViewById(R.id.abnormal_hr_value);
            duration = itemView.findViewById(R.id.abnormal_hr_duration);
            beginTime = itemView.findViewById(R.id.abnormal_hr_begin_time);
            endTime = itemView.findViewById(R.id.abnormal_hr_end_time);
        }
    }
}
