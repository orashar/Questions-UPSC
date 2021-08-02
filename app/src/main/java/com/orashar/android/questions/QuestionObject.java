package com.orashar.android.questions;

import java.util.ArrayList;

public class QuestionObject {
    int qid;
    String question;
    ArrayList<String> tags;
    int isSaved;

    public QuestionObject(int id, String question, ArrayList<String> tags, int isSaved) {
        this.qid = id;
        this.question = question;
        this.tags = tags;
        this.isSaved = isSaved;
    }

    public int getIsSaved() {
        return isSaved;
    }

    public int getQid() {
        return qid;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
