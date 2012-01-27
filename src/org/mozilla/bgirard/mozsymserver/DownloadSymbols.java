package org.mozilla.bgirard.mozsymserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class DownloadSymbols {

	
	public static BuildSymbols GetSymbols(String base, String index) throws Exception {
		String downloadString = downloadString(base + index);
		Scanner scanner = new Scanner(downloadString);
		
		BuildSymbols buildSymbols = new BuildSymbols();
		while( scanner.hasNextLine() ) {
			String line = scanner.nextLine();
			try {
				if( !line.endsWith(".sym") ) continue;
				if( line.contains("\\") ) continue;
				
				String[] urlPart = line.split("\\/");
				String fileName = urlPart[urlPart.length-1];
				String libraryName = fileName.substring(0, fileName.length()-4);
				if( line.startsWith("test") ) continue;
				
				System.out.println("Downloading: " + fileName + ", " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024 + " MBs");
				LibrarySymbol libSymb = downloadSym(base + line);
				
				buildSymbols.put(libraryName, libSymb);
			} catch (Exception e) {
				System.out.println("Error, skipping: " + line);
				e.printStackTrace();
			}
		}
		
		return buildSymbols;
	}
	
	public static LibrarySymbol downloadSym(String urlStr) throws Exception {
		/*
		 * Get a connection to the URL and start up
		 * a buffered reader.
		 */
		//URL url = new URL(urlStr);
		//url.openConnection();
		//Scanner scanner = new Scanner(url.openStream());
		Scanner scanner = new Scanner(new File(urlStr));
		
		LibrarySymbol sym = new LibrarySymbol(scanner);

		return sym;
	}

	public static String downloadString(String urlStr) throws Exception {
		/*
		 * Get a connection to the URL and start up
		 * a buffered reader.
		 */
		//URL url = new URL(urlStr);
		//url.openConnection();
		//InputStream reader = url.openStream();
		InputStream reader = new FileInputStream(new File(urlStr));

		/*
		 * Setup a buffered file writer to write
		 * out what we read from the website.
		 */
		byte[] buffer = new byte[153600];
		int bytesRead = 0;

		StringBuilder builder = new StringBuilder();
		while ((bytesRead = reader.read(buffer)) > 0)
		{  
			builder.append(new String(buffer, 0, bytesRead));
		}

		reader.close();
		return builder.toString();
	}

	public static void downloadFile(String urlStr, String file) {
		try {
			/*
			 * Get a connection to the URL and start up
			 * a buffered reader.
			 */
			long startTime = System.currentTimeMillis();

			URL url = new URL(urlStr);
			url.openConnection();
			InputStream reader = url.openStream();

			/*
			 * Setup a buffered file writer to write
			 * out what we read from the website.
			 */
			FileOutputStream writer = new FileOutputStream(file);
			byte[] buffer = new byte[153600];
			int totalBytesRead = 0;
			int bytesRead = 0;

			System.out.println("Download: " + file);

			while ((bytesRead = reader.read(buffer)) > 0)
			{  
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[153600];
				totalBytesRead += bytesRead;
			}

			long endTime = System.currentTimeMillis();

			System.out.println("Done. " + (new Integer(totalBytesRead).toString()) + " bytes read (" + (new Long(endTime - startTime).toString()) + " millseconds).");
			writer.close();
			reader.close();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		GetSymbols("http://symbols.mozilla.org/firefox/", "firefox-12.0a1-WINNT-20120126040205-profiling-symbols.txt");
		System.out.println(System.currentTimeMillis() - start);
		//downloadFile("http://symbols.mozilla.org/firefox/firefox-12.0a1-WINNT-20120126040205-profiling-symbols.txt", "index.txt");
	}
}
