package com.bhola.realvideochat1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.Models.StreamerEarningModel;
import com.bhola.realvideochat1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StreamerEarning_Adapter extends RecyclerView.Adapter<StreamerEarning_Adapter.GridViewHolder> {

    public static List<StreamerEarningModel> mlist;
    private final Context context;


    public StreamerEarning_Adapter(Context context, ArrayList<StreamerEarningModel> mlist) {
        this.context = context;
        this.mlist = mlist;
    }


    @androidx.annotation.NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.streamer_earning_recycler_item, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull GridViewHolder holder, int position) {
        StreamerEarningModel item = mlist.get(position);

        // Create a SimpleDateFormat object for parsing the input date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Parse the input date string
        Date date = null;
        try {
            date = inputFormat.parse(item.getDate());
        } catch (ParseException e) {
        }

        // Create a SimpleDateFormat object for formatting the output date
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());
        String outputDateString = outputFormat.format(date);




        Date today = new Date();
        String formattedDateToday = inputFormat.format(today);

        if (formattedDateToday.equals(item.getDate())) {

            holder.date.setText(outputDateString+" (today)");
        } else {
            holder.date.setText(outputDateString);
        }


        holder.coins.setText(String.valueOf(item.getCoins() * 100));

    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }


    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView date, coins;


        public GridViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            coins = itemView.findViewById(R.id.coins);

        }
    }

}






