package chatclient;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    
    static JPanel      jpanel          = new JPanel     ();
    static JButton     enterChat_butt  = new JButton    ("Enter Chatroom");
    static JLabel      screenName      = new JLabel     ("Username");
    static JTextField  screenNameField = new JTextField ();
    static JFrame      jframe              = new JFrame     ("Chat Room");     
    
    static void displayEnterChatWindow() {
        Image icon = Toolkit.getDefaultToolkit().getImage("Berry.png");     
        jframe.setIconImage(icon);           
        jframe.setVisible(true);        
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        jframe.setSize(600 , 200);
        jframe.setLocationRelativeTo(null);
        jpanel.setLayout(new GridLayout(3,2));
        screenName.setHorizontalAlignment(SwingConstants.CENTER);
        jpanel.add(screenName);
    
        screenNameField.setHorizontalAlignment(JTextField.CENTER);
        screenNameField.setCaretPosition(screenNameField.getText().length() / 2);
    
        jpanel.add(screenNameField);
        enterChat_butt.addActionListener(new ButtonListener());
        jpanel.add(enterChat_butt);
        jframe.add(jpanel);
        jframe.setVisible(true);
    }
    
    
    public static void main(String[] args) {
        
        displayEnterChatWindow();

    }
}

class ButtonListener implements ActionListener {
    
    static Socket       sock;
    static PrintStream  sout;
    static Scanner      sin;
    static JButton      send_butt    = new JButton    ("Send");
    static JButton      leave_butt   = new JButton("Leave Chat");
    static JTextField   messageField = new JTextField ();
    static JTextArea    chatRoom     = new JTextArea  ();
    static JFrame       jframe       = new JFrame     ("Chat Room");
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        JButton jb = (JButton)arg0.getSource();
    
        if (jb == ChatClient.enterChat_butt) {
    
            ChatClient.enterChat_butt.setEnabled(false);
    
            try {
                sock = new Socket("localhost",8888);
                if (sock.isConnected()) {
    
                    sout = new PrintStream(sock.getOutputStream(), true, "UTF-8");
                    sin  = new Scanner(sock.getInputStream(), "UTF-8");
                    
                    Image icon = Toolkit.getDefaultToolkit().getImage("Berry.png");     
                    jframe.setIconImage(icon);           
                    jframe.setVisible(true); 
                    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    jframe.setSize(600 , 600);
                    jframe.setLocationRelativeTo(null);
                    
                    JPanel jpanel1 = new JPanel ();
                    jpanel1.setLayout(new BorderLayout());
                    
                    chatRoom.setEditable(false);
                    sout.print(ChatClient.screenNameField.getText() + "\r\n");
    
                    JPanel jpanel2 = new JPanel ();
                    jpanel2.setLayout(new BorderLayout());
    
                    send_butt.addActionListener(new ButtonListener2());
                    leave_butt.addActionListener(new ButtonListener3());
    
                    jpanel2.add(messageField, BorderLayout.CENTER);
                    jpanel2.add(send_butt, BorderLayout.EAST);
                    jpanel2.add(leave_butt, BorderLayout.SOUTH);
                    jpanel2.setPreferredSize(new Dimension(600,90));
    
                    jpanel1.add(jpanel2,BorderLayout.SOUTH);
                    jpanel1.add(chatRoom, BorderLayout.CENTER);
    
                    jframe.add(jpanel1);
                    jframe.setVisible(true);
    
                    Thread uc = new UpdateChat(sin, chatRoom);
                    uc.start();
                }
    
                messageField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String message = messageField.getText();
                        sout.print(message + "\r\n");
                        messageField.setText("");
                    }
                });
            }  
            catch (UnknownHostException e) {System.out.println("Unknown Host: " + e.toString());}
            catch (IOException ex) {System.out.println("IO Error: " + ex.toString());}
        }
    }
    
}

class ButtonListener2 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
        JButton jb  = (JButton)arg0.getSource();
        String exit = "EXIT";
        if (jb == ButtonListener.send_butt) {
            
            String mess = ButtonListener.messageField.getText();
            ButtonListener.sout.print(mess+"\r\n");
            ButtonListener.messageField.setText("");
            
            if (mess.equals(exit)==true) {
                ButtonListener.jframe.dispose();
                ChatClient.jframe.dispose();
            }   
        }
    }
}

class ButtonListener3 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
        ButtonListener.sout.print("EXIT\r\n");
        ButtonListener.jframe.dispose();
        ChatClient.jframe.dispose();
    }
}

class UpdateChat extends Thread {
    Scanner sin;
    JTextArea chatRoom;
    
    UpdateChat(Scanner sin, JTextArea chatRoom) {
        this.sin = sin;
        this.chatRoom = chatRoom;
    }
    
    @Override
    public void run() {
        while (sin.hasNext()) {
            chatRoom.append(sin.nextLine() + "\r\n");
            chatRoom.setCaretPosition(chatRoom.getDocument().getLength());
        }
    }
}
