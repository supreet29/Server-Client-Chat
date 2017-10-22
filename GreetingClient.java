import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GreetingClient {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chat Room");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    int port = 9002;

    public GreetingClient() {

        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    
     // Prompt for and return the address of the server.
    
    private String getServerAddress() {
        String answer =  JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chat Room",
            JOptionPane.QUESTION_MESSAGE);
        return answer;
    }

    
     // Prompt for and return the desired screen name.
     
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }


    private void run() throws IOException {

        // Make connection
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, port);
        System.out.println("Connecting to " + serverAddress + " on port " + port);
        System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
               // if(line.split(" ")[2].equalsIgnoreCase("hello base_test")){
                //	messageArea.append("HELLO MATE" + "\n");
                //}
            } 
        }
    }

    
     //Runs the client as an application with a closeable frame.
     
    public static void main(String[] args) throws Exception {
    	GreetingClient client = new GreetingClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}