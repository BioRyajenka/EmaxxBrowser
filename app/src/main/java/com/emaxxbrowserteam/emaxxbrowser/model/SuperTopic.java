package com.emaxxbrowserteam.emaxxbrowser.model;

import android.util.Log;

import com.emaxxbrowserteam.emaxxbrowser.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public int getDrawableId() {
        Integer id = title2drawableId.get(getTitle());
        if (id != null) {
            return id;
        }
        return R.drawable.ic_photos;
    }

    private static Map<String, Integer> title2drawableId;

    static {
        title2drawableId = new HashMap<>();
        title2drawableId.put("Алгебра", R.drawable.ic_algebra);
        title2drawableId.put("Графы", R.drawable.ic_graphs);
        title2drawableId.put("Строки", R.drawable.ic_strings);
        title2drawableId.put("Геометрия", R.drawable.ic_geometry);
        title2drawableId.put("Структуры данных", R.drawable.ic_data_structures);
        title2drawableId.put("Алгоритмы на последовательностях", R.drawable.ic_sequences);
        title2drawableId.put("Динамика", R.drawable.ic_dp);
        title2drawableId.put("Линейная алгебра", R.drawable.ic_linear);
        title2drawableId.put("Численные методы", R.drawable.ic_integral);
        title2drawableId.put("Комбинаторика", R.drawable.ic_whats_hot);
        title2drawableId.put("Теория игр", R.drawable.ic_games);
        title2drawableId.put("Расписания", R.drawable.ic_drawer);
        title2drawableId.put("Разное", R.drawable.ic_pages);
    }

    private static String TAG = "SuperTopic";
}