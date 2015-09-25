package com.example.recipe.crawler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.recipe.crawler.MySQLAccess.COLUMNS;

public class ImageDownloader {
	private static int MAX_IMAGE_COUNT_PER_ITEM = 8;
	public static void downloadImageFromUrl(String urlStr, String detination) 
			throws IOException {
	 URL url = new URL(urlStr);
	 InputStream in = new BufferedInputStream(url.openStream());
	 
	 ByteArrayOutputStream out = new ByteArrayOutputStream();
	 byte[] buf = new byte[1024];
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
	 
	}
	
	public static void main(String args[]) {
		int batch_size_to_process = 3500;
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
		
		String destinationBasePath = "/home/rajnish/Desktop/file_server/" + id + "/";
		 File file = new File(destinationBasePath);
		 if (!file.exists()) {
			 file.mkdirs();
		 }
		 
		int counter = 0;
		for(String imageUrl : imageUrlList) {
			try {
				String destination = destinationBasePath + counter + "";
				ImageDownloader.downloadImageFromUrl(imageUrl, destination);
				String type = imageDownloader.identifyFileTypeUsingFilesProbeContentType(destination);
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
			} catch (IOException e) {
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
