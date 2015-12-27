package com.emaxxbrowserteam.emaxxbrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;
import com.emaxxbrowserteam.emaxxbrowser.loader.Parser;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

        doc.head().append("<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"style" +
                ".css\" />");
        doc.head().append("<script type=\"text/javascript\" src=\"script.js\">");

        Element e = doc.select(".main").first();
        Element div = doc.createElement("div");
        div.addClass("container");
        e.replaceWith(div);
        div.appendChild(e);

        ListIterator<Node> it2 = doc.select(".content").first().childNodes().listIterator();

        int divId = 0;

        /*while (it2.hasNext()) {
            Node h = it2.next();
            Log.d(TAG, "name: " + h.nodeName() + ", doc: " + h.toString());
        }*/

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
            Log.d(TAG, "processing h " + h.toString());
            div = doc.createElement("div");
            div.attr("id", "div" + divId);
            div.attr("style", "display: none;");

            Node ne = null;
            while (it3.hasNext()) {
                ne = it3.next();
                Log.d(TAG, "Found " + ne.nodeName() + " " + ne.toString());
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
        wv.getSettings().setSupportZoom(false);

        wv.setWebViewClient(new WebViewClient() {
            private boolean flag = false;

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e(TAG, "WebView error(" + error.getErrorCode() + "): " + error.getDescription());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e(TAG, "WebView http error(" + errorResponse.getStatusCode() + ")");
            }

            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: " + url);
                if (url.endsWith("#")) {
                    wv.loadUrl(url);
                }
            }
        });

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