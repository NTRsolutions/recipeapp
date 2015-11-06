package com.automation.crawler;


import com.automation.crawler.MySQLAccess.COLUMNS;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;


public class ImageDownloader {
	private static int MAX_IMAGE_COUNT_PER_ITEM = 12;
	public static void downloadImageFromUrl(String urlStr, String detination) 
			throws IOException {
	 URL url = new URL(urlStr);

        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(4000);
        System.out.println("Download start for : " + urlStr);
	 InputStream in = new BufferedInputStream(connection.getInputStream());

	 ByteArrayOutputStream out = new ByteArrayOutputStream();
	 byte[] buf = new byte[2048];
	 int n = 0;
	 while (-1!=(n=in.read(buf)))
	 {
	    out.write(buf, 0, n);
	 }
	 out.close();
	 in.close();
	 byte[] response = out.toByteArray();
	 
	 File file = new File(detination);
	 if (!file.exists()) {
		 file.createNewFile();
	 }
	 FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
	 fos.write(response);
	 fos.close();
        System.out.println("Download Complete for : " + urlStr);
	 
	}
	
	public static void main(String args[]) {
		int batch_size_to_process = 5000;
		MySQLAccess mDatabaseManager = new MySQLAccess();
		String whereClause = " where title is not null and image_downloaded = 0 ";
		try {
			ArrayList<RecipeInfo> list = mDatabaseManager.readDataBase(batch_size_to_process, whereClause);
			for (RecipeInfo info : list) {
				ImageDownloader imageDownloader = new ImageDownloader();
				int hashCode = info.hash;
				String query = info.title;
				imageDownloader.downloadImageForItem(hashCode + "", query, imageDownloader);
				HashMap<String, String> updateQueryMap = new HashMap<>();
				updateQueryMap.put(COLUMNS.IMAGE_DOWNLOADED.toString().toLowerCase(), 1+"");
				mDatabaseManager.updateInDb(hashCode, updateQueryMap);
                System.out.println("Task Completed for " + info.hash + " : " + info.title );
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void downloadImageForItem(String id, String query, ImageDownloader imageDownloader) {
		String url = "http://www.bing.com/images/search?q=" + query;
		ArrayList<String> imageUrlList = new ArrayList<>();
		
		ArrayList<String> urlList = CrawlerUtility.getUrlList(url);
		for (String str : urlList) {
			if (str.endsWith(".jpg") || str.endsWith(".jpeg")) {
				imageUrlList.add(str);
			}
		}
		
		String destinationBasePath = "~/Desktop/file_server/" + id + "/";
		 File file = new File(destinationBasePath);
		 if (!file.exists()) {
			 file.mkdirs();
		 }
		 
		int counter = 0;
		for(String imageUrl : imageUrlList) {
			try {
				String destination = destinationBasePath + counter + "";

                try {
                    ImageDownloader.downloadImageFromUrl(imageUrl, destination);
                }catch (Exception ex) {
                    continue;
                }

				String type = imageDownloader.identifyFileTypeUsingFilesProbeContentType(destination);
                if (type == null) {
                    continue;
                }
				System.out.println("Type : " + type);
				if (!type.contains("image")) {
					File toDelete = new File(destination);
					toDelete.delete();
					continue;
				}
				counter ++;
				if (counter > MAX_IMAGE_COUNT_PER_ITEM) {
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String identifyFileTypeUsingFilesProbeContentType(final String fileName)
	{
	   String fileType = "Undetermined";
	   final File file = new File(fileName);
	   try
	   {
	      fileType = Files.probeContentType(file.toPath());
	   }
	   catch (IOException ioException)
	   {
	      System.out.println(
	           "ERROR: Unable to determine file type for " + fileName
	              + " due to exception " + ioException);
	   }
	   return fileType;
	}
	
}
