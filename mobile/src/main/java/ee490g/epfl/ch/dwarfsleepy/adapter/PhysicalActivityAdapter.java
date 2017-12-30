package ee490g.epfl.ch.dwarfsleepy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ee490g.epfl.ch.dwarfsleepy.R;
import ee490g.epfl.ch.dwarfsleepy.models.PhysicalActivity;

public class PhysicalActivityAdapter extends RecyclerView.Adapter {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss aa", Locale.getDefault());
    private List<PhysicalActivity> physicalActivities;

    public PhysicalActivityAdapter(List<PhysicalActivity> physicalActivities) {
        this.physicalActivities = physicalActivities;
    }

    @Override
    public PhysicalActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.physical_activity_layout, parent, false);
        return new PhysicalActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PhysicalActivityViewHolder physicalActivityViewHolder = (PhysicalActivityViewHolder) holder;
        final PhysicalActivity physicalActivity = physicalActivities.get(physicalActivities.size() - 1 - position);

        String duration = "Duration: " +
                physicalActivity.getDurationHours() + "h " +
                physicalActivity.getDurationMinutes() + "m " +
                physicalActivity.getDurationSeconds() + "s " +
                physicalActivity.getDurationMilliseconds() + "ms";

        String beginTime = "Begin: " + simpleDateFormat.format(physicalActivity.getBeginTime());
        String endTime = "End: " + simpleDateFormat.format(physicalActivity.getEndTime());

        physicalActivityViewHolder.duration.setText(duration);
        physicalActivityViewHolder.beginTime.setText(beginTime);
        physicalActivityViewHolder.endTime.setText(endTime);

        if (physicalActivity.getActivityType() == PhysicalActivity.ActivityType.BIKING)
            physicalActivityViewHolder.activityImage.setImageResource(R.drawable.biking);
        else if (physicalActivity.getActivityType() == PhysicalActivity.ActivityType.RUNNING)
            physicalActivityViewHolder.activityImage.setImageResource(R.drawable.running);
        else if (physicalActivity.getActivityType() == PhysicalActivity.ActivityType.WALKING)
            physicalActivityViewHolder.activityImage.setImageResource(R.drawable.walking);
        else
            physicalActivityViewHolder.activityImage.setImageResource(R.drawable.physical_activity);
    }

    @Override
    public int getItemCount() {
        return physicalActivities.size();
    }

    private class PhysicalActivityViewHolder extends RecyclerView.ViewHolder {

        private final ImageView activityImage;
        private final TextView duration;
        private final TextView beginTime;
        private final TextView endTime;

        PhysicalActivityViewHolder(View itemView) {
            super(itemView);

            activityImage = itemView.findViewById(R.id.physicalActivityImage);
            duration = itemView.findViewById(R.id.physicalActivityDuration);
            beginTime = itemView.findViewById(R.id.physicalActivityBeginTime);
            endTime = itemView.findViewById(R.id.physicalActivityEndTime);
        }
    }
}
