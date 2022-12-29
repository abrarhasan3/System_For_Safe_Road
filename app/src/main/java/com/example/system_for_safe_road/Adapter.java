package com.example.system_for_safe_road;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public List<customClassR>data;
    Context context;

    public Adapter(List<customClassR> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_for_recycle,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String flag=data.get(position).getFlag();
        String aTime=data.get(position).getAcctualtime();
        String rTime=data.get(position).getReachedtime();
        String dTime=data.get(position).getDelay();
        int isDelay = data.get(position).getIsDelay();
        holder.setData(flag,aTime,dTime,rTime,isDelay);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView,textView1,textView2,textView3;

        public ImageView imageView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.flagNo);
            textView1=itemView.findViewById(R.id.actualtime);
            textView2=itemView.findViewById(R.id.totaldelay);
            textView3=itemView.findViewById(R.id.reached_time);
            imageView = itemView.findViewById(R.id.status);


        }


        public void setData(String flag,String aTime, String dTime, String rTime,int isDelay ) {
            textView.setText(flag);
            textView1.setText(aTime);
            textView2.setText(dTime);
            textView3.setText(rTime);
            if(isDelay == 0)
            {
                imageView.setImageResource(R.drawable.varified);
            }

        }

    }
}
