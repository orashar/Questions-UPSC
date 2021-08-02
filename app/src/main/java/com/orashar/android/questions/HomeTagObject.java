package com.orashar.android.questions;

class HomeTagObject {
    String tag;
    int tid;
    boolean editState;
    int questionsCount;

    public HomeTagObject(String tag, int tid, int questionsCount, boolean editState) {
        this.tag = tag;
        this.tid = tid;
        this.questionsCount = questionsCount;
        this.editState = editState;
    }

    public int getTid() {
        return tid;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getEditState() {
        return editState;
    }

    public void setEditState(boolean editState) {
        this.editState = editState;
    }
}
