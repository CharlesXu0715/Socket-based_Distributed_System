/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */


import java.io.*;
import java.net.*;



public class EchoClient {

 
  /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;
        String user="";

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
      	    stdIn = new BufferedReader(new InputStreamReader(System.in));		//clavier
      	    System.out.println("Username?");
      	    user=stdIn.readLine();
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
	    socIn = new BufferedReader(
	    		          new InputStreamReader(echoSocket.getInputStream()));    //from server
	    socOut= new PrintStream(echoSocket.getOutputStream());				//to server

	    socOut.println(user);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
                             
        String line;
        while (true) {
        	/*line=stdIn.readLine();
        	if (line.equals(".")||line==null) {
				//socOut.println("Disconnect");
				System.out.println("Disconnected!");
				break;
			}
			//socOut.println(user);
        	socOut.println(line);
        	if(socIn.ready()) {
                line = socIn.readLine();
                System.out.println(line);
            }*/
            
            if(stdIn.ready()) {
                line=stdIn.readLine();
                if (line.equals(".")){
					System.out.println("Disconnected!");
					break;
				}

                socOut.println(line);
            }

            if(socIn.ready()) {
                line = socIn.readLine();
                System.out.println(line);
            }
        }
      socOut.close();
      socIn.close();
      stdIn.close();
      echoSocket.close();
    }
}


