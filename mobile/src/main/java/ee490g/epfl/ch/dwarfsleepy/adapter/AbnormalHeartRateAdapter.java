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
import ee490g.epfl.ch.dwarfsleepy.models.AbnormalHeartRateEvent;

import static ee490g.epfl.ch.dwarfsleepy.data.DataHolder.physicalActivities;

public class AbnormalHeartRateAdapter extends RecyclerView.Adapter<AbnormalHeartRateAdapter.AbnormalHeartRatesViewHolder> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss aa", Locale.getDefault());
    private List<AbnormalHeartRateEvent> abnormalHeartRateEvents;
    private List<Boolean> isExercisesList;

    public AbnormalHeartRateAdapter(List<AbnormalHeartRateEvent> abnormalHeartRateEvents) {
        this.abnormalHeartRateEvents = abnormalHeartRateEvents;

        isExercisesList = new ArrayList<>();
        for (AbnormalHeartRateEvent abnormalHeartRateEvent: abnormalHeartRateEvents) {
            isExercisesList.add(abnormalHeartRateEvent.isExercise(physicalActivities));
        }
    }

    @Override
    public AbnormalHeartRatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.abnormal_heart_rate_layout, parent, false);
        return new AbnormalHeartRatesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AbnormalHeartRatesViewHolder holder, int position) {
        if (isExercisesList.size() != abnormalHeartRateEvents.size()) {
            for (int i = abnormalHeartRateEvents.size() - isExercisesList.size(); i >= 1; i--)
                isExercisesList.add(abnormalHeartRateEvents.get(abnormalHeartRateEvents.size() - i).isExercise(physicalActivities));
        }

        final AbnormalHeartRateEvent abnormalHeartRateEvent = abnormalHeartRateEvents.get(abnormalHeartRateEvents.size() - 1 - position);
        final boolean isExercise = isExercisesList.get(isExercisesList.size() - 1 - position);

        String heartRateValue = String.valueOf(abnormalHeartRateEvent.getAverageHeartRateValue().intValue());
        String duration = "Duration: " +
                abnormalHeartRateEvent.getDurationHours() + "h " +
                abnormalHeartRateEvent.getDurationMinutes() + "m " +
                abnormalHeartRateEvent.getDurationSeconds() + "s " +
                abnormalHeartRateEvent.getDurationMilliseconds() + "ms";
        String beginTime = "Begin: " + simpleDateFormat.format(abnormalHeartRateEvent.getBeginTime());
        String endTime = "End: " + simpleDateFormat.format(abnormalHeartRateEvent.getEndTime());

        holder.heartRateValue.setText(heartRateValue);
        holder.duration.setText(duration);
        holder.beginTime.setText(beginTime);
        holder.endTime.setText(endTime);

        if (isExercise) {
            holder.heartRateImage.setImageResource(R.drawable.heart_exercise);
        }
        else {
            holder.heartRateImage.setImageResource(R.drawable.heart_attack);
        }
    }

    @Override
    public int getItemCount() {
        return abnormalHeartRateEvents.size();
    }

    class AbnormalHeartRatesViewHolder extends RecyclerView.ViewHolder {

        private final ImageView heartRateImage;
        private final TextView heartRateValue;
        private final TextView duration;
        private final TextView beginTime;
        private final TextView endTime;

        AbnormalHeartRatesViewHolder(View itemView) {
            super(itemView);

            heartRateImage = itemView.findViewById(R.id.heartAttackImage);
            heartRateValue = itemView.findViewById(R.id.abnormal_hr_value);
            duration = itemView.findViewById(R.id.abnormal_hr_duration);
            beginTime = itemView.findViewById(R.id.abnormal_hr_begin_time);
            endTime = itemView.findViewById(R.id.abnormal_hr_end_time);
        }
    }
}
