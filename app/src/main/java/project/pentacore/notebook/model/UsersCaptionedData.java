package project.pentacore.notebook.model;

import java.util.ArrayList;

public class UsersCaptionedData {

    private int idx;
    private String url;
    private ArrayList<String> texts;
    private String date;
    private boolean publish;

    public static class Builder {
        private int idx;
        private String url;
        private ArrayList<String> texts;
        private String date;
        private boolean publish;

        public Builder setIdx(int idx) {
            this.idx = idx;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setTexts(ArrayList<String> texts) {
            this.texts = texts;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setPublish(boolean publish) {
            this.publish = publish;
            return this;
        }

        public UsersCaptionedData build() {
            return new UsersCaptionedData(this);
        }
    }

    private UsersCaptionedData(Builder builder) {
        this.idx = builder.idx;
        this.publish = builder.publish;
        this.url = builder.url;
        this.date = builder.date;
        this.texts = builder.texts;
    }

    public int getIdx() {
        return idx;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public String getDate() {
        return date;
    }

    public boolean isPublish() {
        return publish;
    }
}
