package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;

import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URL;

public class DownloadTask extends AsyncTask<URL, Void, Document> {

    public DownloadTask(String fileNamePrefix, Activity activity, IListener listener) {
        Log.e(TAG, "filename prefix = " + fileNamePrefix);
        this.fileNamePrefix = fileNamePrefix;
        this.listener = listener;
        this.activity = activity;
    }

    protected final String fileNamePrefix;
    protected IListener listener;
    protected Activity activity;
    protected Document document;

    @Override
    protected Document doInBackground(URL... params) {
        URL url = params[0];
        String name = fileNamePrefix + url.toString().substring(1 + url.toString().lastIndexOf('/'));
        Log.e(TAG, "name = " + name);
        File cacheDir = activity.getCacheDir();
        File savedPage = new File(cacheDir, name);
        if (savedPage.exists() && savedPage.canRead()) {
            Log.e(TAG, "read from cache");
            document = FileUtils.readHtml(savedPage, url.toString());
            return document;
        }
        Log.e(TAG, "start downloading");
        document = FileUtils.downloadHtmlAndSave(fileNamePrefix, cacheDir, url, savedPage);
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
            listener.listen(document);
        } else {
            Toast.makeText(activity, "Problems with internet connection", Toast.LENGTH_SHORT).show();
        }
        sendMessage(1);
    }

    public static final String TAG = "DownloadTask";
}