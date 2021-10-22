///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 3000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        //PrintWriter out = new PrintWriter(remote.getOutputStream());
        BufferedOutputStream out2 = new BufferedOutputStream(remote.getOutputStream());


        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = in.readLine();
        String[] line = null;
        String method = null;
        String fileName = "index.html";
        while (method == null && str != null && !str.equals("")){
          line = str.split(" ",3);
          method = line[0];
          if(line[1].substring(0,1).equals("/") && line[1].substring(1) != "") {
            fileName = line[1].substring(1);
          }


          switch (method) {
            case "GET":
              System.out.println("GET " + fileName);
              getRequest(out2, fileName);
              break;
          
            case "POST":
              System.out.println("POST " + fileName);
              postRequest(in,out2, fileName);
              break;
          
            case "HEAD":
            System.out.println("HEAD " + fileName);
              headRequest(out2, fileName);
              break;
          
            case "PUT":
            System.out.println("PUT " + fileName);
              putRequest(in,out2, fileName);
              break;
          
            case "DELETE":
            System.out.println("DELETE " + fileName);
              deleteRequest(out2, fileName);
              break;
          
            default:
              method = null;
              break;
          }
          if(in.ready()) {
            str = in.readLine();
          }
        }
        remote.close();
        
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  protected void getRequest(BufferedOutputStream out, String fileName) {
    try {
      File file = new File(fileName);
      if(file.exists() && file.isFile()) {
        out.write(header("200 OK", fileName, file.length()).getBytes());
      } else {
        file = new File("error404.html");
        out.write(header("404 Not Found", "error404.html", file.length()).getBytes());
      }
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
      byte[] buffer = new byte[256];
      int nbRead = bis.read(buffer);
      while(nbRead  != -1) {
        out.write(buffer, 0, nbRead);
        nbRead = bis.read(buffer);
      }
      bis.close();
      out.flush();
      
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }

  protected void postRequest(BufferedReader in, BufferedOutputStream out, String fileName) {
    try {
			File file = new File(fileName);
      String header = null;
      if(file.exists()) {
				header = header("200 OK");
			} else {
				header = header("201 Created");
			}
			BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(file, true));

      String line = in.readLine();
      int length = 0;
			while(!line.equals("")) {
        if(line != null && line.contains("Content-Length")) {
          length = Integer.parseInt(line.split(": ",2)[1]);
        }
        line = in.readLine();
		  }
      char[] buffer = new char[length];
      in.read(buffer,0,length);
      String read = new String (buffer);
      bis.write(read.getBytes(),0,length);
			bis.flush();
			bis.close();
      
			out.write(header.getBytes());
			out.flush();
		} catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }

  protected void headRequest(BufferedOutputStream out, String fileName) {
    try {
			File file = new File(fileName);
			if(file.exists() && file.isFile()) {
				out.write(header("200 OK", fileName, file.length()).getBytes());
			} else {
			    out.write(header("404 Not Found").getBytes());
			}
			out.flush();
		} catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }

  protected void putRequest(BufferedReader in, BufferedOutputStream out, String fileName) {
    try {
      File file = new File(fileName);

      String header = null;
      if(file.exists()) {
				header = header("200 OK");
			} else {
				header = header("201 Created");
			}

			BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(file, false));

      String line = in.readLine();
      int length = 0;
			while(!line.equals("")) {
        if(line != null && line.contains("Content-Length")) {
          length = Integer.parseInt(line.split(": ",2)[1]);
        }
        line = in.readLine();
		  }
      char[] buffer = new char[length];
      in.read(buffer,0,length);
      String read = new String (buffer);
      bis.write(read.getBytes(),0,length);
      
			bis.flush();
			bis.close();
			out.write(header.getBytes());
			out.flush();
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }

  protected void deleteRequest(BufferedOutputStream out, String fileName) {
    try {
			File file = new File(fileName);
			
			boolean deleted = false;
			if(file.exists() && file.isFile()) {
				deleted = file.delete();
			}
			if(deleted) {
				out.write(header("204 No Content").getBytes());
			} else if (!file.exists()) {
				out.write(header("404 Not Found").getBytes());
			} else {
				out.write(header("403 Forbidden").getBytes());
			}
			out.flush();
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }

  protected String header(String status) {
    String header = "HTTP/1.0 " + status + "\r\n";
    header += "Server: Bot\r\n";
    // this blank line signals the end of the headers
    header += "\r\n";
    System.out.println("----HEADER----");
		System.out.println(header);
		return header;
  }

  protected String header(String status, String fileName, long length) {
		String header = "HTTP/1.0 " + status + "\r\n";
    // Define Content-Type
		if(fileName.endsWith(".html")) {
			header += "Content-Type: text/html\r\n";
    } else if(fileName.endsWith(".pdf")) {
			header += "Content-Type: application/pdf\r\n";
    } else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpeg")) {
			header += "Content-Type: image/jpg\r\n";
    } else if(fileName.endsWith(".png")) {
			header += "Content-Type: image/png\r\n";
		} else if(fileName.endsWith(".mp4")) {
			header += "Content-Type: video/mp4\r\n";
		} else if(fileName.endsWith(".mp3")) {
			header += "Content-Type: audio/mp3\r\n";
		} else if(fileName.endsWith(".avi")) {
			header += "Content-Type: video/x-msvideo\r\n";
		} else if(fileName.endsWith(".css")) {
			header += "Content-Type: text/css\r\n";
		} else if(fileName.endsWith(".odt")) {
			header += "Content-Type: application/vnd.oasis.opendocument.text\r\n";
    }
		header += "Content-Length: " + length + "\r\n";
		header += "Server: Bot\r\n";
		header += "\r\n";
		System.out.println("----HEADER----");
		System.out.println(header);
		return header;
	}
  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
