import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Broker extends Node {
    // Arrays needed for keeping informations about the clients.
    // All subscribers categorized by the topic.
    public static HashMap<String, ArrayList<ArrayList<String>>> usersPerTopic = new HashMap<>();
    // All messages categorized by the topic.
    public static HashMap<String, ArrayList<MultimediaFile>> messagesPerTopic = new HashMap<>();
    //
    public static final int defaultPort = 4321;

    public static void main(String args[]){
        try {
            Broker broker = new Broker();
            //Random rand = new Random();
            //int port = rand.nextInt(200)+4100;
            int[] availablePorts = {4322,4323,4324};
            int port = availablePorts[Integer.parseInt(args[0])];
            System.out.println("Current port-number: " + port);
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Current ip-number: " + ip);
            Socket connection = new Socket("127.0.0.1", defaultPort);
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            out.writeObject("Broker");
            out.flush();
            out.writeObject(ip);
            out.flush();
            out.writeObject(String.valueOf(port));
            out.flush();
            connection.close();
            broker.connect(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


    public void connect(int port){
        try{
            providerSocket = new ServerSocket(port, 10);
            System.out.println("Server stub is on!");
            while(true){
                acceptedConnection = providerSocket.accept();
                Thread t = new BrokerService(acceptedConnection);
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

