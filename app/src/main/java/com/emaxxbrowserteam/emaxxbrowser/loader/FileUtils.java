package com.emaxxbrowserteam.emaxxbrowser.loader;

import android.util.Log;

import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class FileUtils {

    public static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL exception " + e.toString() + ", url = " + url);
            return null;
        }
    }

    public static void writeDocument(File file, String text) {
        Log.e(TAG, "make cache");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                    Parser.ENCODING));
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
        Log.e(TAG, "input stream to file " + file.getName() + ", " + file.getAbsolutePath());
        FileOutputStream writer;
        try {
            writer = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 40];
            int len = 0;
            while ((len = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                writer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public static void writeDocument(String fileNamePrefix, File cacheDir, File file, Document text) {
        Log.e(TAG, "write document");
        Elements imageElements = text.getElementsByClass("tex");
        for (Element imageElement : imageElements) {
            String attr = imageElement.attr("src");
            String link = Parser.E_MAXX_URL + attr.substring(2);
            String cacheFileName = fileNamePrefix + link.substring(link.lastIndexOf('/') + 1).replace(".png", "");
            File cacheFile = new File(cacheDir, cacheFileName);
            if (link.endsWith(".png")) {
                Log.d(TAG, "cfn: " + cacheFileName);
            }
            if (!cacheFile.exists()) {
                try {
                    cacheFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "    failed to create file: " + e.toString());
                }
            }
            if (cacheFile.exists() && cacheFile.canWrite()) {
                URL url = getURL(link);
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    inputStream2file(cacheFile, inputStream);
                    imageElement.attr("src", "file://" + cacheFile.getAbsolutePath());
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
        Scanner scanner = new Scanner(inputStream, Parser.ENCODING);
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

    public static Document downloadHtmlAndSave(String fileNamePrefix, File cacheDir, URL url, File cache) {
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
            Log.e(TAG, "    ok, not null, start make cache in file " + cache.getName());
            if (cache.exists()) {
                writeDocument(fileNamePrefix, cacheDir, cache, document);
            }
        }
        return document;
    }

    private static final String TAG = "FileUtils.java";

    private FileUtils() {
    }

    public static void clearCache(File cacheDir) {
        for (File file : cacheDir.listFiles()) {
            file.delete();
        }
    }

    public static void clearAlgorithmCache(File cacheDir, Algorithm algorithm) {
        final String title = algorithm.getNameInCache();
        for (File file : cacheDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(title);
            }
        })) {
            file.delete();
        }
    }
}