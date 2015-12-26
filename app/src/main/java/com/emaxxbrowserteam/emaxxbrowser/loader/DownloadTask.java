package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;
import com.emaxxbrowserteam.emaxxbrowser.loader.listen.Listener;

import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URL;

public class DownloadTask <T extends Listener> extends AsyncTask<URL, Void, Document> {

    public DownloadTask(T listener) {
        this.listener = listener;
    }

    protected T listener;
    protected Document document;

    @Override
    protected Document doInBackground(URL... params) {
        URL url = params[0];
        String name = url.toString().substring(1 + url.toString().lastIndexOf('/'));
        Log.e(TAG, "name = " + name);
        File cacheDir = listener.getActivity().getCacheDir();
        File savedPage = new File(cacheDir, name);
        if (savedPage.exists()) {
            savedPage.delete();
        }
        if (savedPage.exists() && savedPage.canRead()) {
            Log.e(TAG, "read from cache");
            document = FileUtils.readHtml(savedPage, url.toString());
            return document;
        }
        Log.e(TAG, "start downloading");
        document = FileUtils.downloadHtmlAndSave(url, savedPage);
        Log.e(TAG, "downloaded");
        return document;
    }

    @Override
    protected void onPreExecute() {
        sendMessage(0);
    }

    protected void sendMessage(int n) {
        Handler handler = MainActivity.handler;
        Message message = handler.obtainMessage(n);
        message.sendToTarget();
    }

    @Override
    protected void onPostExecute(Document ignored) {
        if (document != null) {
            listener.onListen(document);
        } else {
            Toast.makeText(listener.getActivity(), "Problems with internet connection", Toast.LENGTH_SHORT).show();
        }
        sendMessage(1);
    }

    public static final String TAG = "DownloadTask";
}