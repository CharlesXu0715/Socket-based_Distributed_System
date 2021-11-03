/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */



import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class EchoServerMultiThreaded  {
  
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
       public static void main(String args[]){ 
        ServerSocket listenSocket;
        //int nbr=0;
        //int numero=0;
        //ArrayList<String> userlist=new ArrayList<String>();
        //ArrayList<Socket> socList = new ArrayList<>();
        
  	if (args.length != 1) {
          System.out.println("Usage: java EchoServer <EchoServer port>");
          System.exit(1);
  	}
	try {
		listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
		System.out.println("Server ready..."); 
		while (true) {
			Socket clientSocket = listenSocket.accept();
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String user=br.readLine();
			System.out.println("Connexion from " + clientSocket.getInetAddress()+", port:"+clientSocket.getPort()+" as "+user);
			//socList.add(clientSocket);
			PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
			socOut.println("You have connected as "+user);
			/*for(Socket s : socList) {
				socOut = new PrintStream(s.getOutputStream());
				if (s.getPort() != clientSocket.getPort()) {
					socOut.println(user+" has connected.");
					//continue;
				}
				//socOut.println("OKServer");
			}*/

			//userlist.add(user);
			ClientThread ct = new ClientThread(clientSocket,user);
			ct.start();
			//numero++;
		}
        } catch (Exception e) {
            System.err.println("Error in EchoServerS:" + e);
        }
      }
  }

  
