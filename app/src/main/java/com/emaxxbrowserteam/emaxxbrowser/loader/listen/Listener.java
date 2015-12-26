package com.emaxxbrowserteam.emaxxbrowser.loader.listen;

import android.app.Activity;

abstract public class Listener<T> {

    protected T object;

    public Listener(T object) {
        this.object = object;
    }

    public abstract Activity getActivity();

    public abstract void onListen(Object... params);
}
