package project.pentacore.notebook.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * [
 *  {
 *      "idx": 58,
 *      "url": "170420266_20190508105916.jpg",
 *      "texts": "{\"texts\":[\"a suitcase with clothes and a bag on it .\"]}",
 *      "date": "2019-05-08T10:59:21.857383",
 *      "publish": false
 *   },
 *
 *   {
 *      "idx": 61,
 *      "url": "170420266_20190508111045.jpg",
 *      "texts": "{\"texts\":[\"a cup of coffee sitting next to a cup of coffee .\"]}",
 *      "date": "2019-05-08T11:10:49.246363",
 *      "publish": false
 *   }
 * ]
 */

public class UsersCaptionedRawData implements Serializable {

    @SerializedName("idx") private int idx;
    @SerializedName("url") private String url;
    @SerializedName("texts") private String texts; // String 형태의 Json
    @SerializedName("date") private String date;
    @SerializedName("publish") private boolean publish;

    public int getIdx() {
        return idx;
    }

    public String getUrl() {
        return url;
    }

    public String getTexts() {
        return texts;
    }

    public String getDate() {
        return date;
    }

    public boolean isPublish() {
        return publish;
    }
}
