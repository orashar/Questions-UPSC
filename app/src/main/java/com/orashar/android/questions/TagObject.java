package com.orashar.android.questions;

public class TagObject {
    String tid;
    String tag;

    public TagObject(String tid, String tag) {
        this.tid = tid;
        this.tag = tag;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
