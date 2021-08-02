package com.orashar.android.questions.DataFiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class TransactionsDbHelper extends SQLiteOpenHelper {

    Context context;

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "question.db";
    public static final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TransactionsContract.QuestionsEntry.TABLE_NAME + "( "
            + TransactionsContract.QuestionsEntry.QID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT + " TEXT, "
            + TransactionsContract.QuestionsEntry.COLUMN_QUESTION_NOTES + " TEXT, "
            + TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED + " INTEGER);";

    public static final String SQL_CREATE_TAGS_TABLE = "CREATE TABLE " + TransactionsContract.TagsEntry.TABLE_NAME + "( "
            + TransactionsContract.TagsEntry.TID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TransactionsContract.TagsEntry.COLUMN_TAG + " TEXT);";

    public static final String SQL_CREATE_QT_TABLE = "CREATE TABLE " + TransactionsContract.QTEntry.TABLE_NAME + "( "
            + TransactionsContract.QTEntry.QTID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TransactionsContract.QTEntry.QID + " INTEGER, "
            + TransactionsContract.QTEntry.TID + " INTEGER);";


    private static final String SQL_DELETE_ENTRIES = "";


    public TransactionsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_TAGS_TABLE);
        db.execSQL(SQL_CREATE_QT_TABLE);
        loadJson(db);
    }

    ArrayList<String> filesList = new ArrayList<>();

    private void getFilesList() {
        filesList.clear();
        filesList.add("2019.json");
        filesList.add("Agriculture.json");
        filesList.add("Ancient History and Art & Culture.json");
        filesList.add("Disaster Management.json");
        filesList.add("Economic Development.json");
        filesList.add("Environment and Ecology.json");
        filesList.add("Ethics.json");
        filesList.add("Case Studies.json");
        filesList.add("Geography.json");
        filesList.add("Governance.json");
        filesList.add("Indian Society.json");
        filesList.add("Internal Security.json");
        filesList.add("International Relations.json");
        filesList.add("Modern History.json");
        filesList.add("Polity.json");
        filesList.add("Post Independent India.json");
        filesList.add("Science & Technology.json");
        filesList.add("Social Justice.json");
        filesList.add("World History.json");
    }

    private void loadJson(SQLiteDatabase db) {
        getFilesList();
        ArrayList<String> allTags = new ArrayList<>();
        allTags.add("UPSC".toLowerCase());
        ArrayList<DBListObject> list = new ArrayList<>();
        for (String fileName : filesList) {
            try {
                InputStream is = context.getAssets().open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String s = new String(buffer, "UTF-8");
                JSONObject obj = new JSONObject(s);
                JSONObject main = obj.getJSONObject("child");
                Iterator<?> keys = main.keys();

                int i = 0;
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (main.get(key) instanceof JSONObject) {
                        i++;
                        JSONObject child = (JSONObject) main.get(key);
                        String questionText = child.getString("question");
                        ContentValues values = new ContentValues();
                        values.put(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_TEXT, questionText);
                        values.put(TransactionsContract.QuestionsEntry.COLUMN_QUESTION_NOTES, "");
                        values.put(TransactionsContract.QuestionsEntry.COLUMN_IS_QUESTION_SAVED, 0);
                        long qid = db.insert(TransactionsContract.QuestionsEntry.TABLE_NAME, null, values);

                        ArrayList<String> tags = new ArrayList<>();
                        JSONArray tagsArr = child.getJSONArray("tags");
                        for(int j = 0; j < tagsArr.length(); j++){
                            String curr = tagsArr.getString(j).trim().replace("\n", "").toLowerCase();
                            tags.add(curr);
                            if(!allTags.contains(curr)) allTags.add(curr);
                        }

                        list.add(new DBListObject((int) qid, tags));
                    }
                }

            } catch (Exception e) {
                Log.e("DBInsertData", e.getMessage());
            }
        }
        if(allTags.size() > 0) {
            for (String tag : allTags) {
                db.execSQL("INSERT INTO " + TransactionsContract.TagsEntry.TABLE_NAME + "( " + TransactionsContract.TagsEntry.COLUMN_TAG + ") VALUES('" + tag + "')");
            }
        }

        if (list.size() > 0) {
            for (DBListObject l : list) {
                ArrayList<Integer> tagIds = new ArrayList<>();
                Cursor ct = db.query(TransactionsContract.TagsEntry.TABLE_NAME, new String[]{TransactionsContract.TagsEntry.TID, TransactionsContract.TagsEntry.COLUMN_TAG}, null, null, null, null, null, null);
                if (ct != null) {
                    while (ct.moveToNext()) {
                        for (String t : l.getTags()) {
                            if (t.equals(ct.getString(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_TAG)))) {
                                tagIds.add(ct.getInt(ct.getColumnIndexOrThrow(TransactionsContract.TagsEntry.TID)));
                            }
                        }
                    }
                }
                for (int tid : tagIds) {
                    ContentValues values = new ContentValues();
                    values.put(TransactionsContract.QTEntry.QID, l.getText());
                    values.put(TransactionsContract.QTEntry.TID, tid);
                    db.insert(TransactionsContract.QTEntry.TABLE_NAME, null, values);
                }
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TransactionsContract.QTEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TransactionsContract.QuestionsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TransactionsContract.TagsEntry.TABLE_NAME);
        onCreate(db);
    }


}
