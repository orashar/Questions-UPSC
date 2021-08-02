package com.orashar.android.questions;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.orashar.android.questions.DataFiles.TransactionsContract;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Comparator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class QuestionInfoActivity extends AppCompatActivity {

    private String questionText, questionNotes;
    private String[] tagsArr = {""};
    private LinearLayout tagsll;

    private EditText questionet, searchTaget;
    private TextView questiontv, taptv, createTagtv, notestv;
    private ImageView editiv;
    private RelativeLayout addTagsrl;
    private BottomSheetBehavior tagsBtmSheet;
    private ListView selectTagslv;
    private SelectTagAdapter adapter;
    private ArrayList<TagItemObject> tagsList;
    private ArrayList<TagItemObject> selectedTagsList;
    private LinearLayout createTagll;

    private ImageView backBtn;

    private boolean isChanged = false;

    int id;

    int isSaved = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questiontv = findViewById(R.id.question_tv);
        questionet = findViewById(R.id.question_et);
        tagsll = findViewById(R.id.tags_ll);
        tagsList = new ArrayList<>();
        selectedTagsList = new ArrayList<>();

        notestv = findViewById(R.id.notes_tv);
        final EditText noteset = findViewById(R.id.notes_et);
        final ImageView notesiv = findViewById(R.id.new_notes_iv);

        id = getIntent().getIntExtra("QID", -1);
        if(id == -1){
            questiontv.setText(R.string.question_not_found);
            questiontv.setGravity(Gravity.CENTER);
        } else {
            loadTags();
            loadQuestionData(id);
            questiontv.setText(questionText);
            questionet.setText(questionText);
            if(questionNotes != null) {
                if (questionNotes.isEmpty()){
                    noteset.setVisibility(GONE);
                    notestv.setVisibility(GONE);
                    notesiv.setVisibility(VISIBLE);

                    notesiv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notesiv.setVisibility(GONE);
                            noteset.setVisibility(VISIBLE);
                            notestv.setVisibility(GONE);
                            editiv.setImageResource(R.drawable.ic_tick);
                        }
                    });
                } else {
                    noteset.setVisibility(GONE);
                    notestv.setVisibility(VISIBLE);
                    notesiv.setVisibility(GONE);
                    notestv.setText(questionNotes);
                    noteset.setText(questionNotes);
                }
            }
            /*loadTags();
            setSelectedTags();*/
            addTags();

            backBtn = findViewById(R.id.back_iv);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            final ScrollView scrollView = findViewById(R.id.main_scroll);

            findViewById(R.id.note_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int bottom = 0;
                    if(notesiv.getVisibility() == GONE) bottom = noteset.getBottom();
                    else bottom = notesiv.getBottom();
                    scrollView.smoothScrollTo(0, bottom);
                    noteset.setVisibility(VISIBLE);
                    noteset.setText(notestv.getText().toString());
                    notestv.setVisibility(GONE);
                    editiv.setImageResource(R.drawable.ic_tick);
                }
            });

            editiv = findViewById(R.id.edit_iv);
            addTagsrl = findViewById(R.id.bottom_sheet);
            addTagsrl.setVisibility(GONE);
            questionet.setVisibility(GONE);
            questiontv.setVisibility(View.VISIBLE);
            editiv.setImageResource(R.drawable.ic_edit);
            selectTagslv = findViewById(R.id.list_tags);
            searchTaget = findViewById(R.id.search_tag_et);
            taptv = findViewById(R.id.tap_tv);
            createTagll = findViewById(R.id.create_tag_ll);
            createTagtv = findViewById(R.id.create_tag_tv);
            taptv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tagsBtmSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        tagsBtmSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else if (tagsBtmSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        tagsBtmSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            });

            adapter = new SelectTagAdapter(this, tagsList);
            selectTagslv.setAdapter(adapter);

            selectTagslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CheckBox c = view.findViewById(R.id.item_checkbox);
                    c.setChecked(!c.isChecked());
                }
            });
            createTagll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagsList.add(new TagItemObject(-1, createTagtv.getText().toString(), true));
                    searchTaget.setText("");
                    selectTagslv.setAdapter(adapter);
                    createTagll.setVisibility(View.GONE);
                    selectTagslv.smoothScrollToPosition(adapter.getCount()-1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tagsList.sort(new Comparator<TagItemObject>() {
                            @Override
                            public int compare(TagItemObject o1, TagItemObject o2) {
                                return o1.getTag().compareTo(o2.getTag());
                            }
                        });
                    }
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


            tagsBtmSheet = BottomSheetBehavior.from(addTagsrl);
            tagsBtmSheet.setHideable(false);
            tagsBtmSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED && listIsAtTop()) {
                        taptv.setText("Tap to close");
                        hideKeyboard(questionet);
                        searchTaget.setText("");
                        selectTagslv.setAdapter(adapter);
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        taptv.setText("Tap to add tags");
                        updateTagsList();
                        hideKeyboard(searchTaget);
                    } else if (newState == BottomSheetBehavior.STATE_DRAGGING && !listIsAtTop()) {
                        tagsBtmSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {

                }
            });

            editiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((questiontv.getVisibility() == GONE) && (questionet.getVisibility() == VISIBLE) || (notestv.getVisibility() == GONE) && (noteset.getVisibility() == VISIBLE)) {
                        if((notestv.getVisibility() == GONE) && (noteset.getVisibility() == VISIBLE)){
                            notestv.setText(noteset.getText().toString());
                            noteset.setVisibility(GONE);
                            if(notestv.getText().toString().isEmpty()){
                                notestv.setVisibility(GONE);
                                notesiv.setVisibility(VISIBLE);
                            } else{
                                notestv.setVisibility(VISIBLE);
                            }
                        }
                        if((questiontv.getVisibility() == GONE) && (questionet.getVisibility() == VISIBLE)){
                            questionet.setVisibility(GONE);
                            questiontv.setVisibility(VISIBLE);
                            questiontv.setText(questionet.getText().toString());
                        }
                        editiv.setImageResource(R.drawable.ic_edit);
                        addTagsrl.setVisibility(GONE);
                        hideKeyboard(questionet);
                        saveChangesInDb();
                    } else {
                        editiv.setImageResource(R.drawable.ic_tick);
                        questionet.setText(questiontv.getText().toString());
                        questionet.setVisibility(VISIBLE);
                        questiontv.setVisibility(GONE);
                        addTagsrl.setVisibility(VISIBLE);
                    }
                }
            });

            final ImageView saveiv = findViewById(R.id.save_iv);
            if(isSaved == 1) saveiv.setImageResource(R.drawable.ic_bookmarked);
            saveiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSaved == 0){
                        saveiv.setImageResource(R.drawable.ic_bookmarked);
                        changeBookmarkState(id, 1);
                        isSaved = 1;
                        Toast.makeText(QuestionInfoActivity.this, "Question Bookmarked", Toast.LENGTH_SHORT).show();
                    }else if(isSaved == 1) {
                        saveiv.setImageResource(R.drawable.ic_bookmark);
                        changeBookmarkState(id, 0);
                        isSaved = 0;
                        Toast.makeText(QuestionInfoActivity.this, "Question removed from Bookmark.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            findViewById(R.id.share_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    String body = questiontv.getText().toString() + " \n\n*Check out this app for more previous years questions of UPSC -*\n " + getString(R.string.app_link);
                    intentShare.setType("text/plain");
                    intentShare.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(intentShare);
                }
            });

        }

    }

    private void changeBookmarkState(int id, int i) {
        String selection = TransactionsContract.QuestionsEntry.QID + "=?";
        String[] selectonArgs = {String.valueOf(id)};
        ContentValues value = new ContentValues();
        value.put(TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED, i);
        this.getContentResolver().update(TransactionsContract.QuestionsEntry.CONTENT_URI, value, selection, selectonArgs);
    }

    private void saveChangesInDb() {
        String selection = TransactionsContract.QuestionsEntry.QID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT, questiontv.getText().toString());
        values.put(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_NOTES, notestv.getText().toString());
        getContentResolver().update(TransactionsContract.QuestionsEntry.CONTENT_URI, values, selection, selectionArgs);
        ArrayList<Integer> tagIdList = getSelectedTagsIds();
        for(TagItemObject tagItems : tagsList){
            if(tagItems.getTid() == -1){
                ContentValues valuesT = new ContentValues();
                valuesT.put(TransactionsContract.TagsEntry.COLUMN_TAG, tagItems.getTag().trim());
                Uri u = getContentResolver().insert(TransactionsContract.TagsEntry.CONTENT_TAG_URI, valuesT);
                tagIdList.add(Integer.parseInt(u.toString().split("/")[u.toString().split("/").length-1]));
            }
        }
        clearPreviousRelations();
        for(int tid : tagIdList){
            ContentValues valuesQT = new ContentValues();
            valuesQT.put(TransactionsContract.QTEntry.QID, id);
            valuesQT.put(TransactionsContract.QTEntry.TID, tid);
            Uri u = getContentResolver().insert(TransactionsContract.QTEntry.CONTENT_QT_URI, valuesQT);
        }
    }

    private void clearPreviousRelations() {
        String selectionQT = TransactionsContract.QTEntry.QID + "=?";
        String[] selectionArgsQT = {String.valueOf(id)};
        long no = getContentResolver().delete(TransactionsContract.QTEntry.CONTENT_QT_URI, selectionQT, selectionArgsQT);
    }

    private ArrayList<Integer> getSelectedTagsIds() {
        ArrayList<Integer> res = new ArrayList<>();
        for(TagItemObject tagItem : selectedTagsList){
            res.add(tagItem.getTid());
        }
        return res;
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

    public void hideKeyboard(View view) {
        if(view != null){
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public boolean listIsAtTop()   {
        if(selectTagslv.getChildCount() == 0) return true;
        return (selectTagslv.getChildAt(0).getTop() == 0 && selectTagslv.getFirstVisiblePosition() ==0);
    }

    private void updateTagsList() {
        selectedTagsList.clear();
        for(TagItemObject tagItem : tagsList){
            if(tagItem.getCBState()) {
                if(!selectedTagsList.contains(tagItem)) {
                    selectedTagsList.add(tagItem);
                }
            }
        }
        addTags();
    }

    private void addTags(){
        tagsll.removeAllViews();
        for(TagItemObject tagItem : selectedTagsList){
            final TextView tagtv = new TextView(this);
            tagtv.setText(tagItem.getTag());
            tagtv.setTextSize(14);
            tagtv.setPadding(16, 6, 16, 6);
            LinearLayout.LayoutParams taglp  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80);
            taglp.setMargins(16, 6, 16, 6);
            tagtv.setLayoutParams(taglp);
            tagtv.setBackgroundResource(R.drawable.quesetion_tag_bg);
            tagtv.setGravity(Gravity.CENTER);
            tagsll.addView(tagtv);
        }
    }

    private void loadTags() {
        tagsList.clear();
        String[] projection = {TransactionsContract.TagsEntry.TID, TransactionsContract.TagsEntry.COLUMN_TAG};
        String sort = TransactionsContract.TagsEntry.TID;
        Cursor c = getContentResolver().query(TransactionsContract.TagsEntry.CONTENT_TAG_URI, projection, null, null, sort);
        if(c != null){
            while (c.moveToNext()){
                int tid = c.getInt(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID));
                String tag = c.getString(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG)).toLowerCase();
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

    private void loadQuestionData(int qid) {
        String[] projection = {TransactionsContract.QuestionsEntry.QID, TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT, TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED, TransactionsContract.QuestionsEntry.COLUMN_QUESTION_NOTES};
        String selection = TransactionsContract.QuestionsEntry.QID + "=?";
        String[] selectionArgs = {String.valueOf(qid)};
        Cursor c = getContentResolver().query(TransactionsContract.QuestionsEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if(c != null){
            c.moveToFirst();
            questionText = c.getString(c.getColumnIndexOrThrow(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT));
            isSaved = c.getInt(c.getColumnIndexOrThrow(TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED));
            questionNotes = c.getString(c.getColumnIndexOrThrow(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_NOTES));
            String[] projectionQT = {TransactionsContract.QTEntry.QID, TransactionsContract.QTEntry.TID};
            String selectionQT = TransactionsContract.QTEntry.QID + "=?";
            String[] selectionArgsQT = {String.valueOf(qid)};
            Cursor cqt = getContentResolver().query(TransactionsContract.QTEntry.CONTENT_QT_URI, projectionQT, selectionQT, selectionArgsQT, null);
            ArrayList<Integer> tagIdList = new ArrayList<>();
            if (cqt != null) {
                while (cqt.moveToNext()) {
                    int tid =  cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.TID));
                    tagIdList.add(tid);
                }
                if (tagIdList.size() > 0) {
                    for (int tid : tagIdList) {
                        for(TagItemObject tagItem : tagsList){
                            if(tagItem.getTid() == tid){
                                tagItem.setCBState(true);
                                selectedTagsList.add(tagItem);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (tagsBtmSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
            tagsBtmSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.nothing, R.anim.up_bottom);
        }
    }
}
