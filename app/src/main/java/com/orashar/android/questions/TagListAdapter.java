package com.orashar.android.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagListViewHolder> {

    ArrayList<String> list;
    Context context;


    public static class TagListViewHolder extends RecyclerView.ViewHolder{

        public TextView tv;

        public TagListViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.item_text);
        }
    }

    public TagListAdapter(ArrayList<String> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TagListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.tag_list_item, parent, false);
        TagListViewHolder viewHolder = new TagListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagListViewHolder holder, final int position) {
        holder.tv.setText(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}