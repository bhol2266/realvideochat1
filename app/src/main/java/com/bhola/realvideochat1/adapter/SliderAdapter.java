package com.bhola.realvideochat1.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.ChatScreen_User;
import com.bhola.realvideochat1.Models.UserModel;
import com.bhola.realvideochat1.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.viewholder> {

    Context context;
    ArrayList<UserModel> girllist;


    public SliderAdapter(Context context, ArrayList<UserModel> girllist) {
        this.context = context;
        this.girllist = girllist;
    }

    @androidx.annotation.NonNull
    @Override
    public viewholder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.slider_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onViewRecycled(viewholder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull viewholder holder, int position) {
        UserModel item = girllist.get(position);
        holder.title.setText(item.getFullname());

        if (item.getProfilepic().length() != 0) {
            if (item.getProfilepic().startsWith("http")) {
                Picasso.get().load(item.getProfilepic()).into(holder.thumbnail);
            } else {
                holder.thumbnail.setImageURI(Uri.parse(item.getProfilepic()));
            }
        } else {
            if (item.getSelectedGender().equals("female")) {
                holder.thumbnail.setImageResource(R.drawable.female_logo);
            }
        }


        holder.sliderlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userModelJson = new Gson().toJson(item); // Using Google's Gson library for JSON serialization
                Intent intent = new Intent(view.getContext(), ChatScreen_User.class);
                intent.putExtra("userModelJson", userModelJson);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return girllist.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        LinearLayout sliderlayout;

        public viewholder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageview);
            title = itemView.findViewById(R.id.categorytextview);
            sliderlayout = itemView.findViewById(R.id.sliderlayout);
        }
    }
}
