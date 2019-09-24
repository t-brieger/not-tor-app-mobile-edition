package app.transcribing.mobile;

import android.media.Image;

public class DataModel {
    private String url;
    private String title;
    private String subTitle;

    public DataModel(String title, String subtitle, String url) {
        this.url = url;
        this.title = title;
        this.subTitle = subtitle;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }
}