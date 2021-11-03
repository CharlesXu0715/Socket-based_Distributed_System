
import java.io.*;

public class HistoryHandler {

    /**
     * read all the histories saved at the local .txt file
     * @return the String that contains all histories
     */
    public static String readAll(String inetAddress,int port){
        File history = new File("UDPHistory.txt");
        BufferedReader reader = null;
        StringBuffer allMsg = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(history));
            String oneLine = null;
            while ((oneLine = reader.readLine()) != null) {
                String[] tokens = oneLine.split(";",3);
                if ((tokens[0].equals(inetAddress)) && (tokens[1].equals(Integer.toString(port)))){
                    allMsg.append(tokens[2] + "\n");
                }
            }
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return allMsg.toString();
    }

    /**
     * write one line of message into the history file
     * @param msg one message sent by one user
     */
    public static void writeAMessage(String inetAddress,int port,String msg) {
        try{
            FileWriter writer = new FileWriter("UDPHistory.txt",true);
            String towrite=inetAddress+";"+Integer.toString(port)+";"+msg;
            writer.write(towrite + "\n");
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
