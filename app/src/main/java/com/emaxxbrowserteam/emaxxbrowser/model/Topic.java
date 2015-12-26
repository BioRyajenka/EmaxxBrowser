package com.emaxxbrowserteam.emaxxbrowser.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Topic implements Parcelable {
    private String title;
    public List<Algorithm> algorithms;

    public Topic(String title) {
        this.title = title;
        algorithms = new ArrayList<>();
    }

    public Topic(String title, List<Algorithm> algorithms) {
        this(title);
        this.algorithms = algorithms;
    }

    public Algorithm getAlgorithm(int i) {
        return algorithms.get(i);
    }

    public int getAlgorithmsCount() {
        return algorithms.size();
    }

    public void add(Algorithm algorithm) {
        algorithms.add(algorithm);
    }

    public String getTitle() {
        return title;
    }

    //-----======++++++Parsel part++++++======-----

    protected Topic(Parcel in) {
        title = in.readString();
        int num = in.readInt();
        algorithms = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            algorithms.add(new Algorithm(in));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(algorithms.size());
        for (Algorithm a : algorithms) {
            a.writeToParcel(dest, flags);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}