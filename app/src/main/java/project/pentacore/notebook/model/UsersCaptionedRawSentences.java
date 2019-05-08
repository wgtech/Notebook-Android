package project.pentacore.notebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsersCaptionedRawSentences {

    @SerializedName("texts") private ArrayList<String> texts;

    public ArrayList<String> getTexts() {
        return texts;
    }
}
