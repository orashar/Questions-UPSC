package com.orashar.android.questions.DataFiles;

import java.util.ArrayList;

class DBListObject {
    int text;
    ArrayList<String> tags;

    public int getText() {
        return text;
    }

    public void setText(int qui) {
        this.text = text;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public DBListObject(int text, ArrayList<String> tags) {
        this.text = text;
        this.tags = tags;
    }
}
