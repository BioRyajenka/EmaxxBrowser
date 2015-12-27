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
        balance = 0;
    }

    private int balance;

    @Override
    public void handleMessage(Message message) {
        Log.e("Handler.java", "Message received! " + message.what);
        balance += message.what * 2 - 1;
        switch (balance) {
            case 0:
                MainActivity.pd.dismiss();
                break;
            default:
                MainActivity.pd.show();
        }
    }
}
