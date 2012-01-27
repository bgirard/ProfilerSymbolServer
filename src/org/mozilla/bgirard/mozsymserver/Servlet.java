package org.mozilla.bgirard.mozsymserver;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private SymbolDB db = new SymbolDB();
	
	private String buildid = "firefox-12.0a1-WINNT-20120126171351-profiling-symbols.txt";
	
	public Servlet() throws Exception {
		BuildSymbols sym = DownloadSymbols.GetSymbols("C:\\Users\\bgirard\\Downloads\\firefox-12.0a1.en-US.win32.crashreporter-symbols\\", buildid);
		db.put(buildid, sym);
		System.out.println("Ready");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setBufferSize(10 * 1024 * 1024);
		System.out.println("got request");
		
		SymbolDB dbForRequest = getDB();
		Scanner scanner = new Scanner(req.getInputStream());
		ServletOutputStream writer = resp.getOutputStream();
		int lc = 0;
		writer.println("m-ben_win_sym");
		while( scanner.hasNextLine() ) {
			lc++;
			String line = scanner.nextLine();
			if( line.startsWith("l-") && line.contains("@") ) {
				try {
					String restOfTag = line.substring(2);
					String[] tagData = restOfTag.split("@", 2);
					long address = Long.parseLong(tagData[1],16);
					String library = tagData[0];
					
					Symbol lookup = dbForRequest.lookup(buildid, library, address);
					if( lookup != null ) {
						writer.println("l-" + lookup.name);
						//System.out.println("l-" + lookup.name + " " + lc);
						continue;
					}
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			writer.println(line);
			//System.out.println(line);
		}
		System.out.println("Handled request, "+ lc + " lines.");
		
	}
	
	

	private SymbolDB getDB() {
		//synchronized (this) {
			return db;
		//}
	}

	public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        
        Context context = new Context(server, "/*", Context.SESSIONS);;
        context.setContextPath("/");
        server.setHandler(context);
        
        context.addServlet(new ServletHolder(new Servlet()),"/*");
        
        server.start();
        server.join();
	}

}
