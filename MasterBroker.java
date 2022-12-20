import java.util.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterBroker extends Broker{
    
    // Arrays needed for keeping informations about the brokers.
    // Array of known ports for 2nd-level brokers.
    public static ArrayList<ArrayList<String>> availableBrokerPorts = new ArrayList<>();
    // A hash map of broker's ports related to their topics.
    public static HashMap<String, Integer> topicsPerBroker = new HashMap<>();
    // An array for the utilization of each broker.
    public static ArrayList<Integer> brokerUtilization = new ArrayList<>();
    //
    public static int nextAvailableBroker = 0;

    
    public static void main(String args[]){
        MasterBroker master = new MasterBroker();
        master.connect(4321);
    }

    public void connect(int port){
        try{
            providerSocket = new ServerSocket(port, 10);
            System.out.println("Server stub is on!");
            while(true){
                acceptedConnection = providerSocket.accept();
                Thread t = new MasterBrokerService(acceptedConnection);
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
