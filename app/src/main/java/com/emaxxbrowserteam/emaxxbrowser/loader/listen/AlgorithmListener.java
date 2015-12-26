package com.emaxxbrowserteam.emaxxbrowser.loader.listen;

import android.app.Activity;

import com.emaxxbrowserteam.emaxxbrowser.AlgorithmFragment;

public class AlgorithmListener extends Listener<AlgorithmFragment> {

    public AlgorithmListener(AlgorithmFragment object) {
        super(object);
    }

    @Override
    public Activity getActivity() {
        return object.getActivity();
    }

    @Override
    public void onListen(Object... params) {
        // TODO
    }
}
