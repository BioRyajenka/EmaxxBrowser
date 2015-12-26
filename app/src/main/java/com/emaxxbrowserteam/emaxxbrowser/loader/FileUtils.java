package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public final class FileUtils {

    public static void writeDocument(File file, Document text) {
        Log.e(TAG, "make cache");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                    "cp1251"));
            writer.println(text.outerHtml());
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static Document readHtml(File file, String url) {
        try {
            Log.e(TAG, "read html " + file.toString());
            return scanner2document(new FileInputStream(file));
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    private static Document scanner2document(InputStream inputStream) throws IOException {
        StringBuilder html = new StringBuilder();
        Scanner scanner = new Scanner(inputStream, "cp1251");
        while (scanner.hasNextLine()) {
            html.append(scanner.nextLine());
            html.append('\n');
        }
        return Jsoup.parse(html.toString());
    }

    public static Document downloadHtml(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return scanner2document(connection.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    public static Document downloadHtmlAndSave(URL url, File cache) {
        Document document = downloadHtml(url);
        Log.e(TAG, "downloaded");
        if (document != null) {
            if (!cache.exists()) {
                try {
                    cache.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "cant make cache file");
                }
            }
            Log.e(TAG, "    ok, not null, start make cache");
            if (cache.exists()) {
                writeDocument(cache, document);
            }
        }
        return document;
    }

    private static final String TAG = "FileUtils";

    private FileUtils() {
    }
}