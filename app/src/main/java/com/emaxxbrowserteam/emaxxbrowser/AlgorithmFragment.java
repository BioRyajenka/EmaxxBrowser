package com.emaxxbrowserteam.emaxxbrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        //Elements els = doc.select("#contents-table");
        //els.remove();
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

        final TextView t = (TextView) rootView.findViewById(R.id.textView);
//        t.setText(algorithm.getHtml());

        algorithm.loadHtml(new IListener() {
            @Override
            public void listen(Document document) {
                t.setText(decorateHtml(document));
            }
        });

        //WebView wv = (WebView) rootView.findViewById(R.id.webView);

        //wv.getSettings().setJavaScriptEnabled(true);
        //wv.loadDataWithBaseURL(null, algorithm.getHtml(), "text/html",
        //        "utf-8", null);

        return rootView;
    }

    private static String TAG = "AlgorithmFragment.java";
}
