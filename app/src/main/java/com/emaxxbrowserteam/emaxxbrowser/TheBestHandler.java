package com.emaxxbrowserteam.emaxxbrowser;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Jackson on 26.12.2015.
 */
public class TheBestHandler extends Handler {
    public TheBestHandler() {
        super(Looper.getMainLooper());
        Log.d("Handler.java", "created");
    }

    @Override
    public void handleMessage(Message message) {
        Log.e("Handler.java", "Message received! " + message.what);
        switch (message.what) {
            case 0:
                MainActivity.pd.show();
                break;
            case 1:
                MainActivity.pd.dismiss();
                break;
        }
    }
}
