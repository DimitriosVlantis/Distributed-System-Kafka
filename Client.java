import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Client extends Node {
    public static int serverStubPort;
    public static final int defaultPort = 4321;
    public static String profileName;
    // All messages categorized by the topic.
    public static HashMap<String, ArrayList<MultimediaFile>> messagesPerTopic = new HashMap<>();
    
    public static void main(String[] args){
        try{
            Client client = new Client();
            client.requestedConnection = new Socket("127.0.0.1", defaultPort);
            int[] availablePorts = {4325,4326,4327};
            String ip = InetAddress.getLocalHost().getHostAddress();
            Client.serverStubPort = availablePorts[Integer.parseInt(args[0])];
            Thread t = new ClientActions(client.requestedConnection, ip, serverStubPort);
            t.start();
            client.connect(serverStubPort);
        }
        catch (IOException ioException) {
			ioException.printStackTrace();
		} 
    }

    public void connect(int port){
        try{
            providerSocket = new ServerSocket(port, 10);
            System.out.println("Server stub is on!");
            while(true){
                acceptedConnection = providerSocket.accept();
                Thread t = new ClientService(acceptedConnection);
                t.start();
            }
        }
        catch (IOException ioException) {
			ioException.printStackTrace();
		} 
        finally {
			disconnect();
        }
    }

    public void disconnect(){
        try{
            providerSocket.close();
        }
        catch (IOException ioException) {
			ioException.printStackTrace();
		}
    }
}
