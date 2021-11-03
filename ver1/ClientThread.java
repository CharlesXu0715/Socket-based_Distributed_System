/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */



import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
	private static ArrayList<Socket> socketList = new ArrayList<>();
	private String user;
	BufferedReader socIn = null;
	PrintStream socOut = null;
	
	ClientThread(Socket soc,String u) {
		this.clientSocket = soc;
		socketList.add(soc);
		this.user=u;
		/*for(Socket s : socketList) {
			socOut = new PrintStream(s.getOutputStream());
			if (s.getPort() != clientSocket.getPort()) {
				socOut.println(user+" has connected.");
				//continue;
			}
	
		}*/
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
    		//BufferedReader socIn = null;
    		socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    //from client
    		socOut = new PrintStream(clientSocket.getOutputStream());	//to client
    		
    		String line;
    		String msg;
    		
    		while (true) {
    		  line = socIn.readLine();
    		  if (line==null||line.equals("."))
    		  {
				   System.out.println(user+" has disconnected.");
				   for(Socket s : socketList) {
					socOut = new PrintStream(s.getOutputStream());
	//						System.out.println(msg);
					if (s.getPort() != clientSocket.getPort()) {
						socOut.println(user+" has disconnected.");
						//continue;
					}
				
					}
				   //clientSocket.close();
				   break;
			  }
			  msg = "From " + clientSocket.getInetAddress() + " : " + clientSocket.getPort() + " , says: " + line+" by "+user;
			  for(Socket s : socketList) {
				socOut = new PrintStream(s.getOutputStream());
//						System.out.println(msg);
				if (s.getPort() == clientSocket.getPort()) {
					socOut.println("From yourself, says: " + line);
					//continue;
				}
				else {
					socOut.println(msg);
				}
				
			}
    		  System.out.println("New message: "+msg);
    		  //socOut.println(msg);
    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServerC:" + e); 
        }
       }
  
  }

  
