package com.orashar.android.questions.DataFiles;

import android.net.Uri;

public class TransactionsContract {

    public static final String CONTENT_AUTHORIY = "com.orashar.android.questions";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORIY);
    public static final String PATH_QUESTIONS = "questions";
    public static final String PATH_TAGS = "tags";
    public static final String PATH_QT = "qt";

    private TransactionsContract(){}

    public static final class QuestionsEntry{
        public static final String TABLE_NAME = "questions";

        public static final String QID = "qid";
        public static final String COLUMN_QUESTION_TEXT = "question_text";
        public static final String COLUMN_QUESTION_NOTES = "question_notes";
        public static final String COLUMN_IS_QUESTION_SAVED = "is_question_saved";


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_QUESTIONS);
    }

    public static final class TagsEntry{
        public static final String TABLE_NAME = "tags";

        public static final String TID = "tid";
        public static final String COLUMN_TAG = "tag";


        public static final Uri CONTENT_TAG_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TAGS);
    }

    public static final class QTEntry{
        public static final String TABLE_NAME = "qt";

        public static final String QTID = "qtid";
        public static final String QID = "qid";
        public static final String TID = "tid";


        public static final Uri CONTENT_QT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_QT);
    }

}