import java.net.*;
import java.io.*;

public class GreetingClient {

   public static void main(String [] args) {
      String serverName = "192.168.0.192";
      int port = 4445;
      try {
         System.out.println("Connecting to " + serverName + " on port " + port);
         Socket client = new Socket(serverName, port);
         
         System.out.println("Just connected to " + client.getRemoteSocketAddress());
         OutputStream outToServer = client.getOutputStream();
         DataOutputStream out = new DataOutputStream(outToServer);
         PrintWriter pw = new PrintWriter(outToServer);
         StringBuilder sb = new StringBuilder();
         
         InputStreamReader ism = new InputStreamReader(System.in);
         BufferedReader bf = new BufferedReader(ism);
         while(true){
        	 sb.append(bf.readLine());
        	 pw.write(sb.toString());
        	 pw.flush();
        	 System.out.print("shjgfdsyg");
        	 pw.close();
         }
         
         /*out.writeUTF("Hello from " + client.getLocalSocketAddress());
         InputStream inFromServer = client.getInputStream();
         DataInputStream in = new DataInputStream(inFromServer);
         
         System.out.println("Server says " + in.readUTF());*/
         
         
         //client.close();
      }catch(IOException e) {
         e.printStackTrace();
      }
   }
}