package com.orashar.android.questions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.orashar.android.questions.DataFiles.TransactionsContract;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Comparator;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    int lastFirstVisible = -1;

    static int whichFilter = 0;
    static String extraFilter = "";
    static String extraAllFilter = "";

    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private ActionBarDrawerToggle t;

    private Toolbar toolbar;

    private static RecyclerView homeListView, tagsListView;
    private static HomeListAdapter homeListAdapter, filteredAdapter;
    private static ArrayList<QuestionObject> questionsList;

    private static SearchView searchView;

    TagListRecyclerAdapter tagListAdapter;
    ArrayList<HomeTagObject> tagList;

    static TextView questionCounttv, homeHeadtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        questionCounttv = findViewById(R.id.tv);
        homeHeadtv = findViewById(R.id.home_head_tv);

        questionsList = new ArrayList<>();
        tagList = new ArrayList<>();
        loadQuestionsList();
        getTagsList();

        homeListView = findViewById(R.id.recycler_view_questions);
        homeListView.setHasFixedSize(false);
        homeListView.setLayoutManager(new LinearLayoutManager(this));
        tagsListView = findViewById(R.id.recycler_view_tags);
        tagsListView.setHasFixedSize(false);
        tagsListView.setLayoutManager(new LinearLayoutManager(this));
        homeListAdapter = new HomeListAdapter(questionsList, this);
        tagListAdapter = new TagListRecyclerAdapter(tagList, this);
        homeListView.setAdapter(homeListAdapter);
        tagsListView.setAdapter(tagListAdapter);
        tagsListView.setVisibility(GONE);
        homeListView.setVisibility(View.VISIBLE);
        whichFilter = 0;

        navigationView.setCheckedItem(R.id.drawer_all);

        questionCounttv.setText(questionsList.size() + " questions found");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        t = new ActionBarDrawerToggle(this, drawer, 0, 0);
        drawer.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        searchView = findViewById(R.id.search_view);
        ((TextView)searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null))).setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterHomeList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHomeList(newText);
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHeadtv.setVisibility(GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                homeHeadtv.setVisibility(View.VISIBLE);
                String navChecked = navigationView.getCheckedItem().getTitle().toString();
                if(navChecked.toLowerCase().equals("upsc") || navChecked.toLowerCase().equals("insight")){
                    whichFilter = 0;
                    filterHomeList(navChecked);
                }
                return false;
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                searchView.setQuery("", false);
                searchView.setIconified(true);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.drawer_all:
                        loadQuestionsList();
                        whichFilter = 0;
                        extraAllFilter = "";
                        extraFilter = "";
                        filterHomeList("");
                        homeHeadtv.setText("All");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_upsc:
                        loadQuestionsList();
                        whichFilter = 0;
                        extraFilter = "upsc";
                        filterHomeList("upsc");
                        homeHeadtv.setText("UPSC");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_insight:
                        loadQuestionsList();
                        whichFilter = 0;
                        extraFilter = "insight";
                        filterHomeList("insight");
                        homeHeadtv.setText("Insight");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_saved:
                        loadQuestionsList();
                        whichFilter = 0;
                        extraFilter = "saved";
                        filterHomeList("");
                        homeHeadtv.setText("Saved");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.drawer_tags:
                        getTagsList();
                        whichFilter = 1;
                        extraFilter = "";
                        filterHomeList("");
                        drawer.closeDrawer(GravityCompat.START);
                        homeHeadtv.setText("Tags");
                        //hideSearchView();
                        break;
                    case R.id.drawer_add_question:
                        startActivity(new Intent(MainActivity.this, AddQuestionsActivity.class));
                        break;
                    case R.id.drawer_contact_feedback:
                        startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                        break;
                    case R.id.drawer_settings_privacy:
                        startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                        break;
                }
                return true;
            }
        });
    }

    public static void resetSearchView(){
        searchView.onActionViewCollapsed();
    }

    public static void tagClickSearch(String tagName){
        searchView.setIconified(true);
        homeHeadtv.setText(tagName);
        extraAllFilter = tagName;
        navigationView.setCheckedItem(R.id.drawer_all);
        whichFilter = 0;
        extraFilter = tagName;
        searchView.setQuery(tagName, true);
    }


    private void getTagsList() {
        tagList.clear();
        String[] projection = {TransactionsContract.TagsEntry.TID, TransactionsContract.TagsEntry.COLUMN_TAG};
        String[] pqt = {TransactionsContract.QTEntry.TID};
        String st = TransactionsContract.TagsEntry.TID;
        String sqt = TransactionsContract.QTEntry.TID;
        Cursor cqt = getContentResolver().query(TransactionsContract.QTEntry.CONTENT_QT_URI, pqt, null, null, sqt);
        Cursor ct = getContentResolver().query(TransactionsContract.TagsEntry.CONTENT_TAG_URI, projection, null, null, st);

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
                for(HomeTagObject tagItem : tagList){
                    if(tagItem.getTag().toLowerCase().equals(tag.toLowerCase())){
                        duplicate = true;
                        break;
                    }
                }
                if(!duplicate) tagList.add(new HomeTagObject(tag, tid, count, false));
            }
        }
        else if((ct != null) && (cqt == null)){
            while (ct.moveToNext()){
                String tag = ct.getString(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG));
                int tid = ct.getInt(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID));
                tagList.add(new HomeTagObject(tag, tid, 0, false));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tagList.sort(new Comparator<HomeTagObject>() {
                @Override
                public int compare(HomeTagObject o1, HomeTagObject o2) {
                    return o1.getTag().compareTo(o2.getTag());
                }
            });
        }

    }

    private  void filterHomeList(String query) {
        ArrayList filteredList;
        if(query.equals("All")) {
            query = "";
            whichFilter = 0;
            extraFilter = "";
        }else if(query.equals("Tags")) {
            query = "";
            whichFilter = 1;
            extraFilter = "";
        }
        if(whichFilter == 0) {
            filteredList = new ArrayList<QuestionObject>();
            if(extraFilter.equals("saved")){
                for (QuestionObject question : questionsList) {
                    if ((matchQuery(question, query) || question.getQuestion().toLowerCase().contains(query.toLowerCase())) && (question.getIsSaved() == 1)) {
                        filteredList.add(question);
                    }
                }
            } else if(!extraAllFilter.isEmpty()){
                for (QuestionObject question : questionsList) {
                    if (matchExactQuery(question, extraFilter)) {
                        filteredList.add(question);
                    }
                }
            } else {
                for (QuestionObject question : questionsList) {
                    if ((matchQuery(question, query) || question.getQuestion().toLowerCase().contains(query.toLowerCase())) && (matchQuery(question, extraFilter) || question.getQuestion().toLowerCase().contains(extraFilter.toLowerCase()))) {
                        filteredList.add(question);
                    }
                }
            }
            homeListView.setVisibility(View.VISIBLE);
            tagsListView.setVisibility(GONE);
            filteredAdapter = new HomeListAdapter(filteredList, this);
            homeListView.setAdapter(filteredAdapter);
            questionCounttv.setText(filteredList.size() + " questions found");
        } else{
            getTagsList();
            filteredList = new ArrayList<HomeTagObject>();
            for(HomeTagObject tag : tagList){
                if(tag.getTag().contains(query)){
                    tag.setEditState(false);
                    filteredList.add(tag);
                }
            }
            homeListView.setVisibility(GONE);
            tagsListView.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filteredList.sort(new Comparator<HomeTagObject>() {
                    @Override
                    public int compare(HomeTagObject o1, HomeTagObject o2) {
                        return o1.getTag().compareTo(o2.getTag());
                    }
                });
            }
            TagListRecyclerAdapter filteredAdapter = new TagListRecyclerAdapter(filteredList, this);
            tagsListView.setAdapter(filteredAdapter);
            questionCounttv.setText(filteredList.size() + " tags found");
        }
    }

    private boolean matchQuery(QuestionObject q, String s) {
        for(String tag : q.getTags()){
            if(tag.toLowerCase().contains(s.toLowerCase())) return true;
        }
        return false;
    }

    private boolean matchExactQuery(QuestionObject q, String s) {
        for(String tag : q.getTags()){
            if(tag.toLowerCase().equals(s.toLowerCase())) return true;
        }
        return false;
    }

    private void loadQuestionsList() {
        questionsList.clear();

        String[] projectionQ = {TransactionsContract.QuestionsEntry.QID, TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT, TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED};
        String[] projectionT = {TransactionsContract.TagsEntry.TID, TransactionsContract.TagsEntry.COLUMN_TAG};
        String[] projectionQT = {TransactionsContract.QTEntry.QID, TransactionsContract.QTEntry.TID};
        String sortQ = TransactionsContract.QuestionsEntry.QID ;
        String sortT = TransactionsContract.TagsEntry.TID;
        String sortQT = TransactionsContract.QTEntry.QID ;
        Cursor cq = getContentResolver().query(TransactionsContract.QuestionsEntry.CONTENT_URI, projectionQ, null, null, sortQ);
        Cursor ct = getContentResolver().query(TransactionsContract.TagsEntry.CONTENT_TAG_URI, projectionT, null, null, sortT);
        Cursor cqt = getContentResolver().query(TransactionsContract.QTEntry.CONTENT_QT_URI, projectionQT, null, null, sortQT);


        if(cq != null){
            while (cq.moveToNext()){
                int qidQ = cq.getInt(cq.getColumnIndexOrThrow(TransactionsContract.QuestionsEntry.QID));
                int isQuestionSaved = cq.getInt(cq.getColumnIndexOrThrow(TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED));
                ArrayList<String> tags = new ArrayList<>();
                if(!((ct == null) || (cqt == null))){
                    ArrayList<Integer> tagids = new ArrayList<>();
                    while(cqt.moveToNext()){
                        int qidQT = cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.QID));
                        if(!(qidQ == qidQT)) {
                            cqt.moveToPrevious();
                            break;
                        }
                        cqt.moveToNext();
                        int next = 0;
                        try {
                            next = cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.QID));
                        }catch (Exception e){

                        }
                        cqt.moveToPrevious();
                        tagids.add(cqt.getInt(cqt.getColumnIndexOrThrow(TransactionsContract.QTEntry.TID)));
                        if(qidQT < next){
                            break;
                        }
                    }
                    if(tagids.size() > 0) {
                        ct.moveToFirst();
                        do {
                            if(tagids.contains(ct.getInt(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID)))){
                                tags.add(ct.getString(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG)));
                            }
                        } while (ct.moveToNext());
                    }
                }
                questionsList.add(new QuestionObject(qidQ, cq.getString(cq.getColumnIndexOrThrow(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT)), tags, isQuestionSaved));

            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (t.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    @Override
    protected void onPause() {
        lastFirstVisible = ((LinearLayoutManager)homeListView.getLayoutManager()).findFirstVisibleItemPosition();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        homeListView.getLayoutManager().scrollToPosition(lastFirstVisible);
        String query = "";
        if(!searchView.getQuery().toString().isEmpty()){
            query = searchView.getQuery().toString();
        }
        loadQuestionsList();
        getTagsList();
        try {
            switch (navigationView.getCheckedItem().getItemId()) {
                case R.id.drawer_all:
                    loadQuestionsList();
                    whichFilter = 0;
                    if(extraAllFilter.isEmpty()) {
                        extraFilter = "";
                        filterHomeList(query);
                        homeHeadtv.setText("All");
                    } else{
                        extraFilter = extraAllFilter;
                        filterHomeList(extraAllFilter);
                        homeHeadtv.setText(extraAllFilter);
                    }
                    break;
                case R.id.drawer_upsc:
                    loadQuestionsList();
                    whichFilter = 0;
                    extraFilter = "upsc";
                    filterHomeList(query);
                    homeHeadtv.setText("UPSC");
                    break;
                case R.id.drawer_insight:
                    loadQuestionsList();
                    whichFilter = 0;
                    extraFilter = "insight";
                    filterHomeList(query);
                    homeHeadtv.setText("Insight");
                    break;
                case R.id.drawer_saved:
                    loadQuestionsList();
                    whichFilter = 0;
                    extraFilter = "saved";
                    filterHomeList(query);
                    homeHeadtv.setText("Saved");
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case R.id.drawer_tags:
                    getTagsList();
                    whichFilter = 1;
                    extraFilter = "";
                    filterHomeList(query);
                    homeHeadtv.setText("Tags");
                    //hideSearchView();
                    break;
            }
        } catch (Exception e){

        }
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (searchView.isIconified()) {
            if (extraAllFilter.isEmpty()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Press BACK again to exit!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            } else {
                loadQuestionsList();
                whichFilter = 0;
                extraAllFilter = "";
                extraFilter = "";
                filterHomeList("");
                homeHeadtv.setText("All");
            }
        } else{
            searchView.setIconified(true);
        }
    }
}
