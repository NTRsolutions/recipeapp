package com.example.recipe.crawler;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 10/9/15.
 */
public class WebCrawler {
    public static final String TAG = "WebCrawler";
//    public static String URL = "http://www.ahomemakersdiary.com/2015/09/chirer-polao-beaten-rice-with-veggies.html";
    public static final String URL = "http://www.vegrecipesofindia.com/palak-paneer-restaurant-style-recipe/";
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
    public void testSample() {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.getElementsByClass("easyrecipe");
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

    private boolean isDirty(String txt) {
        if (txt.equals("\n")) {
            return true;
        }

        if (txt.equals(" ")) {
            return true;
        }

        return false;
    }

    private void printTextNode(TextNode tNode){
        if (tNode == null){
            return;
        }

        if (isDirty(tNode.text())) {
            return;
        }

        Log.d(TAG, "Node Data |" + tNode.text());
    }
    private class WorkerTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            testSample();
            return null;
        }
    }
}
