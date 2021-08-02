package com.orashar.android.questions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.orashar.android.questions.DataFiles.TransactionsContract;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Comparator;

public class AddQuestionsActivity extends AppCompatActivity {


    private EditText questionet;

    private ArrayList<String> universalTagsList;
    private ArrayList<QuestionObject> questionsList;

    private ArrayList<String> tagListFordb;
    private ChipGroup tagsGroup;

    private BottomSheetBehavior tagBtmSheet;
    private RelativeLayout tagSheetrl;
    private TextView taptv, createTagtv;
    private EditText searchTaget;
    private LinearLayout createTagll;
    private ListView selectTagslv;

    private ArrayList<TagItemObject> tagsList;
    private ArrayList<String> chipsList;
    private SelectTagAdapter adapter;

    private ImageView backBtn, infoiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        questionet = findViewById(R.id.questions_et);
        tagsGroup = findViewById(R.id.tag_group);

        backBtn = findViewById(R.id.back_iv);
        infoiv = findViewById(R.id.info_iv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuestionsActivity.super.onBackPressed();
            }
        });
        infoiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddQuestionsActivity.this, R.string.info_not_available, Toast.LENGTH_SHORT).show();
            }
        });

        tagsList = new ArrayList<>();
        chipsList = new ArrayList<>();

        taptv = findViewById(R.id.tap_tv);
        createTagtv = findViewById(R.id.create_tag_tv);
        searchTaget = findViewById(R.id.search_tag_et);
        createTagll = findViewById(R.id.create_tag_ll);
        selectTagslv = findViewById(R.id.list_tags);
        tagSheetrl = findViewById(R.id.bottom_sheet);
        tagBtmSheet = BottomSheetBehavior.from(tagSheetrl);
        tagBtmSheet.setHideable(false);
        tagBtmSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }

        loadTagsList();
        adapter = new SelectTagAdapter(this, tagsList);
        selectTagslv.setAdapter(adapter);

        tagBtmSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED && listIsAtTop()) {
                    taptv.setText(R.string.tag_btmsheet_tap_close);
                    hideKeyboard(questionet);
                    searchTaget.setText("");
                    selectTagslv.setAdapter(adapter);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    taptv.setText(R.string.tag_btmsheet_tap_open);
                    updateChipsList();
                    hideKeyboard(searchTaget);
                } else if(newState == BottomSheetBehavior.STATE_DRAGGING && !listIsAtTop()){
                    tagBtmSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        taptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagBtmSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    tagBtmSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (tagBtmSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    tagBtmSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        createTagll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!duplicateTag(createTagtv.getText().toString())) {
                    tagsList.add(new TagItemObject(-1, createTagtv.getText().toString().toLowerCase(), true));
                    searchTaget.setText("");
                    createTagll.setVisibility(View.GONE);
                    selectTagslv.smoothScrollToPosition(adapter.getCount() - 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tagsList.sort(new Comparator<TagItemObject>() {
                            @Override
                            public int compare(TagItemObject o1, TagItemObject o2) {
                                return o1.getTag().compareTo(o2.getTag());
                            }
                        });
                    }
                } else{
                    Toast.makeText(AddQuestionsActivity.this, "Tag already exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        selectTagslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox c = view.findViewById(R.id.item_checkbox);
                c.setChecked(!c.isChecked());
            }
        });
        searchTaget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    createTagll.setVisibility(View.VISIBLE);
                    createTagtv.setText(s);
                    filterTagList(s.toString());
                }
                else{
                    createTagll.setVisibility(View.GONE);
                    selectTagslv.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterTagList(s.toString());
            }
        });

        questionsList = new ArrayList<>();
        universalTagsList = new ArrayList<>();
        tagListFordb = new ArrayList<>();

        findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTagsIndb();
                if (questionet.getText().toString().isEmpty()) {
                    Toast.makeText(AddQuestionsActivity.this, R.string.empty_data_warning, Toast.LENGTH_SHORT).show();
                } else {
                    if (!universalTagsList.isEmpty()) {
                        for (QuestionObject question : questionsList) {
                            question.setTags(universalTagsList);
                        }
                    }
                    int skippedQuestions = saveQuestionIndb();
                    if (skippedQuestions < 1) {
                        Toast.makeText(AddQuestionsActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                        Intent intentToMain = new Intent(AddQuestionsActivity.this, MainActivity.class);
                        intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToMain);
                    } else {
                        Toast.makeText(AddQuestionsActivity.this, "Something Went wrong! " + skippedQuestions + " questions skipped.", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            questionet.setText(sharedText);
        }
    }

    private boolean duplicateTag(String tagText) {
        for(TagItemObject tagItem : tagsList){
            if(tagItem.getTag().toLowerCase().equals(tagText.toLowerCase())) return true;
        }
        return false;
    }

    private void filterTagList(String query){
        ArrayList<TagItemObject> filteredList = new ArrayList<>();
        for(TagItemObject tagItem : tagsList){
            if(tagItem.getTag().toLowerCase().contains(query.toLowerCase())) filteredList.add(tagItem);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filteredList.sort(new Comparator<TagItemObject>() {
                @Override
                public int compare(TagItemObject o1, TagItemObject o2) {
                    return o1.getTag().compareTo(o2.getTag());
                }
            });
        }
        SelectTagAdapter filteredAdapter = new SelectTagAdapter(this, filteredList);
        selectTagslv.setAdapter(filteredAdapter);
    }

    private void updateChipsList() {
        chipsList.clear();
        for(TagItemObject tagItem : tagsList){
            if(tagItem.getCBState()) {
                if(!chipsList.contains(tagItem.getTag())) {
                    chipsList.add(tagItem.getTag());
                }
            }
        }
        addChips();
    }

    private void addChips() {
        tagsGroup.removeAllViews();
        for (int i = 0; i < chipsList.size(); i++) {
            final Chip tagChip = (Chip) getLayoutInflater().inflate(R.layout.chit_item, tagsGroup, false);
            tagChip.setText(chipsList.get(i));
            tagChip.setCloseIconVisible(true);
            tagChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagsGroup.removeView(tagChip);
                    chipsList.remove(tagChip.getText().toString());
                    adapter.notifyDataSetChanged();
                    uncheckRemovedChipFromBottomSheet(tagChip.getText().toString());
                }
            });
            if(tagChip.getParent() != null) ((ViewGroup) tagChip.getParent()).removeView(tagChip);
            tagsGroup.addView(tagChip);
        }
    }

    private void uncheckRemovedChipFromBottomSheet(String tagName) {
        for(TagItemObject tagItem : tagsList){
            if(tagItem.getTag().equals(tagName)) tagItem.setCBState(false);
        }
    }

    public boolean listIsAtTop()   {
        if(selectTagslv.getChildCount() == 0) return true;
        return (selectTagslv.getChildAt(0).getTop() == 0 && selectTagslv.getFirstVisiblePosition() == 0);

    }

    private void loadTagsList() {
        String[] projection = {TransactionsContract.TagsEntry.TID, TransactionsContract.TagsEntry.COLUMN_TAG};
        Cursor c = getContentResolver().query(TransactionsContract.TagsEntry.CONTENT_TAG_URI, projection, null, null, null);
        if(c != null){
            while (c.moveToNext()){
                int tid = c.getInt(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID));
                String tag = c.getString(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG));
                boolean duplicate = false;
                for(TagItemObject tagItem : tagsList){
                    if(tagItem.getTag().toLowerCase().equals(tag.toLowerCase())) {
                        duplicate = true;
                        break;
                    }
                }
                if(!duplicate) tagsList.add(new TagItemObject(tid, tag, false));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tagsList.sort(new Comparator<TagItemObject>() {
                @Override
                public int compare(TagItemObject o1, TagItemObject o2) {
                    return o1.getTag().compareTo(o2.getTag());
                }
            });
        }
    }

    private void insertTagsIndb() {
        for(TagItemObject tagItems : tagsList){
            if(tagItems.getTid() == -1){
                ContentValues valuesT = new ContentValues();
                valuesT.put(TransactionsContract.TagsEntry.COLUMN_TAG, tagItems.getTag().trim());
                Uri u = getContentResolver().insert(TransactionsContract.TagsEntry.CONTENT_TAG_URI, valuesT);
                tagItems.setTid(Integer.parseInt(u.toString().split("/")[u.toString().split("/").length-1]));
            }
        }
    }
    public void hideKeyboard(View view) {
        if(view != null){
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private int saveQuestionIndb() {
        int skippedQuestions = 0;
        ContentValues valuesQ = prepareValues();
        if (valuesQ != null) {
            Uri uriQ = getContentResolver().insert(TransactionsContract.QuestionsEntry.CONTENT_URI, valuesQ);
            int qid = Integer.parseInt(uriQ.toString().split("/")[uriQ.toString().split("/").length-1]);
            insertTagsIndb();
            ArrayList<Integer> tagIds = getSelectedTagsList();
            if (uriQ == null) {
                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                skippedQuestions++;
            } else{
                if(tagIds.size() > 0){
                    for (int tid : tagIds){
                        ContentValues valuesQT = new ContentValues();
                        valuesQT.put(TransactionsContract.QTEntry.QID, qid);
                        valuesQT.put(TransactionsContract.QTEntry.TID, tid);
                        getContentResolver().insert(TransactionsContract.QTEntry.CONTENT_QT_URI, valuesQT);
                    }
                }
            }
        } else {
            Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
        return skippedQuestions;
    }

    private ContentValues prepareValues() {
        ContentValues values = new ContentValues();
        values.put(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT, questionet.getText().toString());
        return values;
    }


    private ArrayList<Integer> getSelectedTagsList() {
        ArrayList<Integer> list = new ArrayList<>();
        for(TagItemObject tagItem : tagsList){
            if(tagItem.getCBState()){
                list.add(tagItem.getTid());
            }
        }
        return list;

    }

    @Override
    public void onBackPressed() {
        if (tagBtmSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
            tagBtmSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }
}
