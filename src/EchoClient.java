/*
 * File: EchoClient.java
 * Author: Java, Java, Java
 * Description: This class defines the client object of
 *  a simple client/server application. The application
 *  sets up a socket connection between the client and
 *  server and simply echos strings input by the user. 
 *  The client object serves as the user interface. It
 *  accepts user input and sends it to the server.
 */

import java.net.*;
import java.io.*;

public class EchoClient extends ClientServer {

     private TicTacToe_Application myApp;
     protected Socket socket;
     private String userStr = "";  // user input string
     
     
     /**
      *  EchoClient() constructor creates a client object
      *   given the URL and port number of a server that this
      *   client will connect to
      *  @param url -- a String giving the server's URL
      *  @param url -- an int giving the server's port number
      */
     public EchoClient(String url, int port, TicTacToe_Application ap) {
         try {
             myApp = ap;
             socket = new Socket(url, port);
             System.out.println("CLIENT: connected to " + url + ":" + port);
         } catch (Exception e) {
             e.printStackTrace();
             System.exit(1);
         }
     } // EchoClient()

     /**
      *  run() defines the client thread's main behavior which is
      *   simply to request service from its server. Since an I/O
      *   exception may result, it is handled here.
      */
     public void run() {
         try {
             requestService(socket);
             socket.close();
             System.out.println("CLIENT: connection closed");
         } catch (IOException e) { 
             System.out.println(e.getMessage());
             e.printStackTrace(); 
         }
     } // run()

    /**
     *  requestService() implements the details of the service
     *   request. In this case it accepts a line of input from
     *   the user and passes it along to the server. The protocol
     *   with the server requires that the server say "Hello" first.
     *  @param socket -- the Socket connection to the server
     */
    protected void requestService(Socket socket) throws IOException { 
        String servStr = readFromSocket(socket);          // Check for "Hello"
        myApp.updateBoard(servStr);
        System.out.println("SERVER: " + servStr);         // Report the server's response
        System.out.println("CLIENT: type a line or 'goodbye' to quit"); // Prompt the user

         do {
            if (servStr.substring(0,2).equals("XX") || servStr.substring(0,2).equals("OO")) // add flag to indicate use selected an item
            {
                userStr = getPlayerSelection();                   // Get selection from user
                // get updated from app
                writeToSocket(socket, userStr + "\n");          // Send it to server
                servStr = readFromSocket(socket);               // Read the server's response
                System.out.println("SERVER: " + servStr);       // Report the server's response
                myApp.updateBoard(servStr);                    // Update game board
            } 
            
            if (servStr.substring(0,2).equals("XO") || servStr.substring(0,2).equals("OX"))
            {
                servStr = readFromSocket(socket);               // Read the server's response
                System.out.println("SERVER: " + servStr);       // Report the server's response   
                myApp.updateBoard(servStr);                    // Update game board
            }
                
         } while (!userStr.toLowerCase().equals("goodbye")); // Until user says 'goodbye'
        
    } // requestService()


    
    public synchronized void playerMadeSelection(String s )  
    {
         userStr = s;
         notify();
    } // playerMadeSelection()



    public synchronized String getPlayerSelection( )  
    {
        try{
            wait();
        }
        catch (InterruptedException e)
        {
            System.out.println("Exception " + e.getMessage());
        }
        finally
        {
            return userStr;
        }
    } // getPlayerSelection()

} // EchoClient
