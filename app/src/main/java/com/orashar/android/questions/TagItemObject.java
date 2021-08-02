package com.orashar.android.questions;

public class TagItemObject {
    int tid;
    String tag;
    boolean state;

    public TagItemObject(int tid, String tag, boolean state) {
        this.tid = tid;
        this.tag = tag;
        this.state = state;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getCBState() {
        return state;
    }

    public void setCBState(boolean state) {
        this.state = state;
    }
}
