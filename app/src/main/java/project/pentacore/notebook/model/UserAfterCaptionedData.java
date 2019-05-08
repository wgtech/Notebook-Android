package project.pentacore.notebook.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserAfterCaptionedData {

    /**
     * {
     * 'idx': '1',
     * 'publish': '0',
     * 'rename': '170420266_20190507122500.jpg',
     * 'sentence': ['a large clock in a building with lights on .'],
     * 'model': 'simple'}
     */

    @SerializedName("idx") private String idx;
    @SerializedName("publish") private String publish;
    @SerializedName("rename") private String rename;
    @SerializedName("sentence") private ArrayList<String> sentences = new ArrayList<>(1);
    @SerializedName("model") private String model;

    public String getIdx() {
        return idx;
    }

    public String getPublish() {
        return publish;
    }

    public String getRename() {
        return rename;
    }

    public ArrayList<String> getSentences() {
        return sentences;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserAfterCaptionedData{");
        sb.append("idx='").append(idx).append('\'');
        sb.append(", publish='").append(publish).append('\'');
        sb.append(", rename='").append(rename).append('\'');
        sb.append(", sentences= ");
        for (String s: sentences) {
            sb.append(s).append(", ");
        }
        sb.append(", model='").append(model).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
