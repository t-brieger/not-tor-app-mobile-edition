package app.transcribing.mobile;

import android.view.View;

public class DataModel {
    private String url;
    private String title;
    private String subTitle;
    private View.OnClickListener e;

    public DataModel(String title, String subtitle, String url, View.OnClickListener e) {
        this.url = url;
        this.title = title;
        this.subTitle = subtitle;
        this.e = e;
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

    public View.OnClickListener getOnClick() {
        return e;
    }
}