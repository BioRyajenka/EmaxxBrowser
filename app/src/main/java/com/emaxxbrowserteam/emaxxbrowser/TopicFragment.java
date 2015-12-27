package com.emaxxbrowserteam.emaxxbrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

/**
 * Created by Jackson on 26.12.2015.
 */
public class TopicFragment extends Fragment {
    @Deprecated
    public TopicFragment() {
    }

    public static TopicFragment newInstance(Topic topic) {
        TopicFragment myFragment = new TopicFragment();

        Bundle args = new Bundle();
        args.putParcelable("topic", topic);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Topic topic = getArguments().getParcelable("topic");
        Log.d(TAG, "ocv: topic is " + topic);

        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.topicLinearLayout);
//        TextView textView = (TextView) rootView.findViewById(R.id.textLabel);
//        textView.setText(topic.getTitle() + " desc");
        Log.d(TAG, "act is " + getActivity());
        Log.d(TAG, "bar is " + getActivity().getActionBar());
        getActivity().getActionBar().setTitle(topic.getTitle());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < topic.getAlgorithmsCount(); i++) {
            final Algorithm algorithm = topic.getAlgorithm(i);
            Button myButton = new Button(getActivity());
            myButton.setLayoutParams(params);
            myButton.setText(algorithm.getTitle());
            final int fi = i;
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Clicked " + fi);
                    ((MainActivity)getActivity()).replaceFragment(AlgorithmFragment.newInstance(algorithm));
                }
            });
            layout.addView(myButton);
        }
        return rootView;
    }

    private static String TAG = "TopicFragment.java";
}
