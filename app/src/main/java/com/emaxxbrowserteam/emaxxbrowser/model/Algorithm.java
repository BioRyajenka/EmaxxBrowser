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
    private String title;
    private String html;

    public String getTitle() {
        return title;
    }

    public Algorithm(MainActivity activity, String title, String url) {
        this.title = title;

        DownloadTask task = new DownloadTask(activity, new IListener() {
            @Override
            public void listen(Document document) {
                Algorithm.this.html = decorateHtml(document);
            }
        });
        //Log.d(TAG, );
        task.execute(FileUtils.getURL(url));
    }

    private String decorateHtml(Document doc) {
        //Elements els = doc.select("#contents-table");
        //els.remove();
        return doc.outerHtml();
    }

    public String getHtml() {
        return html;
    }

    //-----======++++++Parsel part++++++======-----

    protected Algorithm(Parcel in) {
        title = in.readString();
        html = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(html);
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