import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;


 // A chat server.
 
public class GreetingServer {

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients connections.
  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

  public static void main(String args[]) {

    // The default port number.
    int portNumber = 2226;
    if (args.length < 1) {
      System.out.println("Usage: Server Started..... <portNumber>\n"
          + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

    
     // Open a server socket on the portNumber 2222
     
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    
     // Create a client socket for each connection and pass it to a new client thread.
     
    try {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String s = br.readLine();
	if (s.equalsIgnoreCase("HELLO BASE_TEST")){
		System.out.println("HELLO BASE_TEST" + "\n" + "IP: " + InetAddress.getLocalHost() + "\n" + "Port: " + portNumber  + "\n" + "Student ID : 17312704");
	}
    } catch (IOException e) {
    	System.out.println(e);
    }
	
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
            break;
          }
        }
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}

class clientThread extends Thread {

  private String clientName = null;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;
    
    try {
      
       //Create input and output streams for this client.
       
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      String name;
      while (true) {
    	os.println("HELO BASE_TEST" + "\n" + "IP: " + InetAddress.getLocalHost() + "\n" + "Port:2222 "  + "\n" + "Student ID:17312704");
    	os.println("Enter your name.");
        name = is.readLine().trim();
        if (name.indexOf('@') == -1) {
          break;
        } else {
          os.println("The name should not contain '@' character.");
        }
      }

      // Welcome the new the client.
      os.println("Welcome " + name
          + " to our chat room.\nTo leave enter quit in a new line.");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = "@" + name;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].os.println("*** A new user " + name
                + " Joined the chat room !!! ***");
          }
        }
      }
      // Start the conversation.
      while (true) {
        String line = is.readLine();
        if (line.startsWith("quit")) {
          break;
        }
        // If the message is private sent it to the given client.
        if (line.startsWith("@")) {
          String[] words = line.split("\\s", 2);
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
              synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(words[0])) {
                    threads[i].os.println("<" + name + "> " + words[1]);
                    
                     // Display this message to let the client know the private message was sent.
                     
                    this.os.println(">" + name + "> " + words[1]);
                    break;
                  }
                }
              }
            }
          }
        } else {
          // The message is public, broadcast it to all other clients.
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].clientName != null) {
                threads[i].os.println("<" + name + "> " + line);
              }
            }
          }
        }
      }
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
            threads[i].os.println("*** The user " + name
                + " left the chat room !!! ***");
          }
        }
      }
      os.println("*** Bye " + name + " ***");
       
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
      
       // Close the output stream, input stream and close the socket.
      
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}
