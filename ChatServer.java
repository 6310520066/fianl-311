package chatserver;

import java.io.*;
import java.net.*;
import java.util.*;


public class ChatServer {
    static int portNum = 8888;
    static List<Socket> clientList = new ArrayList();
    
    public static void main(String[] args) throws IOException {
        ServerSocket sock = null;
        
        try {
            sock = new ServerSocket(portNum);
            System.out.println("Listening socket is setup, now accepting connections on port " + portNum);
            
            while(true) {
                Socket client = sock.accept();
                System.out.println("Got a Connection from " + client.getInetAddress().toString() + " on port " + portNum);
                new ProcessConnection(client).start();
            }
        }
        
        catch (IOException e) { System.out.println("IO Error: " + e.toString()); }   
        
        if (sock != null) {
            sock.close();
        }
    }
}

class ProcessConnection extends Thread {
    Socket client;
    
    ProcessConnection(Socket newClient) { client = newClient; }
    
    String getScreenName(Scanner sin) {
        return sin.nextLine();
    }
    
    void addToChat(String screenName, PrintStream sout) {
        try {
            ChatServer.clientList.add(client);
            System.out.println(screenName + " has entered the chat");
            
            for (int i = 0; i<ChatServer.clientList.size(); i++) {
                PrintStream sendOut = new PrintStream (ChatServer.clientList.get(i).getOutputStream());
                sendOut.print(screenName + " has entered the chat" + "\r\n");
            }
        }
        catch (IOException e) { System.out.println("IO Error: " + e.toString()); }
    }
    
    void processMessages(String screenName, Scanner sin) {
        try{
            String message = "";
            while(!message.equalsIgnoreCase("EXIT")) {
                message = sin.nextLine();
                for (int i = 0; i<ChatServer.clientList.size(); i++) {
                    PrintStream sendOut = new PrintStream (ChatServer.clientList.get(i).getOutputStream());
                    sendOut.print(screenName + ": " + message + "\r\n");
                }
            }
        }
        catch (IOException e) { System.out.println("IO Error: " + e.toString()); }
    }
    
    void leaveChat (String screenName) {
        try {
            System.out.println(screenName + " has left the chat");
            for (int i = 0; i<ChatServer.clientList.size(); i++) {
                PrintStream sendOut = new PrintStream (ChatServer.clientList.get(i).getOutputStream());
                sendOut.print(screenName + " has left the chat" + "\r\n");
            }
            ChatServer.clientList.remove(client);
            client.close();
        }
        catch (IOException e) { System.out.println("IO Error: " + e.toString()); }
    }
    
    @Override
    public void run() {
        
        try{
            
            PrintStream sout = new PrintStream (client.getOutputStream());
            Scanner     sin  = new Scanner     (client.getInputStream());
            
            String screenName = getScreenName(sin);
            
            addToChat(screenName, sout);
            
            processMessages(screenName, sin);
            
            leaveChat(screenName);

        }
        catch (IOException e) { System.out.println("IO Error: " + e.toString()); }

    }
}