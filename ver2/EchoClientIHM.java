import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class EchoClientIHM extends JFrame implements MessageListener{
    private TextArea allMsgsArea = new TextArea();
    private TextField groupIPField = new TextField();
    private TextField groupPortField = new TextField();
    private TextField msgField = new TextField();
    private TextField nicknameField = new TextField();

    private Button join = new Button("join chat");
//    private Button leave = new Button("leave");
    private Button send = new Button("send");

    private JPanel upper = new JPanel();
    private JPanel middle = new JPanel();
    private JPanel lower = new JPanel();

    private String currentInetAddress;
    private int currentPort;
    private boolean onConnection = false;

    Socket echoSocket = null;
    PrintStream socOut = null;      //to Server
    //BufferedReader stdIn = null;        //clavier
    BufferedReader socIn = null;        //from Server
    String user="";
    private ClientListenThread clientListenThread;

    public EchoClientIHM(){
        setTitle("multicast Chat Client");
        setSize(640,480);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //components
        groupIPField.setText("127.0.0.1");
        groupPortField.setText("1234");
        nicknameField.setText("Somebody");
        nicknameField.setPreferredSize(new Dimension(120, 24));
        send.setPreferredSize(new Dimension(80, 24));

        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                join();
            }
        });

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        send.setEnabled(false);
        // page payout
        upper.setLayout(new GridLayout(1, 6, 5, 5));
        upper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        upper.add(new JLabel("Group IP:"));
        upper.add(groupIPField);
        upper.add(new JLabel("Group port:"));
        upper.add(groupPortField);
        upper.add(join);
        this.add(upper, BorderLayout.NORTH);

        middle.setLayout(new BorderLayout());
        middle.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        middle.add(allMsgsArea);
        this.add(middle, BorderLayout.CENTER);

        lower.setLayout(new BorderLayout());
        lower.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        lower.add(nicknameField, BorderLayout.WEST);
        lower.add(msgField, BorderLayout.CENTER);
        lower.add(send, BorderLayout.EAST);
        this.add(lower, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private synchronized void join() {
        try{
            if (onConnection) {
                /*String quitText = nicknameField.getText() + " leaves the chat.";
                DatagramPacket msg = new DatagramPacket(quitText.getBytes(),quitText.length(), InetAddress.getByName(currentInetAddress), currentPort);
                mcSocket.send(msg);*/
                allMsgsArea.append("You are already connected.\n");
            }
            else{
                File historyfile = new File("history.txt");
                if (historyfile.exists()) {
                    String history = HistoryHandler.readAll();
                    allMsgsArea.setText(history);
                    allMsgsArea.append("/*****Above are all history messages*****/"+"\n");
                }
                // connection
                //String groupAddr = groupIPField.getText();
                currentInetAddress = groupIPField.getText();
                currentPort = Integer.valueOf(groupPortField.getText());
                user=nicknameField.getText();
                echoSocket = new Socket(currentInetAddress,currentPort);
                System.out.println(currentInetAddress);
                
                /*String connectedInfo = nicknameField.getText() + " joins the chat of " + groupIPField.getText() + " : " + currentPort;
                DatagramPacket enteringMsg = new DatagramPacket(connectedInfo.getBytes(), connectedInfo.length(), groupAddr, groupPort);
                mcSocket.send(enteringMsg);*/
                //String connectionInfo=currentInetAddress+";"+currentPort+";"+user;
                socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                socOut= new PrintStream(echoSocket.getOutputStream());
                socOut.println(user);
                //System.out.println("OKClient");
                /*String line = socIn.readLine();     //"OKOK"
                allMsgsArea.append(line + '\n');*/
    
                clientListenThread = new ClientListenThread(currentInetAddress + ":" + currentPort, echoSocket, this);
                clientListenThread.start();
                onConnection = true;
                send.setEnabled(true);
            }

            

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + groupIPField.getText());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:"+ groupIPField.getText());
            System.exit(1);
        }
    }

    @Override
    public void onReceiveMessage(ClientListenThread clientListenThread, String msg) {
        allMsgsArea.append(msg + '\n');
    }

    private synchronized void send() {
        //String msg=nicknameField.getText() + " says: " + msgField.getText();
        String msg=msgField.getText();
        //System.out.println(msg);
        try {
            if (msg.equals(".")){      //disconnect
                //clientListenThread.stop();
                allMsgsArea.append("You have disconnected." + '\n');
                onConnection = false;
                send.setEnabled(false);
            }
            socOut.println(msg);
            msgField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        new EchoClientIHM();
    }

}
