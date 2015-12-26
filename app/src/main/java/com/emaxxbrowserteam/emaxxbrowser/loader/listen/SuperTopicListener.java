package com.emaxxbrowserteam.emaxxbrowser.loader.listen;

import android.app.Activity;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;
import com.emaxxbrowserteam.emaxxbrowser.loader.Parser;

import org.jsoup.nodes.Document;

public class SuperTopicListener extends Listener<MainActivity> {

    public SuperTopicListener(MainActivity object) {
        super(object);
    }

    @Override
    public Activity getActivity() {
        return object;
    }

    @Override
    public void onListen(Object... params) {
        Document document = (Document) params[0];
        object.updateSuperTopics(Parser.parse(document));
    }
}
