package com.example.system_for_safe_road;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    Context context;
    List<TrackModel> trackModels;

    public TrackAdapter(Context context, List<TrackModel> trackModels) {
        this.context = context;
        this.trackModels = trackModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.track_table_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if( trackModels!=null && trackModels.size()>0){
            TrackModel model = trackModels.get(position);
            holder.address_id.setText(model.getAddress());
            holder.predicted_time_id.setText(model.getPredicted_time());
            holder.arrival_time_id.setText(model.getArrival_time());
            holder.late_not.setText(model.getLate_not());
        }
        else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return trackModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address_id, predicted_time_id, arrival_time_id, late_not;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address_id = itemView.findViewById(R.id.address_id);
            predicted_time_id = itemView.findViewById(R.id.predicted_time_id);
            arrival_time_id = itemView.findViewById(R.id.arrival_time_id);
            late_not = itemView.findViewById(R.id.late_not);

        }
    }
}
