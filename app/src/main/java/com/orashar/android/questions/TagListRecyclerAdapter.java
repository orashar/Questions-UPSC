package com.orashar.android.questions;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.orashar.android.questions.DataFiles.TransactionsContract;

import java.util.ArrayList;
import java.util.Comparator;

import static android.view.View.GONE;

public class TagListRecyclerAdapter extends RecyclerView.Adapter<TagListRecyclerAdapter.TagListViewHolder> {
    ArrayList<HomeTagObject> list;
    Context context;

    public class TagListViewHolder extends RecyclerView.ViewHolder{

        public TextView title, count;
        public EditText et;
        public ImageView deleteBtn;
        public ImageView editBtn;

        public TagListViewHolder(@NonNull View itemView/*final onItemCLickListener listener*/) {
            super(itemView);
            title = itemView.findViewById(R.id.tag_tv);
            et = itemView.findViewById(R.id.tag_et);
            count = itemView.findViewById(R.id.item_count_tv);
            deleteBtn = itemView.findViewById(R.id.delete_iv);
            editBtn = itemView.findViewById(R.id.edit_iv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.tagClickSearch(list.get(getAdapterPosition()).getTag());
                }
            });

            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        int duplicate = isDuplicate(et.getText().toString(), getAdapterPosition());
                        if(duplicate < 0) {
                            editBtn.setImageResource(R.drawable.ic_edit);
                            deleteBtn.setImageResource(R.drawable.tag_icon);
                            title.setVisibility(View.VISIBLE);
                            count.setVisibility(View.VISIBLE);
                            et.setVisibility(GONE);
                            if (updateTagInDb(title.getText().toString(), et.getText().toString()) != -1) {
                                title.setText(et.getText().toString());
                                list.get(getAdapterPosition()).setEditState(false);
                                loadUpdatedTagsList();
                            } else {
                                Toast.makeText(context, "Error updating tag" + list.get(getAdapterPosition()).getTag(), Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            for (HomeTagObject tagItem : list){

                                if(tagItem.getTid() == duplicate) {
                                    duplicate = list.indexOf(tagItem);
                                    break;
                                }
                            }


                            if(duplicate < 0){
                                editBtn.setImageResource(R.drawable.ic_edit);
                                deleteBtn.setImageResource(R.drawable.tag_icon);
                                title.setVisibility(View.VISIBLE);
                                count.setVisibility(View.VISIBLE);
                                et.setVisibility(GONE);
                                list.get(getAdapterPosition()).setEditState(false);
                                Toast.makeText(context, "operation failed!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(R.string.duplicate_tag_alert_message);
                                builder.setTitle(R.string.duplicate_tag_alert_title);
                                final int finalDuplicate = duplicate;
                                builder.setPositiveButton("Merge", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (mergeTags(getAdapterPosition(), finalDuplicate)) {
                                            //list.remove(getAdapterPosition());
                                            loadUpdatedTagsList();
                                        } else {
                                            editBtn.setImageResource(R.drawable.ic_edit);
                                            deleteBtn.setImageResource(R.drawable.tag_icon);
                                            title.setVisibility(View.VISIBLE);
                                            count.setVisibility(View.VISIBLE);
                                            et.setVisibility(GONE);
                                            list.get(getAdapterPosition()).setEditState(false);
                                            Toast.makeText(context, "Tag merging failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        editBtn.setImageResource(R.drawable.ic_edit);
                                        deleteBtn.setImageResource(R.drawable.tag_icon);
                                        title.setVisibility(View.VISIBLE);
                                        count.setVisibility(View.VISIBLE);
                                        et.setVisibility(GONE);
                                        list.get(getAdapterPosition()).setEditState(false);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    }
                    return false;
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.get(getAdapterPosition()).getEditState()){
                        int duplicate = isDuplicate(et.getText().toString(), getAdapterPosition());
                        if(duplicate < 0) {
                            editBtn.setImageResource(R.drawable.ic_edit);
                            deleteBtn.setImageResource(R.drawable.tag_icon);
                            title.setVisibility(View.VISIBLE);
                            count.setVisibility(View.VISIBLE);
                            et.setVisibility(GONE);
                            if (updateTagInDb(title.getText().toString(), et.getText().toString()) != -1) {
                                title.setText(et.getText().toString());
                                list.get(getAdapterPosition()).setEditState(false);
                                loadUpdatedTagsList();
                            } else {
                                Toast.makeText(context, "Error updating tag" + list.get(getAdapterPosition()).getTag(), Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            for (HomeTagObject tagItem : list){

                                if(tagItem.getTid() == duplicate) {
                                    duplicate = list.indexOf(tagItem);
                                    break;
                                }
                            }


                            if(duplicate < 0){
                                editBtn.setImageResource(R.drawable.ic_edit);
                                deleteBtn.setImageResource(R.drawable.tag_icon);
                                title.setVisibility(View.VISIBLE);
                                count.setVisibility(View.VISIBLE);
                                et.setVisibility(GONE);
                                list.get(getAdapterPosition()).setEditState(false);
                                Toast.makeText(context, "operation failed!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(R.string.duplicate_tag_alert_message);
                                builder.setTitle(R.string.duplicate_tag_alert_title);
                                final int finalDuplicate = duplicate;
                                builder.setPositiveButton("Merge", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (mergeTags(getAdapterPosition(), finalDuplicate)) {
                                            //list.remove(getAdapterPosition());
                                            loadUpdatedTagsList();
                                        } else {
                                            editBtn.setImageResource(R.drawable.ic_edit);
                                            deleteBtn.setImageResource(R.drawable.tag_icon);
                                            title.setVisibility(View.VISIBLE);
                                            count.setVisibility(View.VISIBLE);
                                            et.setVisibility(GONE);
                                            list.get(getAdapterPosition()).setEditState(false);
                                            Toast.makeText(context, "Tag merging failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        editBtn.setImageResource(R.drawable.ic_edit);
                                        deleteBtn.setImageResource(R.drawable.tag_icon);
                                        title.setVisibility(View.VISIBLE);
                                        count.setVisibility(View.VISIBLE);
                                        et.setVisibility(GONE);
                                        list.get(getAdapterPosition()).setEditState(false);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    } else{
                        editBtn.setImageResource(R.drawable.ic_tick);
                        deleteBtn.setImageResource(R.drawable.ic_delete);
                        title.setVisibility(View.GONE);
                        count.setVisibility(GONE);
                        et.setVisibility(View.VISIBLE);
                        et.setText(title.getText().toString());
                        list.get(getAdapterPosition()).setEditState(true);
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.get(getAdapterPosition()).getEditState()){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.delete_tag_alert_message);
                        builder.setTitle(R.string.delete_tag_alert_title);
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(deleteTagFromDb(list.get(getAdapterPosition())) != -1){
                                    list.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }

            });
        }
    }

    private boolean mergeTags(int first, int second) {
        HomeTagObject firstTag = list.get(first);
        HomeTagObject secondTag = list.get(second);

        String selectiont = TransactionsContract.TagsEntry.TID + "=?";
        String[] selectionArgst = {String.valueOf(firstTag.getTid())};

        String[] pqt = {TransactionsContract.QTEntry.QTID, TransactionsContract.QTEntry.QID, TransactionsContract.QTEntry.TID};
        String sortQT = TransactionsContract.QTEntry.TID;
        Cursor cqt = context.getContentResolver().query(TransactionsContract.QTEntry.CONTENT_QT_URI, pqt, null, null, sortQT);

        ArrayList<int[]> secondTagQTIdList = new ArrayList<>();
        ArrayList<int[]> firstTagQTIdList = new ArrayList<>();
        int max = Math.max(firstTag.getTid(), secondTag.getTid());
        int min = Math.min(firstTag.getTid(), secondTag.getTid());
        if(cqt != null){
            while (cqt.moveToNext()){
                int tid = cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.TID));

                if(tid  < min) continue;
                if(tid > max) break;

                int[] data = {cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.QID)), cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.QTID))};

                if(tid == secondTag.getTid()) {
                    secondTagQTIdList.add(data);
                }
                else if(tid == firstTag.getTid()) {
                    firstTagQTIdList.add(data);
                }
            }
        }
        ArrayList<Integer> mergeQTList = new ArrayList<>();
        ArrayList<Integer> deleteQTList = new ArrayList<>();
        for(int[] qtidItem : firstTagQTIdList) {
            for (int[] qtidItem2 : secondTagQTIdList) {
                if (qtidItem2[0] == qtidItem[0]) deleteQTList.add(qtidItem[1]);
                else mergeQTList.add(qtidItem[1]);
            }
        }
        for(int qtid : deleteQTList){
            context.getContentResolver().delete(TransactionsContract.QTEntry.CONTENT_QT_URI, TransactionsContract.QTEntry.QTID+"=?", new String[] {String.valueOf(qtid)});
        }
        int updateCount = 0;
        for(int qtid : mergeQTList) {
            String selectionqt = TransactionsContract.QTEntry.QTID + "=?";
            String[] selectionArgsqt = {String.valueOf(qtid)};
            ContentValues value = new ContentValues();
            value.put(TransactionsContract.QTEntry.TID, secondTag.getTid());
            int u = context.getContentResolver().update(TransactionsContract.QTEntry.CONTENT_QT_URI, value, selectionqt, selectionArgsqt);
            updateCount += u;
        }
        int deleteCount = context.getContentResolver().delete(TransactionsContract.TagsEntry.CONTENT_TAG_URI, selectiont, selectionArgst);

        if( deleteCount < 0) return  false;
        return true;
    }

    private int isDuplicate(String s, int pos) {
        Cursor ct = context.getContentResolver().query(TransactionsContract.TagsEntry.CONTENT_TAG_URI, null, null, null, null);
        if(ct != null){
            while (ct.moveToNext()){
                int dbTid = ct.getInt(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID));
                String dbTag = ct.getString(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG));

                if( (dbTag.toLowerCase().equals(s.toLowerCase())) && (dbTid != list.get(pos).getTid())) {
                    return dbTid;
                }
            }
        }
        return -1;
        /*for(HomeTagObject tagItem : list){
            if(tagItem.getTag().toLowerCase().equals(s.toLowerCase().trim()) && (list.indexOf(tagItem) != pos)) return list.indexOf(tagItem);
        }
        return -1;*/
    }

    private void loadUpdatedTagsList() {
        list.clear();
        String[] projection = {TransactionsContract.TagsEntry.TID, TransactionsContract.TagsEntry.COLUMN_TAG};
        String[] pqt = {TransactionsContract.QTEntry.TID};
        String st = TransactionsContract.TagsEntry.TID;
        String sqt = TransactionsContract.QTEntry.TID;
        Cursor cqt = context.getContentResolver().query(TransactionsContract.QTEntry.CONTENT_QT_URI, pqt, null, null, sqt);
        Cursor ct = context.getContentResolver().query(TransactionsContract.TagsEntry.CONTENT_TAG_URI, projection, null, null, st);

        if((ct != null) && (cqt != null)){
            while (ct.moveToNext()){
                int tid = ct.getInt(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID));
                String tag = ct.getString(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG));
                int count = 0;
                while(cqt.moveToNext()){
                    int tidQT = cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.TID));
                    if(!(tid == tidQT)) {
                        cqt.moveToPrevious();
                        break;
                    }
                    cqt.moveToNext();
                    int next = 0;
                    try {
                        next = cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.TID));
                    }catch (Exception e){

                    }
                    cqt.moveToPrevious();
                    count++;
                    if(tidQT < next){
                        break;
                    }
                }
                boolean duplicate = false;
                for(HomeTagObject tagItem : list){
                    if(tagItem.getTag().toLowerCase().equals(tag.toLowerCase())){
                        duplicate = true;
                        break;
                    }
                }
                if(!duplicate) list.add(new HomeTagObject(tag, tid, count, false));
            }
            MainActivity.resetSearchView();
        }
        else if((ct != null) && (cqt == null)){
            while (ct.moveToNext()){
                String tag = ct.getString(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG));
                int tid = ct.getInt(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID));
                list.add(new HomeTagObject(tag, tid, 0, false));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(new Comparator<HomeTagObject>() {
                @Override
                public int compare(HomeTagObject o1, HomeTagObject o2) {
                    return o1.getTag().compareTo(o2.getTag());
                }
            });
        }
        notifyDataSetChanged();
    }


    private int deleteTagFromDb(HomeTagObject tagObject) {
        String selection = TransactionsContract.TagsEntry.COLUMN_TAG + "=?";
        String[] selectionArgs = {tagObject.getTag()};
        context.getContentResolver().delete(TransactionsContract.QTEntry.CONTENT_QT_URI, TransactionsContract.QTEntry.TID + "=?", selectionArgs);
        return context.getContentResolver().delete(TransactionsContract.TagsEntry.CONTENT_TAG_URI, selection, selectionArgs);
    }

    private int updateTagInDb(String oldText, String newText) {
        ContentValues values = new ContentValues();
        String selection = TransactionsContract.TagsEntry.COLUMN_TAG + "=?";
        String[] selectionArgs = {oldText};
        values.put(TransactionsContract.TagsEntry.COLUMN_TAG, newText);
        return context.getContentResolver().update(TransactionsContract.TagsEntry.CONTENT_TAG_URI, values, selection, selectionArgs);
    }


    public TagListRecyclerAdapter(ArrayList<HomeTagObject> listItems, Context context){
        this.list = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public TagListRecyclerAdapter.TagListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.tag_recycler_list_item, parent, false);
        TagListViewHolder viewHolder = new TagListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagListViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTag());
        holder.et.setText(list.get(position).getTag());
        holder.count.setText(list.get(position).getQuestionsCount()+" Questions");
        if (!list.get(position).getEditState()) {
            holder.title.setVisibility(View.VISIBLE);
            holder.et.setVisibility(GONE);
            holder.count.setVisibility(View.VISIBLE);
            holder.editBtn.setImageResource(R.drawable.ic_edit);
            holder.deleteBtn.setImageResource(R.drawable.tag_icon);
        } else {
            holder.title.setVisibility(GONE);
            holder.count.setVisibility(GONE);
            holder.et.setVisibility(View.VISIBLE);
            holder.editBtn.setImageResource(R.drawable.ic_tick);
            holder.deleteBtn.setImageResource(R.drawable.ic_delete);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
