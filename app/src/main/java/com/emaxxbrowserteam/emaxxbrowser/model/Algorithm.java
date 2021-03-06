package com.emaxxbrowserteam.emaxxbrowser.model;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.emaxxbrowserteam.emaxxbrowser.loader.DownloadTask;
import com.emaxxbrowserteam.emaxxbrowser.loader.FileUtils;
import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;

public class Algorithm implements Parcelable {

    private Activity activity;
    private String url;
    private String title;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Algorithm(Activity activity, String title, String url) {
        this.activity = activity;
        this.title = title;
        this.url = url;
    }

    public String toString() {
        return getTitle();
    }

    public String getNameInCache() {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public void loadHtml(IListener listener) {
        DownloadTask task = new DownloadTask(getNameInCache(), activity, listener);
        task.execute(FileUtils.getURL(url));
    }

    //-----======++++++Parsel part++++++======-----

    protected Algorithm(Parcel in) {
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Algorithm> CREATOR = new Parcelable
            .Creator<Algorithm>() {
        @Override
        public Algorithm createFromParcel(Parcel in) {
            return new Algorithm(in);
        }

        @Override
        public Algorithm[] newArray(int size) {
            return new Algorithm[size];
        }
    };
}