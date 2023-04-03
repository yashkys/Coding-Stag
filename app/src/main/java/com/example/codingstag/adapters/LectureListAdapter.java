package com.example.codingstag.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codingstag.R;
import com.example.codingstag.WebViewActivity;
import com.example.codingstag.model.LectureListModel;

import java.util.ArrayList;

public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.viewHolder>{

    ArrayList<LectureListModel> list;
    Context context;

    public LectureListAdapter(ArrayList<LectureListModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public LectureListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lecture_list_model,parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureListAdapter.viewHolder holder, int position) {
        LectureListModel model = list.get(position);

        holder.lectureNumber.setText(model.getLectureNumber());
        holder.lectureTitle.setText(model.getLectureTitle());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", model.getLectureUrl());
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView lectureNumber;
        TextView lectureTitle;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            lectureTitle = itemView.findViewById(R.id.lecture_title);
            lectureNumber = itemView.findViewById(R.id.lecture_number);
        }
    }
}
