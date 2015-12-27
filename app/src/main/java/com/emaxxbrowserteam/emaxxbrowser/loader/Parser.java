package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.app.Activity;
import android.util.Log;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.SuperTopic;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    public static final String E_MAXX_ALGO_URL = "http://e-maxx.ru/algo";
    public static final String E_MAXX_URL = "http://e-maxx.ru";
    public static final String SUPER_TOPIC_TAG = "h2";
    public static final String TOPIC_TAG = "h4";
    public static final String DEFAULT_TOPIC_TITLE = "все алгоритмы";
    public static final String CONTENT = "content";
    public static final String ENCODING = "cp1251";

    public static Map<String, Algorithm> algorithmByNameInCache;

    public static List<SuperTopic> parse(Activity activity, Document document) {
        algorithmByNameInCache = new HashMap<>();
        List<SuperTopic> superTopics = new ArrayList<>();
        Elements elements = document.getElementsByClass(CONTENT);
        if (elements.isEmpty()) {
            return superTopics;
        }
        elements = elements.first().children();
        for (int id = 0; id < elements.size(); ) {
            Element superTopicElement = elements.get(id++);
            if (superTopicElement.tagName().equals(SUPER_TOPIC_TAG)) {
                SuperTopic superTopic = new SuperTopic(normalize(eraseCount(superTopicElement.html())));
                while (id < elements.size()) {
                    Element topicElement = elements.get(id);
                    Topic curTopic;
                    if (!topicElement.tagName().equals(TOPIC_TAG)) {
                        if (superTopic.isEmpty()) {
                            curTopic = new Topic(DEFAULT_TOPIC_TITLE);
                        } else {
                            break;
                        }
                    } else {
                        curTopic = new Topic(normalize(eraseCount(topicElement.html())));
                        ++id;
                    }
                    if (elements.get(id).tagName().equals(TOPIC_TAG)) {
                        continue;
                    }
                    Elements algos = elements.get(id++).children();
                    for (Element algo : algos) {
                        Algorithm algorithm = new Algorithm(activity, normalize(algo.text()),
                                E_MAXX_ALGO_URL + "/" + algo.childNode(0).attr("href"));
                        curTopic.add(algorithm);
                        algorithmByNameInCache.put(algorithm.getNameInCache(), algorithm);
                    }
                    superTopic.add(curTopic);
                }
                superTopics.add(superTopic);
            }
        }
        for (String key : algorithmByNameInCache.keySet()) {
            Log.d(TAG, key + " -> " + algorithmByNameInCache.get(key).getTitle());
        }
        return superTopics;
    }

    private static String eraseCount(String name) {
        int index = name.indexOf('(');
        if (index >= 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    private static String normalize(String name) {
        name = name.replaceAll("TeX", "");
        name = name.replace("[]", "");
        return name.trim();
    }

    public static final String TAG = "Parser";
}
