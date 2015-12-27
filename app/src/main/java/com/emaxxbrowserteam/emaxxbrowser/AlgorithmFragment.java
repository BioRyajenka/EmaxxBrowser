package com.emaxxbrowserteam.emaxxbrowser;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;
import com.emaxxbrowserteam.emaxxbrowser.loader.Parser;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ListIterator;

/**
 * Created by Jackson on 26.12.2015.
 */
public class AlgorithmFragment extends Fragment {
    @Deprecated
    public AlgorithmFragment() {
    }

    public static AlgorithmFragment newInstance(Algorithm algorithm) {
        AlgorithmFragment myFragment = new AlgorithmFragment();

        Bundle args = new Bundle();
        args.putParcelable("algorithm", algorithm);
        myFragment.setArguments(args);

        return myFragment;
    }

    private String decorateHtml(Document doc) {
        doc.select("#contents-table").remove();
        doc.select(".title").remove();
        doc.select(".algoinfo").remove();
        doc.select(".menu").remove();
        doc.select("#disqus_thread").remove();
        doc.head().append("<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"style" +
                ".css\" />");
        doc.head().append("<script type=\"text/javascript\" src=\"script.js\">");

        Element e = doc.select(".main").first();
        Element div = doc.createElement("div");
        div.addClass("container");
        e.replaceWith(div);
        div.appendChild(e);

        ListIterator<Element> it = doc.select("h1").listIterator();
        int divId = 0;
        while (it.hasNext()) {
            Element h = it.next();
            div = doc.createElement("div");
            div.addClass("expandable");
            div.attr("id", "div" + divId);

            e = h.nextElementSibling();
            while (e != null && !e.tagName().equalsIgnoreCase("h1")) {
                div.appendChild(e);
                //h.remove();
            }
            Element a = doc.createElement("a");
            a.attr("href", "#").attr("onclick", "swap('div" + divId + "');return false;");
            h.replaceWith(a);
            a.after(div);

            divId++;
        }
        return doc.outerHtml();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Algorithm algorithm = getArguments().getParcelable("algorithm");
        Log.d(TAG, "ocv: algorithm is " + algorithm);

        View rootView = inflater.inflate(R.layout.fragment_algorithm, container, false);
        getActivity().getActionBar().setTitle(algorithm.getTitle());

        final WebView wv = (WebView) rootView.findViewById(R.id.webView);

        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);

        algorithm.loadHtml(new IListener() {
            @Override
            public void listen(Document document) {
                String dec = decorateHtml(document);
                wv.loadDataWithBaseURL("files://" + getActivity().getCacheDir().getAbsolutePath(), dec, "text/html", Parser.ENCODING, null);
                wv.loadDataWithBaseURL("file:///android_asset/www/",
                        decorateHtml(document), "text/html", "utf-8", null);
            }
        });

        return rootView;
    }

    private static String TAG = "AlgorithmFragment.java";
}