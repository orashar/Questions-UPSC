package com.orashar.android.questions;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.HomeListViewHolder> {

    ArrayList<QuestionObject> list;
    Context context;

    public static class HomeListViewHolder extends RecyclerView.ViewHolder{

        public TextView questiontv;
        public LinearLayout tagsll;

        public HomeListViewHolder(@NonNull View itemView) {
            super(itemView);

            questiontv = itemView.findViewById(R.id.item_text);
            tagsll = itemView.findViewById(R.id.item_tags_ll);
        }
    }

    public HomeListAdapter(ArrayList<QuestionObject> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.home_list_item, parent, false);
        HomeListViewHolder viewHolder = new HomeListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListViewHolder holder, final int position) {
        holder.questiontv.setText(list.get(position).getQuestion());
        ArrayList<String> tagsList = list.get(position).getTags();
        ArrayList<String> dummy = new ArrayList<>();
        for(String tag : tagsList){
            if(!dummy.contains(tag.trim())) dummy.add(tag.trim());
        }
        tagsList = dummy;
        holder.tagsll.removeAllViews();
        int c = 0;
        for(String tag : tagsList){
            if(tag.replaceAll(" ", "").isEmpty() || c > 3) continue;
            TextView tagtv = new TextView(context);
            tagtv.setText(tag);

            tagtv.setPadding(16, 4, 16, 4);
            LinearLayout.LayoutParams taglp  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 72);
            taglp.setMargins(16, 2, 8, 2);
            tagtv.setBackgroundResource(R.drawable.quesetion_tag_bg);
            tagtv.setLayoutParams(taglp);
            holder.tagsll.addView(tagtv);
            c++;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questionIntent = new Intent(context, QuestionInfoActivity.class);
                questionIntent.putExtra("QID", list.get(position).getQid());
                context.startActivity(questionIntent);
                ((MainActivity) context).overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}