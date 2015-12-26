package com.emaxxbrowserteam.emaxxbrowser.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Algorithm implements Parcelable {
    private String title;
    private String html;

    public String getTitle() {
        return title;
    }

    public Algorithm(String title, String html) {
        this.title = title;
        this.html = html;
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
    public static final Parcelable.Creator<Algorithm> CREATOR = new Parcelable.Creator<Algorithm>() {
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