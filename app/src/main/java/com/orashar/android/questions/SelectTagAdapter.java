package com.orashar.android.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectTagAdapter extends ArrayAdapter<TagItemObject> {

    public SelectTagAdapter(Context context, ArrayList<TagItemObject> tagsList) {

        super(context, 0, tagsList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.tag_list_item, parent, false);
        }

        final TagItemObject currentTag = getItem(position);
        TextView tagtv = listItemView.findViewById(R.id.item_text);
        final CheckBox tagcb = listItemView.findViewById(R.id.item_checkbox);

        tagtv.setText(currentTag.getTag());
        tagcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentTag.setCBState(isChecked);
                currentTag.setCBState(currentTag.getCBState());
            }
        });
        tagcb.setChecked(currentTag.getCBState());
        return listItemView;

    }
}
