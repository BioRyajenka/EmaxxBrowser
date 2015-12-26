package com.emaxxbrowserteam.emaxxbrowser.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;
import com.emaxxbrowserteam.emaxxbrowser.loader.DownloadTask;
import com.emaxxbrowserteam.emaxxbrowser.loader.FileUtils;
import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Algorithm implements Parcelable {

    private MainActivity activity;
    private String url;
    private String title;

    public String getTitle() {
        return title;
    }

    public Algorithm(MainActivity activity, String title, String url) {
        this.activity = activity;
        this.title = title;
        this.url = url;
    }

    public void loadHtml(IListener listener) {
        DownloadTask task = new DownloadTask(activity, listener);
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