package com.automation.crawler;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerUtility {

	public static ArrayList<String> getUrlList(String url) {
		ArrayList<String> list = new ArrayList<>();
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(10 * 1000).get();
		} catch (Exception e) {
			return list;
		}

		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String urlFound = link.attr("abs:href");
			list.add(urlFound);
		}

		return list;
	}
	
    public static String loadJSONFromFile(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();
        String aDataRow = "";
        try {
            File myFile = new File(filePath);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));

            while ((aDataRow = myReader.readLine()) != null) {
                stringBuffer.append(aDataRow + "\n");
            }
            myReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();

    }
	
}
