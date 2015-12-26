package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.emaxxbrowserteam.emaxxbrowser.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class FileUtils {

    public static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL exception " + e.toString());
            Log.d(TAG, "url is " + url);
            return null;
        }
    }

    public static void writeDocument(File file, String text) {
        Log.e(TAG, "make cache");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                    "cp1251"));
            writer.println(text);
            Log.e(TAG, "cache was made");
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void inputStream2file(File file, InputStream inputStream) {
        Log.e(TAG, "input stream to file " + file.getName());
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file),
                    "cp1251");
            byte[] buffer = new byte[1024 * 40];
            int len = 0;
            while ((len = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                for (int i = 0; i < len; i++) {
                    writer.write(buffer[i]);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public static void writeDocument(File cacheDir, File file, Document text) {
        Log.e(TAG, "write document");
        Elements imageElements = text.getElementsByClass("tex");
        for (Element imageElement : imageElements) {
            String attr = imageElement.attr("src");
            String link = Parser.E_MAXX_URL + attr.substring(2);
            Log.e(TAG, "    " + link);
            String cacheFileName = link.substring(link.lastIndexOf('/') + 1).replace(".png", "");
//            Log.e(TAG, "dir = " + cacheDir.getAbsolutePath() + ", " + (cacheDir.exists() ? "Exists" : "Not exists"));
            File cacheFile = new File(cacheDir, cacheFileName);
//            Log.e(TAG, "file = " + cacheFile.getAbsolutePath() + ", " + (cacheFile.exists() ? "Exists" : "Not exists"));
            if (!cacheFile.exists()) {
                try {
//                    Log.e(TAG, "    create file " + cacheFile.getName());
                    cacheFile.createNewFile();
//                    Log.e(TAG, "    created file " + cacheFile.getName() + ", " + (created ? "created" : "not created"));
                } catch (IOException e) {
                    Log.e(TAG, "    failed to create file: " + e.toString());
                }
            }
            if (cacheFile.exists() && cacheFile.canWrite()) {
//                Log.e(TAG, "    file exists " + cacheFile.getName());
                URL url = getURL(link);
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    inputStream2file(cacheFile, inputStream);
                    imageElement.attr(cacheFile.getAbsolutePath());
                } catch (IOException ignored) {}
            }
        }
        
        writeDocument(file, text.outerHtml());
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
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            return scanner2document(connection.getInputStream());
        } catch (IOException e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static Document downloadHtmlAndSave(File cacheDir, URL url, File cache) {
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
            Log.e(TAG, "    ok, not null, start make cache in file " + cache.getName()  );
            if (cache.exists()) {
                writeDocument(cacheDir, cache, document);
            }
        }
        return document;
    }

    private static final String TAG = "FileUtils";

    private FileUtils() {
    }
}