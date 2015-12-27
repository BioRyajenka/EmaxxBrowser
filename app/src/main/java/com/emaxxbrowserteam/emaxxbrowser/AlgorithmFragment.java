package com.emaxxbrowserteam.emaxxbrowser;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Iterator;
import java.util.LinkedList;
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

        doc.select("script").remove();
        doc.select("link").remove();

        ListIterator<Element> it = doc.select("a").listIterator();
        while (it.hasNext()) {
            Element a = it.next();
            if (a.text().equals("")) {
                a.remove();
            }
        }

        it = doc.select("pre").listIterator();
        while (it.hasNext()) {
            Element a = it.next();
            String html = a.html();
            a.html(html.replace("\t", "&nbsp;&nbsp;"));
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String style = sp.getString(getString(R.string.pref_color_theme), "classic");

        Log.d(TAG, "style: " + style);

        doc.head().append("<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"" + style + ".css\"" +
                ".css\" />");
        doc.head().append("<script type=\"text/javascript\" src=\"script.js\">");

        Element e = doc.select(".main").first();
        Element div = doc.createElement("div");
        div.addClass("container");
        e.replaceWith(div);
        div.appendChild(e);

        ListIterator<Node> it2 = doc.select(".content").first().childNodes().listIterator();

        int divId = 0;

        LinkedList<Node> list = new LinkedList<>();
        Node h = it2.next();
        while (it2.hasNext()) {
            list.add(it2.next());
        }

        Iterator<Node> it3 = list.iterator();
        h = it3.next();
        while (h != null && !h.nodeName().equalsIgnoreCase("h2")) {
            h = it3.next();
        }
        while (it3.hasNext()) {
            div = doc.createElement("div");
            div.attr("id", "div" + divId);
            div.attr("style", "display: none;");

            Node ne = null;
            while (it3.hasNext()) {
                ne = it3.next();
                if (ne.nodeName().equalsIgnoreCase("h2")) break;
                div.appendChild(ne);
            }
            Element a = doc.createElement("a");
            a.append(h.toString());
            a.attr("href", "#").attr("onclick", "swap('div" + divId + "');return false;");
            a.addClass("expandable");

            h.replaceWith(a);
            a.after(div);

            divId++;
            h = ne;
        }
        return doc.outerHtml();
    }

    static int id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Algorithm algorithm = getArguments().getParcelable("algorithm");

        id++;
        View rootView = inflater.inflate(R.layout.fragment_algorithm, container, false);
        getActivity().getActionBar().setTitle(algorithm.getTitle());

        //final TextView t = (TextView) rootView.findViewById(R.id.textView);
        //t.setText(algorithm.getHtml());

        final WebView wv = (WebView) rootView.findViewById(R.id.webView);

        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setSupportZoom(false);

        final String homeUrl = "file:///android_asset/www/";

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "" + id + "onPageFinished: " + url);
                if (url.equals(homeUrl)) {
                    return true;
                }
                if (url.startsWith(homeUrl)) {
                    String aurl = url.substring(homeUrl.length());
                    Activity act = AlgorithmFragment.this.getActivity();
                    if (act == null) return true;
                    AlgorithmFragment.this.activity.showAlgorithmFragment(aurl);
                } else {
                    Log.d(TAG, "Intent");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                return true;
            }
        });

        algorithm.loadHtml(new IListener() {
            @Override
            public void listen(Document document) {
                wv.loadDataWithBaseURL(homeUrl,
                        decorateHtml(document), "text/html", "utf-8", null);
            }
        });

        return rootView;
    }

    private MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (MainActivity)activity;
        super.onAttach(activity);
    }

    private static String TAG = "AlgorithmFragment.java";
}