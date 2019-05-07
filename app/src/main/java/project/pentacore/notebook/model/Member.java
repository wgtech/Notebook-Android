package project.pentacore.notebook.model;

import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("idx") private String idx;

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
}
