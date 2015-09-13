package com.example.recipe.crawler;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/9/15.
 */
public class WebCrawler {
    public static final String TAG = "WebCrawler";

    public static final String URL = "http://allrecipes.co.in/recipe/379/print-friendly.aspx";
    public static final String BASE_URL = "http://allrecipes.co.in/";
    private static WebCrawler sInstance;

    public static WebCrawler getInstance() {
        if (sInstance == null) {
            sInstance = new WebCrawler();
        }

        return sInstance;
    }

    private WebCrawler() {};

    public void startCrawler() {
        WorkerTask task = new WorkerTask();
        task.execute("Worker Task");
    }

    private class WorkerTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                testSample();
//                extractLinks(URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void testSample() {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (doc == null) {
            return;
        }
        Elements elements = doc.getElementsByClass("fullContainer");
        for (Element element : elements) {
            recurseElemet(element);
        }
    }

    public void recurseElemet(Element element){
        Elements elements = element.children();
        for (Element el : elements) {
            List<TextNode> tNodes = el.textNodes();
            for (TextNode tNode : tNodes) {
                printTextNode(tNode);
            }
            recurseElemet(el);
        }
    }

    private boolean isDirtyText(String txt) {
        if (txt.equals("\n")) {
            return true;
        }

        if (txt.equals(" ")) {
            return true;
        }

        if (txt.equals(":")) {
            return true;
        }

        return false;
    }

    private void printTextNode(TextNode tNode){
        if (tNode == null){
            return;
        }

        if (isDirtyText(tNode.text())) {
            return;
        }

        Log.d(TAG, "Node Data |" + tNode.text());
    }

    private boolean isDirtyURL(String url) {
        if (!url.contains(BASE_URL)) {
            return true;
        }

        if (url.contains("#")) {
            return true;
        }

        if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
            return true;
        }

        return false;
    }
    public List<String> extractLinks(String url) throws Exception {
        final ArrayList<String> result = new ArrayList<String>();
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String urlFound = link.attr("abs:href");

            if (isDirtyURL(urlFound)) {
                continue;
            }

            Log.d(TAG, "extractLinks | " + link.attr("abs:href"));
        }
        return result;
    }
}
