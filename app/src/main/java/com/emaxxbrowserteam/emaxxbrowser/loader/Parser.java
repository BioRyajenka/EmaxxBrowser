package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.util.Log;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.SuperTopic;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static final String E_MAXX_ALGO_URL = "http://e-maxx.ru/algo";

    public static List<SuperTopic> parse(MainActivity activity, Document document) {
        List<SuperTopic> superTopics = new ArrayList<>();
        Elements elements = document.getElementsByClass("content").get(0).children();
        for (int id = 0; id < elements.size(); ) {
            Element superThemeElement = elements.get(id++);
            if (superThemeElement.tagName().equals("h2")) {
                SuperTopic superTopic = new SuperTopic(normalize(superThemeElement.html()));
                while (id < elements.size()) {
                    Element topicElement = elements.get(id);
                    Topic curTopic;
                    if (!topicElement.tagName().equals("h4")) {
                        if (superTopic.isEmpty()) {
                            curTopic = new Topic("все алгоритмы");
                        } else {
                            break;
                        }
                    } else {
                        curTopic = new Topic(normalize(topicElement.html()));
                        ++id;
                    }
                    Elements algos = elements.get(id++).children();
                    for (Element algo : algos) {
                        curTopic.add(new Algorithm(activity, normalize(algo.text()),
                                E_MAXX_ALGO_URL + "/" + algo.childNode(0).attr("href")));
                    }
                    superTopic.add(curTopic);
                }
                superTopics.add(superTopic);
            }
        }
        return superTopics;
    }

    private static String normalize(String name) {
        int index = name.indexOf('(');
        if (index >= 0) {
            name = name.substring(0, index);
        }
        name = name.replaceAll("TeX", "");
        name = name.replace("[]", "");
        return name.trim();
    }

    public static final String TAG = "Parser";
}
