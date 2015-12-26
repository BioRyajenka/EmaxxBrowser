package com.emaxxbrowserteam.emaxxbrowser.model;

import java.util.ArrayList;
import java.util.List;

public class SuperTopic {

    private String title;
    public List<Topic> topics;

    public SuperTopic(String title) {
        this.title = title;
        topics = new ArrayList<>();
    }

    public SuperTopic(String title, List<Topic> topics) {
        this(title);
        this.topics = topics;
    }

    public void add(Topic topic) {
        topics.add(topic);
    }

    public String getTitle() {
        return title;
    }

    public boolean isEmpty() {
        return topics.isEmpty();
    }

    public Topic getTopic(int i) {
        return topics.get(i);
    }

    public int getTopicsCount() {
        return topics.size();
    }
}